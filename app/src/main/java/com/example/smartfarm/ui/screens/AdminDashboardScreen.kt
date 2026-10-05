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
import com.example.smartfarm.data.model.*
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun AdminDashboardScreen(
    currentUser: UserEntity,
    allProducts: List<ProductEntity>,
    allListings: List<CassavaListingEntity>,
    allProduceOrders: List<ProduceOrderEntity>,
    allCommissions: List<CommissionRecordEntity>,
    recentAuditLogs: List<AuditLogEntity>,
    onApproveProduct: (String, Boolean) -> Unit,
    onApproveListing: (String, Boolean) -> Unit,
    onReleaseMilestone: (String, Int) -> Unit,
    onResetDemoData: () -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedSection by remember { mutableIntStateOf(0) }
    val sections = listOf("Overview", "Approvals", "Milestones", "Commissions", "Audit Logs")
    var showResetConfirmDialog by remember { mutableStateOf(false) }

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

        // Admin Header
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
                                text = "SmartFarm Admin Console",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Platform governance, compliance, ledger & milestone releases",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Button(
                            onClick = { showResetConfirmDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmAmberAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("reset_demo_data_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset Demo", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Navigation Tabs
        item {
            ScrollableTabRow(selectedTabIndex = selectedSection, edgePadding = 0.dp) {
                sections.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedSection == index,
                        onClick = { selectedSection = index },
                        text = { Text(title, fontWeight = if (selectedSection == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }
        }

        when (selectedSection) {
            0 -> {
                // OVERVIEW METRICS
                val totalCommissionKobo = allCommissions.sumOf { it.commissionKobo }
                val pendingListingsCount = allListings.count { it.status == "PENDING_REVIEW" }
                val pendingMilestonesCount = allProduceOrders.count { it.milestone1Status == "ELIGIBLE" || it.milestone2Status == "ELIGIBLE" }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "SmartFarm Revenue",
                                value = Money(totalCommissionKobo).toFormattedNaira(),
                                subtitle = "Platform Commissions",
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Produce Orders",
                                value = allProduceOrders.size.toString(),
                                subtitle = "Milestone Workflow",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MetricCard(
                                title = "Listings Review",
                                value = pendingListingsCount.toString(),
                                subtitle = "Pending Approval",
                                modifier = Modifier.weight(1f)
                            )
                            MetricCard(
                                title = "Milestones Ready",
                                value = pendingMilestonesCount.toString(),
                                subtitle = "Awaiting Release",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            1 -> {
                // APPROVALS QUEUE (Products & Cassava Listings)
                val pendingProducts = allProducts.filter { it.approvalStatus == "PENDING" }
                val pendingListings = allListings.filter { it.status == "PENDING_REVIEW" }

                item {
                    Text("Pending Cassava Listings (${pendingListings.size})", fontWeight = FontWeight.Bold)
                }

                if (pendingListings.isEmpty()) {
                    item { Text("No listings pending review.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(pendingListings) { listing ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("${listing.quantity} ${listing.unit} of ${listing.cassavaForm}", fontWeight = FontWeight.Bold)
                                Text("Farmer: ${listing.farmerFirstName} (${listing.lga}) · Total: ${Money(listing.totalKobo).toFormattedNaira()}", fontSize = 12.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onApproveListing(listing.id, true) },
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.testTag("approve_listing_${listing.id.lowercase()}")
                                    ) {
                                        Text("Approve")
                                    }
                                    OutlinedButton(
                                        onClick = { onApproveListing(listing.id, false) }
                                    ) {
                                        Text("Reject")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // MILESTONE SETTLEMENT CONSOLE (Test Settlement Release)
                item {
                    Surface(
                        color = FarmGreenLight,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚖️ MILESTONE SETTLEMENT CONSOLE: Admin verifies proof of pickup and proof of delivery before triggering payout release to farmer.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmGreenDark,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                items(allProduceOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text("Produce Order #${order.id}", fontWeight = FontWeight.Bold)
                            Text("Status: ${order.orderStatus} · Farmer: ${order.farmerFirstName}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Milestone 1 release button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Milestone 1 (50%): ${Money(order.milestone1Kobo).toFormattedNaira()}", fontSize = 12.sp)
                                    Text("Status: ${order.milestone1Status}", fontSize = 11.sp, color = FarmGreenDark)
                                }
                                if (order.milestone1Status == "ELIGIBLE") {
                                    Button(
                                        onClick = { onReleaseMilestone(order.id, 1) },
                                        modifier = Modifier.testTag("release_m1_${order.id.lowercase()}")
                                    ) {
                                        Text("Release Milestone 1")
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Milestone 2 release button
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Milestone 2 (50%): ${Money(order.milestone2Kobo).toFormattedNaira()}", fontSize = 12.sp)
                                    Text("Status: ${order.milestone2Status}", fontSize = 11.sp, color = FarmGreenDark)
                                }
                                if (order.milestone2Status == "ELIGIBLE") {
                                    Button(
                                        onClick = { onReleaseMilestone(order.id, 2) },
                                        modifier = Modifier.testTag("release_m2_${order.id.lowercase()}")
                                    ) {
                                        Text("Release Milestone 2")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            3 -> {
                // COMMISSION LEDGER
                items(allCommissions) { comm ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Ledger #${comm.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(comm.orderType, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gross Goods:", fontSize = 12.sp)
                                Text(Money(comm.grossKobo).toFormattedNaira(), fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Commission (${comm.ratePercent}%):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                                Text(Money(comm.commissionKobo).toFormattedNaira(), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
                            }
                        }
                    }
                }
            }
            4 -> {
                // AUDIT LOGS
                items(recentAuditLogs) { log ->
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${log.action} (${log.role})", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(log.entityType, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(log.details, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Demo Data?") },
            text = { Text("This will reset all listings, cases, and orders back to initial clean demo states.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetDemoData()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmRedError)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = FarmGreenPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
