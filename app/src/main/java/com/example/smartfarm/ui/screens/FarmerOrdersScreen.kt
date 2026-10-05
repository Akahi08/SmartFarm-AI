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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.InputOrderEntity
import com.example.smartfarm.data.model.ProduceOrderEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.Money
import com.example.smartfarm.domain.ProduceOrderStateMachine
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.EmptyStateView
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun FarmerOrdersScreen(
    currentUser: UserEntity,
    inputOrders: List<InputOrderEntity>,
    produceOrders: List<ProduceOrderEntity>,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Input Orders (${inputOrders.size})", "Cassava Sales (${produceOrders.size})")

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
                    text = "My Farm Activity & Orders",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Track input shipments and cassava produce sales milestones",
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
            // INPUT ORDERS
            if (inputOrders.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.ShoppingBag,
                        title = "No Input Orders Yet",
                        message = "Order certified stems, fertilizers, and disease treatments directly from verified dealers.",
                        actionButtonText = "Browse Farm Inputs",
                        onActionClicked = { onNavigate(AppScreen.INPUT_CATALOGUE) }
                    )
                }
            } else {
                items(inputOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Badge(
                                    containerColor = FarmGreenContainer,
                                    contentColor = FarmGreenDark
                                ) {
                                    Text(
                                        text = order.status,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = order.itemsSummary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Total Paid (Test):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    Money(order.totalKobo).toFormattedNaira(),
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // CASSAVA PRODUCE SALES
            if (produceOrders.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.Storefront,
                        title = "No Produce Sales Yet",
                        message = "Post your cassava harvest to connect with verified garri and flour processors.",
                        actionButtonText = "List Cassava for Sale",
                        onActionClicked = { onNavigate(AppScreen.SELL_CASSAVA) }
                    )
                }
            } else {
                items(produceOrders) { order ->
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
                                    Text("Produce Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Buyer: ${order.buyerName}", style = MaterialTheme.typography.bodySmall)
                                }
                                Badge(
                                    containerColor = if (order.isDisputed) FarmRedLight else FarmBlueLight,
                                    contentColor = if (order.isDisputed) FarmRedError else FarmBlueInfo
                                ) {
                                    Text(
                                        text = if (order.isDisputed) "DISPUTED" else order.orderStatus.replace("_", " "),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${order.quantity} ${order.unit} Cassava Roots",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Milestone Progress Cards
                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("Milestone Payouts (50% / 50%):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("• Milestone 1 (Pickup):", fontSize = 11.sp)
                                        Text("${Money(order.milestone1Kobo).toFormattedNaira()} [${order.milestone1Status}]", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("• Milestone 2 (Delivery):", fontSize = 11.sp)
                                        Text("${Money(order.milestone2Kobo).toFormattedNaira()} [${order.milestone2Status}]", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Net Farmer Payout:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(Money(order.farmerNetPayoutKobo).toFormattedNaira(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
