package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartfarm.data.model.UserRole
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.SmartFarmViewModel
import com.example.smartfarm.ui.components.SmartTopBar
import com.example.smartfarm.ui.components.WebDirectAccessDialog
import com.example.smartfarm.ui.screens.*
import com.example.ui.theme.SmartFarmTheme

class MainActivity : ComponentActivity() {

    private var pendingDeepLinkScreen by mutableStateOf<AppScreen?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleIncomingIntent(intent)
        setContent {
            SmartFarmTheme {
                val viewModel: SmartFarmViewModel = viewModel()
                var showWebDialog by remember { mutableStateOf(false) }

                LaunchedEffect(pendingDeepLinkScreen) {
                    pendingDeepLinkScreen?.let {
                        viewModel.navigateTo(it)
                        pendingDeepLinkScreen = null
                    }
                }

                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()

                // Data streams
                val diseaseCases by viewModel.diseaseCases.collectAsStateWithLifecycle()
                val latestScreeningResult by viewModel.latestScreeningResult.collectAsStateWithLifecycle()
                val isAnalyzingDisease by viewModel.isAnalyzingDisease.collectAsStateWithLifecycle()

                val treatmentPlans by viewModel.treatmentPlans.collectAsStateWithLifecycle()
                val approvedProducts by viewModel.approvedProducts.collectAsStateWithLifecycle()
                val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
                val cartItems by viewModel.cartItems.collectAsStateWithLifecycle()
                val inputOrders by viewModel.inputOrders.collectAsStateWithLifecycle()
                val allInputOrders by viewModel.allInputOrders.collectAsStateWithLifecycle()

                val marketPrices by viewModel.marketPrices.collectAsStateWithLifecycle()
                val activeListings by viewModel.activeListings.collectAsStateWithLifecycle()
                val allListings by viewModel.allListings.collectAsStateWithLifecycle()
                val farmerListings by viewModel.farmerListings.collectAsStateWithLifecycle()

                val produceOrdersForBuyer by viewModel.produceOrdersForBuyer.collectAsStateWithLifecycle()
                val produceOrdersForFarmer by viewModel.produceOrdersForFarmer.collectAsStateWithLifecycle()
                val allProduceOrders by viewModel.allProduceOrders.collectAsStateWithLifecycle()

                val consultations by viewModel.consultations.collectAsStateWithLifecycle()
                val allConsultations by viewModel.allConsultations.collectAsStateWithLifecycle()

                val commissions by viewModel.commissions.collectAsStateWithLifecycle()
                val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()

                var isWebMode by remember { mutableStateOf(true) }

                if (isWebMode) {
                    Scaffold(
                        topBar = {
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.statusBars)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🌿", fontSize = 18.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                "SmartFarm AI Website",
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Text(
                                                "Kogi State Cassava Platform",
                                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 10.sp)
                                            )
                                        }
                                    }
                                    FilledTonalButton(
                                        onClick = { isWebMode = false },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Native App Mode", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        androidx.compose.ui.viewinterop.AndroidView(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            factory = { context ->
                                android.webkit.WebView(context).apply {
                                    settings.javaScriptEnabled = true
                                    settings.domStorageEnabled = true
                                    settings.allowFileAccess = true
                                    settings.allowContentAccess = true
                                    settings.loadWithOverviewMode = true
                                    settings.useWideViewPort = true
                                    webViewClient = android.webkit.WebViewClient()
                                    loadUrl("file:///android_asset/web/index.html")
                                }
                            }
                        )
                    }
                } else {
                    // Native Compose App Mode
                    // Intercept back button for sub-screens
                    BackHandler(enabled = currentScreen != AppScreen.FARMER_HOME && currentScreen != AppScreen.BUYER_MARKETPLACE && currentScreen != AppScreen.ADMIN_DASHBOARD) {
                        viewModel.navigateBack()
                    }

                val title = when (currentScreen) {
                    AppScreen.FARMER_HOME -> "SmartFarm AI"
                    AppScreen.DISEASE_SCREENING -> "Cassava Leaf Screening"
                    AppScreen.TREATMENT_PLANS -> "Treatment Plan"
                    AppScreen.INPUT_CATALOGUE -> "Verified Farm Inputs"
                    AppScreen.MARKET_PRICES -> "Kogi Market Prices"
                    AppScreen.SELL_CASSAVA -> "Sell My Cassava"
                    AppScreen.CONSULTATION -> "Consult Expert"
                    AppScreen.FARMER_ORDERS -> "My Orders & Sales"
                    AppScreen.BUYER_MARKETPLACE -> "Buyer Marketplace"
                    AppScreen.SUPPLIER_DASHBOARD -> "Supplier Portal"
                    AppScreen.CONSULTANT_DASHBOARD -> "Consultant Workspace"
                    AppScreen.ADMIN_DASHBOARD -> "Admin Console"
                    AppScreen.LEGAL_PRIVACY -> "Legal & Privacy"
                }

                Scaffold(
                    topBar = {
                        SmartTopBar(
                            title = title,
                            canNavigateBack = currentScreen != AppScreen.FARMER_HOME,
                            onNavigateBack = { viewModel.navigateBack() },
                            currentUser = currentUser,
                            onRoleSelected = { role -> viewModel.switchRole(role) },
                            onLegalClicked = { viewModel.navigateTo(AppScreen.LEGAL_PRIVACY) },
                            onWebLinkClicked = { showWebDialog = true }
                        )
                    },
                    bottomBar = {
                        // Standard Bottom Navigation for primary navigation
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.FARMER_HOME,
                                onClick = { viewModel.navigateTo(AppScreen.FARMER_HOME) },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home") },
                                modifier = Modifier.testTag("nav_item_home")
                            )
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.INPUT_CATALOGUE,
                                onClick = { viewModel.navigateTo(AppScreen.INPUT_CATALOGUE) },
                                icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Inputs") },
                                label = { Text("Inputs") },
                                modifier = Modifier.testTag("nav_item_inputs")
                            )
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.MARKET_PRICES,
                                onClick = { viewModel.navigateTo(AppScreen.MARKET_PRICES) },
                                icon = { Icon(Icons.Default.TrendingUp, contentDescription = "Market") },
                                label = { Text("Market") },
                                modifier = Modifier.testTag("nav_item_market")
                            )
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.FARMER_ORDERS || currentScreen == AppScreen.SELL_CASSAVA,
                                onClick = { viewModel.navigateTo(AppScreen.FARMER_ORDERS) },
                                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                                label = { Text("Orders") },
                                modifier = Modifier.testTag("nav_item_orders")
                            )
                            NavigationBarItem(
                                selected = currentScreen == AppScreen.CONSULTATION,
                                onClick = { viewModel.navigateTo(AppScreen.CONSULTATION) },
                                icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Expert") },
                                label = { Text("Expert") },
                                modifier = Modifier.testTag("nav_item_expert")
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            AppScreen.FARMER_HOME -> {
                                FarmerHomeScreen(
                                    currentUser = currentUser,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.DISEASE_SCREENING -> {
                                DiseaseScreeningScreen(
                                    currentUser = currentUser,
                                    latestResult = latestScreeningResult,
                                    isAnalyzing = isAnalyzingDisease,
                                    onAnalyze = { sample -> viewModel.performDiseaseScreening(sample) },
                                    onClearResult = { viewModel.clearScreeningResult() },
                                    onUpdateConsent = { img, loc -> viewModel.updateConsent(img, loc) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.TREATMENT_PLANS -> {
                                TreatmentPlanScreen(
                                    currentUser = currentUser,
                                    treatmentPlans = treatmentPlans,
                                    onPayForPlan = { viewModel.payForTreatmentPlan(it) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.INPUT_CATALOGUE -> {
                                InputCatalogueScreen(
                                    currentUser = currentUser,
                                    products = approvedProducts,
                                    cartItems = cartItems,
                                    onAddToCart = { viewModel.addToCart(it) },
                                    onRemoveFromCart = { viewModel.removeFromCart(it) },
                                    onCheckout = { callback -> viewModel.checkoutInputOrder(callback) },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.MARKET_PRICES -> {
                                CassavaMarketPricesScreen(
                                    currentUser = currentUser,
                                    marketPrices = marketPrices,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.SELL_CASSAVA -> {
                                CassavaSellScreen(
                                    currentUser = currentUser,
                                    farmerListings = farmerListings,
                                    onCreateListing = { form, qty, unit, price, harvest, notes, logistics, onSuccess ->
                                        viewModel.createListing(form, qty, unit, price, harvest, notes, logistics, onSuccess)
                                    },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.CONSULTATION -> {
                                ConsultationScreen(
                                    currentUser = currentUser,
                                    consultations = consultations,
                                    onBookConsultation = { text, callback ->
                                        viewModel.bookConsultation(text, callback)
                                    },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.FARMER_ORDERS -> {
                                FarmerOrdersScreen(
                                    currentUser = currentUser,
                                    inputOrders = inputOrders,
                                    produceOrders = produceOrdersForFarmer,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.BUYER_MARKETPLACE -> {
                                BuyerMarketplaceScreen(
                                    currentUser = currentUser,
                                    activeListings = activeListings,
                                    buyerOrders = produceOrdersForBuyer,
                                    onPlaceOrder = { listing, callback ->
                                        viewModel.placeProduceOrder(listing, callback)
                                    },
                                    onConfirmDelivery = { orderId ->
                                        viewModel.transitionProduceOrder(
                                            orderId,
                                            com.example.smartfarm.domain.ProduceOrderStateMachine.OrderState.BUYER_CONFIRMED
                                        )
                                    },
                                    onRaiseDispute = { orderId, reason ->
                                        viewModel.raiseDispute(orderId, reason)
                                    },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.SUPPLIER_DASHBOARD -> {
                                SupplierDashboardScreen(
                                    currentUser = currentUser,
                                    products = allProducts.filter { it.supplierId == currentUser.id },
                                    incomingOrders = allInputOrders,
                                    onUpdateOrderStatus = { orderId, status ->
                                        viewModel.updateInputOrderStatus(orderId, status)
                                    },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.CONSULTANT_DASHBOARD -> {
                                ConsultantDashboardScreen(
                                    currentUser = currentUser,
                                    diseaseCases = diseaseCases,
                                    treatmentPlans = treatmentPlans,
                                    consultations = allConsultations,
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.ADMIN_DASHBOARD -> {
                                AdminDashboardScreen(
                                    currentUser = currentUser,
                                    allProducts = allProducts,
                                    allListings = allListings,
                                    allProduceOrders = allProduceOrders,
                                    allCommissions = commissions,
                                    recentAuditLogs = auditLogs,
                                    onApproveProduct = { id, approved ->
                                        viewModel.approveProduct(id, approved)
                                    },
                                    onApproveListing = { id, approved ->
                                        viewModel.approveListing(id, approved)
                                    },
                                    onReleaseMilestone = { orderId, num ->
                                        viewModel.releaseTestMilestone(orderId, num)
                                    },
                                    onResetDemoData = { viewModel.resetDemoData() },
                                    onNavigate = { viewModel.navigateTo(it) }
                                )
                            }
                            AppScreen.LEGAL_PRIVACY -> {
                                LegalPrivacyScreen(
                                    currentUser = currentUser,
                                    onUpdateConsent = { img, loc -> viewModel.updateConsent(img, loc) }
                                )
                            }
                        }

                        if (showWebDialog) {
                            WebDirectAccessDialog(onDismiss = { showWebDialog = false })
                        }
                    }
                }
            }
        }
    }
}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val data = intent?.data ?: return
        val path = data.path?.lowercase() ?: ""
        pendingDeepLinkScreen = when {
            path.contains("check-cassava") || path.contains("disease") -> AppScreen.DISEASE_SCREENING
            path.contains("treatment") -> AppScreen.TREATMENT_PLANS
            path.contains("inputs") || path.contains("shop") -> AppScreen.INPUT_CATALOGUE
            path.contains("market") || path.contains("price") -> AppScreen.MARKET_PRICES
            path.contains("sell") -> AppScreen.SELL_CASSAVA
            path.contains("expert") || path.contains("consult") -> AppScreen.CONSULTATION
            path.contains("buyer") -> AppScreen.BUYER_MARKETPLACE
            path.contains("order") -> AppScreen.FARMER_ORDERS
            else -> AppScreen.FARMER_HOME
        }
    }
}
