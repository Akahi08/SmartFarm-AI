package com.example.smartfarm.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartfarm.data.local.SmartFarmDatabase
import com.example.smartfarm.data.model.*
import com.example.smartfarm.data.repository.SmartFarmRepository
import com.example.smartfarm.domain.ProduceOrderStateMachine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppScreen {
    FARMER_HOME,
    DISEASE_SCREENING,
    TREATMENT_PLANS,
    INPUT_CATALOGUE,
    MARKET_PRICES,
    SELL_CASSAVA,
    CONSULTATION,
    FARMER_ORDERS,
    BUYER_MARKETPLACE,
    SUPPLIER_DASHBOARD,
    CONSULTANT_DASHBOARD,
    ADMIN_DASHBOARD,
    LEGAL_PRIVACY
}

class SmartFarmViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SmartFarmDatabase.getDatabase(application, viewModelScope)
    val repository = SmartFarmRepository(database.smartFarmDao())

    // Current Screen & Back Stack
    private val _currentScreen = MutableStateFlow(AppScreen.FARMER_HOME)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val backStack = mutableListOf<AppScreen>()

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    // Current User Session
    val currentUser = repository.currentUser
    val allUsers = repository.allUsers.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun switchRole(role: UserRole) {
        viewModelScope.launch {
            val users = allUsers.value
            val target = users.find { it.role == role }
            if (target != null) {
                repository.switchUser(target)
                // If user is buyer, supplier, consultant or admin, route to their default view
                when (role) {
                    UserRole.FARMER -> navigateTo(AppScreen.FARMER_HOME)
                    UserRole.BUYER -> navigateTo(AppScreen.BUYER_MARKETPLACE)
                    UserRole.SUPPLIER -> navigateTo(AppScreen.SUPPLIER_DASHBOARD)
                    UserRole.CONSULTANT -> navigateTo(AppScreen.CONSULTANT_DASHBOARD)
                    UserRole.ADMIN -> navigateTo(AppScreen.ADMIN_DASHBOARD)
                }
            }
        }
    }

    // Consent Management
    fun updateConsent(imageConsent: Boolean, locationConsent: Boolean) {
        viewModelScope.launch {
            val updated = currentUser.value.copy(
                consentGiven = imageConsent,
                locationConsent = locationConsent
            )
            repository.saveUser(updated)
        }
    }

    // --- Disease Screening ---
    val diseaseCases = repository.diseaseCasesForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allDiseaseCases = repository.allDiseaseCases.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAnalyzingDisease = MutableStateFlow(false)
    val isAnalyzingDisease: StateFlow<Boolean> = _isAnalyzingDisease.asStateFlow()

    private val _latestScreeningResult = MutableStateFlow<DiseaseCaseEntity?>(null)
    val latestScreeningResult: StateFlow<DiseaseCaseEntity?> = _latestScreeningResult.asStateFlow()

    fun performDiseaseScreening(sampleImageKey: String) {
        viewModelScope.launch {
            _isAnalyzingDisease.value = true
            // Simulate brief analysis latency
            kotlinx.coroutines.delay(1200)
            val result = repository.runDiseaseScreening(
                farmerId = currentUser.value.id,
                imageNameOrUri = sampleImageKey,
                isConsentGiven = currentUser.value.consentGiven
            )
            _latestScreeningResult.value = result
            _isAnalyzingDisease.value = false
        }
    }

    fun clearScreeningResult() {
        _latestScreeningResult.value = null
    }

    // --- Treatment Plans ---
    val treatmentPlans = repository.treatmentPlansForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allTreatmentPlans = repository.allTreatmentPlans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun payForTreatmentPlan(planId: String) {
        viewModelScope.launch {
            repository.payForTreatmentPlan(planId)
        }
    }

    // --- Products & Cart ---
    val approvedProducts = repository.approvedProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allProducts = repository.allProducts.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _cartItems = MutableStateFlow<List<ProductEntity>>(emptyList())
    val cartItems: StateFlow<List<ProductEntity>> = _cartItems.asStateFlow()

    fun addToCart(product: ProductEntity) {
        _cartItems.value = _cartItems.value + product
    }

    fun removeFromCart(product: ProductEntity) {
        _cartItems.value = _cartItems.value - product
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun checkoutInputOrder(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val items = _cartItems.value
            if (items.isEmpty()) return@launch
            val order = repository.placeInputOrder(
                farmerId = currentUser.value.id,
                farmerName = currentUser.value.name,
                products = items
            )
            _cartItems.value = emptyList()
            onSuccess(order.id)
        }
    }

    // --- Input Orders ---
    val inputOrders = repository.inputOrdersForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allInputOrders = repository.allInputOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateInputOrderStatus(orderId: String, status: String) {
        viewModelScope.launch {
            repository.updateInputOrderStatus(orderId, status)
        }
    }

    // --- Market Prices ---
    val marketPrices = repository.allMarketPrices.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addMarketPrice(location: String, form: String, priceKobo: Long, unit: String, source: String) {
        viewModelScope.launch {
            val price = MarketPriceEntity(
                id = "PRICE-${UUID.randomUUID().toString().take(6).uppercase()}",
                location = location,
                cassavaForm = form,
                priceKobo = priceKobo,
                unit = unit,
                updatedAt = System.currentTimeMillis(),
                sourceLabel = source,
                isDemo = false
            )
            repository.addMarketPrice(price)
        }
    }

    // --- Cassava Listings ---
    val activeListings = repository.activeListings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allListings = repository.allListings.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val farmerListings = repository.listingsForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createListing(
        cassavaForm: String,
        quantity: Double,
        unit: String,
        pricePerUnitKobo: Long,
        harvestDate: String,
        notes: String,
        logisticsNeeded: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val totalKobo = (quantity * pricePerUnitKobo).toLong()
            val listing = CassavaListingEntity(
                id = "LIST-${UUID.randomUUID().toString().take(6).uppercase()}",
                farmerId = currentUser.value.id,
                farmerFirstName = currentUser.value.name.split(" ").firstOrNull() ?: "Farmer",
                lga = currentUser.value.lga,
                community = currentUser.value.community,
                cassavaForm = cassavaForm,
                quantity = quantity,
                unit = unit,
                pricePerUnitKobo = pricePerUnitKobo,
                totalKobo = totalKobo,
                harvestDate = harvestDate,
                notes = notes,
                status = "PENDING_REVIEW", // Admin must approve
                logisticsNeeded = logisticsNeeded
            )
            repository.createCassavaListing(listing)
            onSuccess()
        }
    }

    fun approveListing(listingId: String, approved: Boolean) {
        viewModelScope.launch {
            repository.approveListing(listingId, approved)
        }
    }

    // --- Produce Orders & Milestones ---
    val produceOrdersForBuyer = repository.produceOrdersForCurrentBuyer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val produceOrdersForFarmer = repository.produceOrdersForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allProduceOrders = repository.allProduceOrders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun placeProduceOrder(listing: CassavaListingEntity, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val order = repository.createProduceOrder(
                listing = listing,
                buyer = currentUser.value
            )
            onSuccess(order.id)
        }
    }

    fun transitionProduceOrder(orderId: String, nextState: ProduceOrderStateMachine.OrderState) {
        viewModelScope.launch {
            repository.transitionProduceOrder(
                orderId = orderId,
                nextState = nextState,
                proofOfPickup = if (nextState == ProduceOrderStateMachine.OrderState.PICKED_UP) "proof_pickup.jpg" else null,
                proofOfDelivery = if (nextState == ProduceOrderStateMachine.OrderState.DELIVERED) "proof_delivery.jpg" else null
            )
        }
    }

    fun releaseTestMilestone(orderId: String, milestoneNum: Int) {
        viewModelScope.launch {
            repository.releaseMilestone(orderId, milestoneNum)
        }
    }

    fun raiseDispute(orderId: String, reason: String) {
        viewModelScope.launch {
            repository.raiseDispute(orderId, reason)
        }
    }

    // --- Consultations ---
    val consultations = repository.consultationsForCurrentFarmer.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val allConsultations = repository.allConsultations.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun bookConsultation(problemText: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            val consultation = repository.bookConsultation(
                farmerId = currentUser.value.id,
                farmerName = currentUser.value.name,
                farmerLga = currentUser.value.lga,
                farmerPhone = currentUser.value.phone,
                problemText = problemText
            )
            onSuccess(consultation.id)
        }
    }

    // --- Admin & Ledger ---
    val commissions = repository.allCommissions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val auditLogs = repository.recentAuditLogs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun approveProduct(productId: String, approved: Boolean) {
        viewModelScope.launch {
            repository.approveProduct(productId, approved)
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetDemoData()
        }
    }
}
