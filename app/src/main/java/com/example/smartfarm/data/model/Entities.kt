package com.example.smartfarm.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    FARMER,
    BUYER,
    SUPPLIER,
    CONSULTANT,
    ADMIN
}

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val role: UserRole,
    val name: String,
    val phone: String,
    val email: String,
    val state: String = "Kogi",
    val lga: String = "Dekina",
    val community: String = "Ayingba",
    val consentGiven: Boolean = true,
    val locationConsent: Boolean = false,
    val isDemo: Boolean = true
)

@Entity(tableName = "disease_cases")
data class DiseaseCaseEntity(
    @PrimaryKey val id: String,
    val farmerId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val adapterId: String = "demo",
    val label: String?,
    val confidence: Double,
    val confidenceGrade: String,
    val isConfident: Boolean,
    val isDemo: Boolean = true,
    val problemExplanation: String,
    val whatToDoNext: String,
    val imageResName: String = "cassava_leaf_guide_1791065875241"
)

@Entity(tableName = "treatment_plans")
data class TreatmentPlanEntity(
    @PrimaryKey val id: String,
    val diseaseCaseId: String,
    val farmerId: String,
    val status: String, // "REQUESTED", "AWAITING_EXPERT", "READY", "DELIVERED"
    val feeKobo: Long = 50000L, // ₦500
    val feeStatus: String, // "UNPAID", "PAID", "WAIVED_INPUT_PURCHASE"
    val waivedByOrderId: String? = null,
    val problemTitle: String,
    val nextSteps: String,
    val localTreatments: String,
    val organicOption: String,
    val chemicalOption: String,
    val safetyNotes: String,
    val recommendedProductIdsJson: String, // JSON array of string IDs
    val authorName: String = "Verified SmartFarm AI Consultant",
    val approvedAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "consultations")
data class ConsultationEntity(
    @PrimaryKey val id: String,
    val farmerId: String,
    val farmerName: String,
    val farmerLga: String,
    val farmerPhone: String,
    val consultantId: String = "consultant_01",
    val feeKobo: Long = 300000L, // ₦3,000
    val status: String, // "REQUESTED", "PAYMENT_PENDING", "PAID", "SCHEDULED", "COMPLETED", "CANCELLED"
    val problemText: String,
    val scheduledTime: String = "Pending Confirmation",
    val handoffNotes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val supplierId: String,
    val supplierName: String,
    val isSupplierVerified: Boolean = true,
    val simpleName: String,
    val officialName: String,
    val purpose: String,
    val category: String, // "Stem Cuttings", "Fertilizer", "Crop Protection", "Biological"
    val type: String, // "CHEMICAL", "ORGANIC_BIOLOGICAL", "NEITHER"
    val priceKobo: Long,
    val unit: String,
    val quantityPerUnit: String,
    val inStock: Boolean = true,
    val state: String = "Kogi",
    val lga: String = "Lokoja",
    val safetyInfo: String,
    val labelInfo: String,
    val registrationNumber: String,
    val approvalStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val isDemo: Boolean = true
)

@Entity(tableName = "input_orders")
data class InputOrderEntity(
    @PrimaryKey val id: String,
    val farmerId: String,
    val farmerName: String,
    val itemsSummary: String,
    val itemsProductIdsJson: String,
    val grossKobo: Long,
    val deliveryKobo: Long = 200000L, // ₦2,000 delivery quote
    val totalKobo: Long,
    val commissionKobo: Long,
    val supplierAmountKobo: Long,
    val status: String, // "PLACED", "PAID", "SUPPLIER_CONFIRMED", "READY", "DELIVERED", "COMPLETED", "CANCELLED"
    val paymentRef: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_prices")
data class MarketPriceEntity(
    @PrimaryKey val id: String,
    val location: String,
    val cassavaForm: String, // "Fresh Roots", "Garri", "Dried Chips", "Cassava Flour"
    val priceKobo: Long,
    val unit: String,
    val updatedAt: Long = System.currentTimeMillis(),
    val sourceLabel: String = "Entered by SmartFarm AI team",
    val isDemo: Boolean = true,
    val buyersLookingCount: Int = 3
)

@Entity(tableName = "cassava_listings")
data class CassavaListingEntity(
    @PrimaryKey val id: String,
    val farmerId: String,
    val farmerFirstName: String,
    val lga: String,
    val community: String,
    val cassavaForm: String,
    val quantity: Double,
    val unit: String, // "Tonne", "100kg Bag", "Basin"
    val pricePerUnitKobo: Long,
    val totalKobo: Long,
    val harvestDate: String,
    val notes: String,
    val status: String = "PENDING_REVIEW", // "PENDING_REVIEW", "ACTIVE", "RESERVED", "SOLD", "REJECTED"
    val logisticsNeeded: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "produce_orders")
data class ProduceOrderEntity(
    @PrimaryKey val id: String,
    val listingId: String,
    val buyerId: String,
    val buyerName: String,
    val buyerPhone: String,
    val farmerId: String,
    val farmerFirstName: String,
    val farmerLga: String,
    val quantity: Double,
    val unit: String,
    val producePriceKobo: Long,
    val buyerServiceChargeKobo: Long,
    val sellerProcessingFeeKobo: Long,
    val logisticsChargeKobo: Long,
    val totalBuyerPayableKobo: Long,
    val farmerNetPayoutKobo: Long,
    val milestone1Kobo: Long,
    val milestone2Kobo: Long,
    val milestone1Status: String = "PENDING", // "PENDING", "ELIGIBLE", "RELEASED_TEST"
    val milestone2Status: String = "PENDING", // "PENDING", "ELIGIBLE", "RELEASED_TEST"
    val orderStatus: String, // ProduceOrderStateMachine.OrderState
    val disputeWindowEndsAt: Long = System.currentTimeMillis() + (48 * 3600 * 1000L),
    val isDisputed: Boolean = false,
    val disputeReason: String? = null,
    val proofOfPickup: String? = null,
    val proofOfDelivery: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "commissions")
data class CommissionRecordEntity(
    @PrimaryKey val id: String,
    val orderId: String,
    val orderType: String, // "INPUT_ORDER", "PRODUCE_ORDER"
    val grossKobo: Long,
    val ratePercent: Double,
    val commissionKobo: Long,
    val partyNetKobo: Long,
    val processingFeeKobo: Long = 0L,
    val netSettlementKobo: Long,
    val settlementStatus: String = "SIMULATED_LEDGER",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val actor: String,
    val role: String,
    val action: String,
    val entityType: String,
    val entityId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)
