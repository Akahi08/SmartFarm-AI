package com.example.smartfarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.CassavaListingEntity
import com.example.smartfarm.data.model.ProduceOrderEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.CommissionCalculator
import com.example.smartfarm.domain.Money
import com.example.smartfarm.domain.ProduceOrderStateMachine
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.EmptyStateView
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun BuyerMarketplaceScreen(
    currentUser: UserEntity,
    activeListings: List<CassavaListingEntity>,
    buyerOrders: List<ProduceOrderEntity>,
    onPlaceOrder: (CassavaListingEntity, (String) -> Unit) -> Unit,
    onConfirmDelivery: (String) -> Unit,
    onRaiseDispute: (String, String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Available Cassava (${activeListings.size})", "My Orders (${buyerOrders.size})")

    var selectedListingForCheckout by remember { mutableStateOf<CassavaListingEntity?>(null) }
    var orderSuccessId by remember { mutableStateOf<String?>(null) }
    var showDisputeDialogForOrderId by remember { mutableStateOf<String?>(null) }
    var disputeReason by remember { mutableStateOf("Roots received smaller than agreed standard weight.") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TestModeBanner()
        }

        item {
            Column {
                Text(
                    text = "Buyer Cassava Marketplace",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Source fresh cassava roots directly from verified Kogi farmers with milestone security",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        item {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // AVAILABLE LISTINGS
            if (activeListings.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Storefront,
                        title = "No Cassava Matches Available",
                        message = "Farmers across Kogi State will post new harvest lots as maturity is reached."
                    )
                }
            } else {
                items(activeListings) { listing ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text(
                                        text = "${listing.quantity} ${listing.unit} of ${listing.cassavaForm}",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    // Privacy rule: first name or LGA only, never phone/address
                                    Text(
                                        text = "Farmer: ${listing.farmerFirstName} (${listing.community}, ${listing.lga} LGA)",
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }

                                Badge(
                                    containerColor = FarmGreenContainer,
                                    contentColor = FarmGreenDark
                                ) {
                                    Text("VERIFIED FARM", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Harvest: ${listing.harvestDate} · Transport Assistance: ${if (listing.logisticsNeeded) "Yes" else "No"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = Money(listing.totalKobo).toFormattedNaira(),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = FarmGreenPrimary
                                        )
                                    )
                                    Text(
                                        text = "${Money(listing.pricePerUnitKobo).toFormattedNaira()} per ${listing.unit}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { selectedListingForCheckout = listing },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                    modifier = Modifier.testTag("request_to_buy_${listing.id.lowercase()}")
                                ) {
                                    Icon(imageVector = Icons.Default.ShoppingCartCheckout, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Buy Produce")
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // BUYER ORDERS
            if (buyerOrders.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.ShoppingBag,
                        title = "No Produce Orders Yet",
                        message = "Switch to 'Available Cassava' to source fresh batches from farmers."
                    )
                }
            } else {
                items(buyerOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(
                                        text = "${order.quantity} ${order.unit} from Farmer ${order.farmerFirstName} (${order.farmerLga})",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }

                                Badge(
                                    containerColor = if (order.isDisputed) FarmRedLight else FarmBlueLight,
                                    contentColor = if (order.isDisputed) FarmRedError else FarmBlueInfo
                                ) {
                                    Text(
                                        text = if (order.isDisputed) "DISPUTED" else order.orderStatus.replace("_", " "),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Paid by Buyer:", fontSize = 12.sp)
                                Text(Money(order.totalBuyerPayableKobo).toFormattedNaira(), fontWeight = FontWeight.Bold)
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                            // Delivery Confirmation & Dispute Actions
                            if (order.orderStatus != ProduceOrderStateMachine.OrderState.COMPLETED.name && !order.isDisputed) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { onConfirmDelivery(order.id) },
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("confirm_delivery_${order.id.lowercase()}")
                                    ) {
                                        Text("Confirm Receipt")
                                    }

                                    OutlinedButton(
                                        onClick = { showDisputeDialogForOrderId = order.id },
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = FarmRedError)
                                    ) {
                                        Text("Raise Dispute")
                                    }
                                }
                            } else if (order.isDisputed) {
                                Surface(
                                    color = FarmRedLight,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Dispute active: ${order.disputeReason ?: "Pending admin review"}. Payout milestones frozen.",
                                        fontSize = 11.sp,
                                        color = FarmRedError,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                }
                            } else {
                                Text(
                                    text = "✓ Order completed & final milestone released.",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // "Check and Pay" Buyer Dialog (Section 6.10)
    selectedListingForCheckout?.let { listing ->
        val charges = CommissionCalculator.calculateProduceCharges(
            producePriceKobo = listing.totalKobo,
            logisticsChargeKobo = 2500000L // ₦25,000 logistics quote
        )

        AlertDialog(
            onDismissRequest = { selectedListingForCheckout = null },
            title = { Text("Check and Pay", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "${listing.quantity} ${listing.unit} of ${listing.cassavaForm}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text("Farmer: ${listing.farmerFirstName} (${listing.lga} LGA)", fontSize = 12.sp)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Line 1: Produce price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("1. Produce Price:", fontSize = 12.sp)
                        Text(Money(charges.producePriceKobo).toFormattedNaira(), fontSize = 12.sp)
                    }

                    // Line 2: Buyer service charge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("2. SmartFarm AI Service Charge (${charges.buyerRatePercent}%):", fontSize = 12.sp)
                        Text(Money(charges.buyerServiceChargeKobo).toFormattedNaira(), fontSize = 12.sp)
                    }

                    // Line 3: Payment-processing charge info line (paid by seller)
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Text(
                            text = "ℹ️ Payment-processing fee: Paid by the seller. Not added to your total.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(6.dp)
                        )
                    }

                    // Line 4: Logistics
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("3. Logistics & Truck Quote:", fontSize = 12.sp)
                        Text(Money(charges.logisticsChargeKobo).toFormattedNaira(), fontSize = 12.sp)
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    // Line 5: Total payable
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Payable:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            Money(charges.totalBuyerPayableKobo).toFormattedNaira(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = FarmGreenPrimary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onPlaceOrder(listing) { orderId ->
                            selectedListingForCheckout = null
                            orderSuccessId = orderId
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier.testTag("confirm_produce_payment_button")
                ) {
                    Text("Pay with Paystack (Test)")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedListingForCheckout = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Success dialog
    orderSuccessId?.let { orderId ->
        AlertDialog(
            onDismissRequest = { orderSuccessId = null },
            title = { Text("Payment Received & Secured") },
            text = {
                Text(
                    "Order #$orderId has been placed. Milestone workflow activated (NOT escrow).\n\n" +
                    "Logistics will be coordinated from the farmer's location in Kogi State."
                )
            },
            confirmButton = {
                Button(onClick = { orderSuccessId = null }) {
                    Text("View Order")
                }
            }
        )
    }

    // Dispute Dialog
    showDisputeDialogForOrderId?.let { orderId ->
        AlertDialog(
            onDismissRequest = { showDisputeDialogForOrderId = null },
            title = { Text("Raise Order Dispute") },
            text = {
                Column {
                    Text("State reason for dispute (within 48hr window):", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = disputeReason,
                        onValueChange = { disputeReason = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRaiseDispute(orderId, disputeReason)
                        showDisputeDialogForOrderId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmRedError)
                ) {
                    Text("Submit Dispute")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDisputeDialogForOrderId = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
