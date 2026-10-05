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
import com.example.smartfarm.data.model.InputOrderEntity
import com.example.smartfarm.data.model.ProductEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.CommissionCalculator
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun SupplierDashboardScreen(
    currentUser: UserEntity,
    products: List<ProductEntity>,
    incomingOrders: List<InputOrderEntity>,
    onUpdateOrderStatus: (String, String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Products (${products.size})", "Incoming Orders (${incomingOrders.size})", "Settlements")

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

        // Supplier Header & Verification Badge
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentUser.name,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Registered Agro-Dealer · ${currentUser.lga} LGA, Kogi State",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Badge(
                            containerColor = FarmGreenContainer,
                            contentColor = FarmGreenDark
                        ) {
                            Text(
                                text = "VERIFIED SUPPLIER",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
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

        when (selectedTab) {
            0 -> {
                // PRODUCTS CATALOGUE
                items(products) { product ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.simpleName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("Category: ${product.category}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Badge(
                                    containerColor = if (product.approvalStatus == "APPROVED") FarmGreenContainer else FarmAmberLight,
                                    contentColor = if (product.approvalStatus == "APPROVED") FarmGreenDark else FarmBrownEarth
                                ) {
                                    Text(product.approvalStatus, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Price: ${Money(product.priceKobo).toFormattedNaira()} per ${product.unit}",
                                fontWeight = FontWeight.SemiBold,
                                color = FarmGreenPrimary
                            )
                            if (product.registrationNumber.isNotBlank()) {
                                Text("Reg No: ${product.registrationNumber}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
            1 -> {
                // INCOMING ORDERS
                items(incomingOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Badge(containerColor = FarmGreenContainer, contentColor = FarmGreenDark) {
                                    Text(order.status, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Farmer: ${order.farmerName}", fontSize = 12.sp)
                            Text("Items: ${order.itemsSummary}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (order.status == "PAID") {
                                    Button(
                                        onClick = { onUpdateOrderStatus(order.id, "SUPPLIER_CONFIRMED") },
                                        modifier = Modifier.testTag("confirm_order_${order.id.lowercase()}")
                                    ) {
                                        Text("Confirm Order")
                                    }
                                } else if (order.status == "SUPPLIER_CONFIRMED") {
                                    Button(onClick = { onUpdateOrderStatus(order.id, "READY") }) {
                                        Text("Mark Ready for Pickup")
                                    }
                                } else if (order.status == "READY") {
                                    Button(onClick = { onUpdateOrderStatus(order.id, "DELIVERED") }) {
                                        Text("Mark Delivered")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // SETTLEMENT STATEMENT (Section 13)
                item {
                    Text("Settlement Statement (Verified Ledger)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SmartFarm AI commission: 5% for orders < ₦500,000; 2.5% for orders ≥ ₦500,000.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                items(incomingOrders) { order ->
                    val comm = remember(order.grossKobo) {
                        CommissionCalculator.calculateInputCommission(order.grossKobo)
                    }

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Order #${order.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gross Product Amount:", fontSize = 12.sp)
                                Text(Money(order.grossKobo).toFormattedNaira(), fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("SmartFarm AI Commission (${comm.ratePercent}%):", fontSize = 12.sp, color = FarmRedError)
                                Text("-${Money(comm.commissionKobo).toFormattedNaira()}", fontSize = 12.sp, color = FarmRedError)
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Net Supplier Settlement:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(
                                    Money(comm.supplierNetKobo).toFormattedNaira(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = FarmGreenPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
