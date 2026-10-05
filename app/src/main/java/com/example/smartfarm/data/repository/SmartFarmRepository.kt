package com.example.smartfarm.data.repository

import com.example.smartfarm.data.local.SmartFarmDao
import com.example.smartfarm.data.local.SmartFarmDatabase
import com.example.smartfarm.data.model.*
import com.example.smartfarm.domain.CommissionCalculator
import com.example.smartfarm.domain.DemoDiseaseAdapter
import com.example.smartfarm.domain.ProduceOrderStateMachine
import com.example.smartfarm.domain.TreatmentFeeCalculator
import kotlinx.coroutines.flow.*
import java.util.UUID

class SmartFarmRepository(private val dao: SmartFarmDao) {

    private val diseaseAdapter = DemoDiseaseAdapter()

    // Active User State for session simulation
    private val _currentUser = MutableStateFlow<UserEntity>(
        UserEntity(
            id = "farmer_musa",
            role = UserRole.FARMER,
            name = "Musa Ibrahim",
            phone = "+234 803 000 1122",
            email = "musa.farmer@smartfarm.demo",
            state = "Kogi",
            lga = "Dekina",
            community = "Anyigba",
            consentGiven = true,
            locationConsent = false,
            isDemo = true
        )
    )
    val currentUser: StateFlow<UserEntity> = _currentUser.asStateFlow()

    fun switchUser(user: UserEntity) {
        _currentUser.value = user
    }

    // --- Users ---
    val allUsers: Flow<List<UserEntity>> = dao.getAllUsers()

    suspend fun saveUser(user: UserEntity) {
        dao.insertUser(user)
        if (_currentUser.value.id == user.id) {
            _currentUser.value = user
        }
    }

    // --- Disease Screening ---
    val diseaseCasesForCurrentFarmer: Flow<List<DiseaseCaseEntity>> = _currentUser.flatMapLatest { user ->
        dao.getDiseaseCasesForFarmer(user.id)
    }

    val allDiseaseCases: Flow<List<DiseaseCaseEntity>> = dao.getAllDiseaseCases()

    suspend fun runDiseaseScreening(
        farmerId: String,
        imageNameOrUri: String,
        isConsentGiven: Boolean
    ): DiseaseCaseEntity {
        val analysis = diseaseAdapter.analyse(imageNameOrUri)
        val caseId = "CASE-${UUID.randomUUID().toString().take(8).uppercase()}"

        val caseEntity = DiseaseCaseEntity(
            id = caseId,
            farmerId = farmerId,
            createdAt = System.currentTimeMillis(),
            adapterId = diseaseAdapter.id,
            label = analysis.label,
            confidence = analysis.confidence,
            confidenceGrade = analysis.confidenceGrade,
            isConfident = analysis.isConfident,
            isDemo = analysis.isDemo,
            problemExplanation = analysis.problemExplanation,
            whatToDoNext = analysis.whatToDoNext,
            imageResName = imageNameOrUri
        )
        dao.insertDiseaseCase(caseEntity)

        // Automatically create draft treatment plan if confident
        if (analysis.isConfident && analysis.label != null && analysis.label != "Healthy Cassava Leaf") {
            val planId = "PLAN-${UUID.randomUUID().toString().take(8).uppercase()}"
            val treatmentPlan = TreatmentPlanEntity(
                id = planId,
                diseaseCaseId = caseId,
                farmerId = farmerId,
                status = "READY",
                feeKobo = TreatmentFeeCalculator.DEFAULT_PLAN_FEE.kobo,
                feeStatus = TreatmentFeeCalculator.FeeStatus.UNPAID.name,
                problemTitle = analysis.label,
                nextSteps = analysis.whatToDoNext,
                localTreatments = "Field Sanitation: Rogue and burn infected plants away from field. Disinfect pruning knives between cuts.",
                organicOption = "Bio-Neem Spray: Apply botanical azadirachtin repellent to suppress whitefly transmitters. 0-day harvest delay.",
                chemicalOption = "Targeted Bactericide/Fungicide: If infection rate exceeds 20%, apply registered copper bactericide strictly following label.",
                safetyNotes = "VERIFIED LABEL ONLY: Never exceed manufacturer dilution rates. Wear protective goggles and gloves.",
                recommendedProductIdsJson = analysis.recommendedProductIds.joinToString(","),
                authorName = "Verified SmartFarm AI Consultant",
                approvedAt = System.currentTimeMillis()
            )
            dao.insertTreatmentPlan(treatmentPlan)
        }

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = farmerId,
                role = "FARMER",
                action = "DISEASE_SCREENING",
                entityType = "DiseaseCase",
                entityId = caseId,
                details = "Disease screening performed with DemoAdapter. Result: ${analysis.label ?: "Uncertain"}"
            )
        )

        return caseEntity
    }

    // --- Treatment Plans ---
    val treatmentPlansForCurrentFarmer: Flow<List<TreatmentPlanEntity>> = _currentUser.flatMapLatest { user ->
        dao.getTreatmentPlansForFarmer(user.id)
    }

    val allTreatmentPlans: Flow<List<TreatmentPlanEntity>> = dao.getAllTreatmentPlans()

    suspend fun payForTreatmentPlan(planId: String) {
        val plan = dao.getTreatmentPlanById(planId) ?: return
        val updated = plan.copy(
            feeStatus = TreatmentFeeCalculator.FeeStatus.PAID.name,
            status = "DELIVERED"
        )
        dao.updateTreatmentPlan(updated)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = plan.farmerId,
                role = "FARMER",
                action = "PAYMENT_TREATMENT_PLAN",
                entityType = "TreatmentPlan",
                entityId = planId,
                details = "Farmer paid ₦500 via Paystack for localized treatment plan."
            )
        )
    }

    suspend fun checkAndUpdateWaiver(planId: String, farmerId: String) {
        val plan = dao.getTreatmentPlanById(planId) ?: return
        val orders = dao.getInputOrdersForFarmer(farmerId).first()
        val recommended = plan.recommendedProductIdsJson.split(",").filter { it.isNotBlank() }

        val eligibleOrders = orders.map { o ->
            TreatmentFeeCalculator.EligibleInputOrder(
                orderId = o.id,
                isPaid = o.status in listOf("PAID", "SUPPLIER_CONFIRMED", "READY", "DELIVERED", "COMPLETED"),
                createdAt = o.createdAt,
                productIds = o.itemsProductIdsJson.split(",").filter { it.isNotBlank() }
            )
        }

        val waiverResult = TreatmentFeeCalculator.checkWaiverEligibility(
            treatmentPlanRequestedAt = plan.createdAt,
            recommendedProductIds = recommended,
            paidInputOrders = eligibleOrders
        )

        if (waiverResult.isWaived && plan.feeStatus != TreatmentFeeCalculator.FeeStatus.PAID.name) {
            val updated = plan.copy(
                feeStatus = TreatmentFeeCalculator.FeeStatus.WAIVED_INPUT_PURCHASE.name,
                waivedByOrderId = waiverResult.matchingOrderId,
                status = "DELIVERED"
            )
            dao.updateTreatmentPlan(updated)
            dao.insertAuditLog(
                AuditLogEntity(
                    id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                    actor = "System",
                    role = "System",
                    action = "TREATMENT_PLAN_WAIVED",
                    entityType = "TreatmentPlan",
                    entityId = planId,
                    details = "Treatment plan ₦500 fee waived due to input order ${waiverResult.matchingOrderId}"
                )
            )
        }
    }

    // --- Consultations ---
    val consultationsForCurrentFarmer: Flow<List<ConsultationEntity>> = _currentUser.flatMapLatest { user ->
        dao.getConsultationsForFarmer(user.id)
    }

    val allConsultations: Flow<List<ConsultationEntity>> = dao.getAllConsultations()

    suspend fun bookConsultation(
        farmerId: String,
        farmerName: String,
        farmerLga: String,
        farmerPhone: String,
        problemText: String
    ): ConsultationEntity {
        val consultationId = "CONS-${UUID.randomUUID().toString().take(8).uppercase()}"
        val consultation = ConsultationEntity(
            id = consultationId,
            farmerId = farmerId,
            farmerName = farmerName,
            farmerLga = farmerLga,
            farmerPhone = farmerPhone,
            status = "PAID",
            problemText = problemText,
            scheduledTime = "Consultant will contact within 2 hours",
            handoffNotes = "Paid ₦3,000 via Paystack. WhatsApp and direct call active."
        )
        dao.insertConsultation(consultation)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = farmerId,
                role = "FARMER",
                action = "BOOK_CONSULTATION",
                entityType = "Consultation",
                entityId = consultationId,
                details = "Consultation booked and ₦3,000 paid via Paystack."
            )
        )
        return consultation
    }

    // --- Products & Inputs ---
    val approvedProducts: Flow<List<ProductEntity>> = dao.getApprovedProducts()
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()

    fun productsForSupplier(supplierId: String): Flow<List<ProductEntity>> =
        dao.getProductsForSupplier(supplierId)

    suspend fun insertOrUpdateProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun approveProduct(productId: String, approved: Boolean) {
        val prod = dao.getProductById(productId) ?: return
        dao.updateProduct(prod.copy(approvalStatus = if (approved) "APPROVED" else "REJECTED"))
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = "Admin",
                role = "ADMIN",
                action = "PRODUCT_APPROVAL",
                entityType = "Product",
                entityId = productId,
                details = "Product approval updated to ${if (approved) "APPROVED" else "REJECTED"}"
            )
        )
    }

    // --- Input Orders & Commissions ---
    val inputOrdersForCurrentFarmer: Flow<List<InputOrderEntity>> = _currentUser.flatMapLatest { user ->
        dao.getInputOrdersForFarmer(user.id)
    }
    val allInputOrders: Flow<List<InputOrderEntity>> = dao.getAllInputOrders()

    suspend fun placeInputOrder(
        farmerId: String,
        farmerName: String,
        products: List<ProductEntity>,
        deliveryFeeKobo: Long = 200000L
    ): InputOrderEntity {
        val orderId = "ORD-${UUID.randomUUID().toString().take(8).uppercase()}"
        val grossKobo = products.sumOf { it.priceKobo }
        val commissionResult = CommissionCalculator.calculateInputCommission(grossKobo)
        val totalKobo = grossKobo + deliveryFeeKobo

        val order = InputOrderEntity(
            id = orderId,
            farmerId = farmerId,
            farmerName = farmerName,
            itemsSummary = products.joinToString(", ") { it.simpleName },
            itemsProductIdsJson = products.joinToString(",") { it.id },
            grossKobo = grossKobo,
            deliveryKobo = deliveryFeeKobo,
            totalKobo = totalKobo,
            commissionKobo = commissionResult.commissionKobo,
            supplierAmountKobo = commissionResult.supplierNetKobo,
            status = "PAID",
            paymentRef = "PAYSTACK-${UUID.randomUUID().toString().take(10).uppercase()}"
        )
        dao.insertInputOrder(order)

        // Record commission ledger
        dao.insertCommission(
            CommissionRecordEntity(
                id = "COMM-${UUID.randomUUID().toString().take(8)}",
                orderId = orderId,
                orderType = "INPUT_ORDER",
                grossKobo = grossKobo,
                ratePercent = commissionResult.ratePercent,
                commissionKobo = commissionResult.commissionKobo,
                partyNetKobo = commissionResult.supplierNetKobo,
                processingFeeKobo = 0L,
                netSettlementKobo = commissionResult.supplierNetKobo,
                settlementStatus = "SETTLED"
            )
        )

        // Check if any treatment plan can now be waived
        val plans = dao.getTreatmentPlansForFarmer(farmerId).first()
        for (plan in plans) {
            checkAndUpdateWaiver(plan.id, farmerId)
        }

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = farmerId,
                role = "FARMER",
                action = "INPUT_ORDER_PLACED",
                entityType = "InputOrder",
                entityId = orderId,
                details = "Order placed and paid for gross ₦${grossKobo / 100}. Commission: ₦${commissionResult.commissionKobo / 100} (${commissionResult.ratePercent}%)."
            )
        )

        return order
    }

    suspend fun updateInputOrderStatus(orderId: String, newStatus: String) {
        val orders = dao.getAllInputOrders().first()
        val order = orders.find { it.id == orderId } ?: return
        dao.updateInputOrder(order.copy(status = newStatus))
    }

    // --- Market Prices ---
    val allMarketPrices: Flow<List<MarketPriceEntity>> = dao.getAllMarketPrices()

    suspend fun addMarketPrice(price: MarketPriceEntity) {
        dao.insertMarketPrice(price)
    }

    suspend fun deleteMarketPrice(price: MarketPriceEntity) {
        dao.deleteMarketPrice(price)
    }

    // --- Cassava Listings ---
    val activeListings: Flow<List<CassavaListingEntity>> = dao.getActiveListings()
    val allListings: Flow<List<CassavaListingEntity>> = dao.getAllListings()
    val listingsForCurrentFarmer: Flow<List<CassavaListingEntity>> = _currentUser.flatMapLatest { user ->
        dao.getListingsForFarmer(user.id)
    }

    suspend fun createCassavaListing(listing: CassavaListingEntity) {
        dao.insertListing(listing)
        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = listing.farmerId,
                role = "FARMER",
                action = "CREATE_LISTING",
                entityType = "CassavaListing",
                entityId = listing.id,
                details = "Listing created for ${listing.quantity} ${listing.unit} of ${listing.cassavaForm} at ₦${listing.pricePerUnitKobo / 100} per unit."
            )
        )
    }

    suspend fun approveListing(listingId: String, approved: Boolean) {
        val listings = dao.getAllListings().first()
        val listing = listings.find { it.id == listingId } ?: return
        dao.updateListing(listing.copy(status = if (approved) "ACTIVE" else "REJECTED"))
    }

    // --- Produce Orders & Milestone Workflow ---
    val allProduceOrders: Flow<List<ProduceOrderEntity>> = dao.getAllProduceOrders()
    val produceOrdersForCurrentFarmer: Flow<List<ProduceOrderEntity>> = _currentUser.flatMapLatest { user ->
        dao.getProduceOrdersForFarmer(user.id)
    }
    val produceOrdersForCurrentBuyer: Flow<List<ProduceOrderEntity>> = _currentUser.flatMapLatest { user ->
        dao.getProduceOrdersForBuyer(user.id)
    }

    suspend fun createProduceOrder(
        listing: CassavaListingEntity,
        buyer: UserEntity,
        logisticsQuoteKobo: Long = 2500000L // ₦25,000 default logistics estimate
    ): ProduceOrderEntity {
        val orderId = "PRD-${UUID.randomUUID().toString().take(8).uppercase()}"
        val charges = CommissionCalculator.calculateProduceCharges(
            producePriceKobo = listing.totalKobo,
            logisticsChargeKobo = logisticsQuoteKobo
        )

        val order = ProduceOrderEntity(
            id = orderId,
            listingId = listing.id,
            buyerId = buyer.id,
            buyerName = buyer.name,
            buyerPhone = buyer.phone,
            farmerId = listing.farmerId,
            farmerFirstName = listing.farmerFirstName,
            farmerLga = listing.lga,
            quantity = listing.quantity,
            unit = listing.unit,
            producePriceKobo = listing.totalKobo,
            buyerServiceChargeKobo = charges.buyerServiceChargeKobo,
            sellerProcessingFeeKobo = charges.sellerProcessingFeeKobo,
            logisticsChargeKobo = logisticsQuoteKobo,
            totalBuyerPayableKobo = charges.totalBuyerPayableKobo,
            farmerNetPayoutKobo = charges.farmerNetPayoutKobo,
            milestone1Kobo = charges.milestone1Kobo,
            milestone2Kobo = charges.milestone2Kobo,
            milestone1Status = "PENDING",
            milestone2Status = "PENDING",
            orderStatus = ProduceOrderStateMachine.OrderState.PAYMENT_RECEIVED_TEST.name,
            createdAt = System.currentTimeMillis()
        )
        dao.insertProduceOrder(order)

        // Mark listing as reserved
        dao.updateListing(listing.copy(status = "RESERVED"))

        // Record buyer commission in ledger
        dao.insertCommission(
            CommissionRecordEntity(
                id = "COMM-${UUID.randomUUID().toString().take(8)}",
                orderId = orderId,
                orderType = "PRODUCE_ORDER",
                grossKobo = listing.totalKobo,
                ratePercent = charges.buyerRatePercent,
                commissionKobo = charges.buyerServiceChargeKobo,
                partyNetKobo = charges.farmerNetPayoutKobo,
                processingFeeKobo = charges.sellerProcessingFeeKobo,
                netSettlementKobo = charges.farmerNetPayoutKobo,
                settlementStatus = "SETTLED"
            )
        )

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = buyer.id,
                role = "BUYER",
                action = "PRODUCE_ORDER_PLACED",
                entityType = "ProduceOrder",
                entityId = orderId,
                details = "Produce order placed. Buyer paid ₦${charges.totalBuyerPayableKobo / 100} via Paystack. Farmer net payout: ₦${charges.farmerNetPayoutKobo / 100} (Milestone 1: ₦${charges.milestone1Kobo / 100}, Milestone 2: ₦${charges.milestone2Kobo / 100})."
            )
        )

        return order
    }

    suspend fun transitionProduceOrder(
        orderId: String,
        nextState: ProduceOrderStateMachine.OrderState,
        proofOfPickup: String? = null,
        proofOfDelivery: String? = null
    ) {
        val order = dao.getProduceOrderById(orderId) ?: return
        val currentState = runCatching {
            ProduceOrderStateMachine.OrderState.valueOf(order.orderStatus)
        }.getOrDefault(ProduceOrderStateMachine.OrderState.ORDER_CONFIRMED)

        if (!ProduceOrderStateMachine.canTransition(currentState, nextState)) {
            return // Prevent invalid state jumps
        }

        var m1Status = order.milestone1Status
        var m2Status = order.milestone2Status

        // Check Milestone 1 eligibility upon pickup
        if (ProduceOrderStateMachine.isMilestone1Eligible(nextState, proofOfPickup != null || order.proofOfPickup != null)) {
            if (m1Status == "PENDING") {
                m1Status = "ELIGIBLE"
            }
        }

        // Check Milestone 2 eligibility upon buyer confirmation or delivery
        if (nextState == ProduceOrderStateMachine.OrderState.BUYER_CONFIRMED ||
            nextState == ProduceOrderStateMachine.OrderState.FINAL_MILESTONE_ELIGIBLE
        ) {
            if (m2Status == "PENDING") {
                m2Status = "ELIGIBLE"
            }
        }

        val updated = order.copy(
            orderStatus = nextState.name,
            milestone1Status = m1Status,
            milestone2Status = m2Status,
            proofOfPickup = proofOfPickup ?: order.proofOfPickup,
            proofOfDelivery = proofOfDelivery ?: order.proofOfDelivery
        )
        dao.updateProduceOrder(updated)

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = "User/Admin",
                role = "SYSTEM",
                action = "ORDER_TRANSITION",
                entityType = "ProduceOrder",
                entityId = orderId,
                details = "Order transitioned from ${currentState.displayName} to ${nextState.displayName}."
            )
        )
    }

    suspend fun releaseMilestone(orderId: String, milestoneNumber: Int) {
        val order = dao.getProduceOrderById(orderId) ?: return
        val updated = if (milestoneNumber == 1) {
            order.copy(milestone1Status = "RELEASED")
        } else {
            order.copy(
                milestone2Status = "RELEASED",
                orderStatus = ProduceOrderStateMachine.OrderState.COMPLETED.name
            )
        }
        dao.updateProduceOrder(updated)

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = "Admin",
                role = "ADMIN",
                action = "MILESTONE_RELEASE",
                entityType = "ProduceOrder",
                entityId = orderId,
                details = "MILESTONE SETTLEMENT: Released Milestone $milestoneNumber payout to farmer account."
            )
        )
    }

    suspend fun raiseDispute(orderId: String, reason: String) {
        val order = dao.getProduceOrderById(orderId) ?: return
        val updated = order.copy(
            isDisputed = true,
            disputeReason = reason,
            orderStatus = ProduceOrderStateMachine.OrderState.DISPUTED.name,
            milestone1Status = if (order.milestone1Status == "ELIGIBLE") "HELD" else order.milestone1Status,
            milestone2Status = if (order.milestone2Status == "ELIGIBLE") "HELD" else order.milestone2Status
        )
        dao.updateProduceOrder(updated)

        dao.insertAuditLog(
            AuditLogEntity(
                id = "AUDIT-${UUID.randomUUID().toString().take(8)}",
                actor = "Buyer/Farmer",
                role = "USER",
                action = "RAISE_DISPUTE",
                entityType = "ProduceOrder",
                entityId = orderId,
                details = "Dispute raised: $reason. Milestones held pending admin review."
            )
        )
    }

    // --- Commissions & Audit ---
    val allCommissions: Flow<List<CommissionRecordEntity>> = dao.getAllCommissions()
    val recentAuditLogs: Flow<List<AuditLogEntity>> = dao.getRecentAuditLogs()

    suspend fun resetDemoData() {
        dao.deleteDemoDiseaseCases()
        dao.deleteDemoProducts()
        dao.deleteDemoMarketPrices()
        dao.deleteDemoListings()
        SmartFarmDatabase.populateInitialDemoData(dao)
    }
}
