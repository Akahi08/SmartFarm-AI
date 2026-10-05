package com.example.smartfarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.CassavaListingEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.CommissionCalculator
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.ui.theme.*

@Composable
fun CassavaSellScreen(
    currentUser: UserEntity,
    farmerListings: List<CassavaListingEntity>,
    onCreateListing: (String, Double, String, Long, String, String, Boolean, () -> Unit) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedForm by remember { mutableStateOf("Fresh Roots (TME 419)") }
    var quantityText by remember { mutableStateOf("5") }
    var selectedUnit by remember { mutableStateOf("Tonne") }
    var pricePerUnitText by remember { mutableStateOf("85000") } // ₦85,000 / tonne
    var harvestDate by remember { mutableStateOf("Ready in 5 days") }
    var notes by remember { mutableStateOf("Well-matured roots, high starch content.") }
    var logisticsNeeded by remember { mutableStateOf(true) }

    val quantity = quantityText.toDoubleOrNull() ?: 1.0
    val priceNaira = pricePerUnitText.toLongOrNull() ?: 85000L
    val grossTotalKobo = (quantity * priceNaira * 100).toLong()

    val charges = remember(grossTotalKobo) {
        CommissionCalculator.calculateProduceCharges(grossTotalKobo)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sell My Cassava",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Reach verified buyers across Kogi with 50/50 payout protection",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier.testTag("new_cassava_listing_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Listing")
                }
            }
        }

        // 50/50 Milestone Payout Information Strip (Section 6.11)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = FarmGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Milestone Payment Protection (50% / 50%)",
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenDark,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• 50% released immediately upon confirmed farm pickup.\n" +
                               "• 50% released when buyer confirms delivery receipt.\n" +
                               "• Transparent seller processing fee deduction (never hidden).",
                        fontSize = 12.sp,
                        color = FarmGreenDark,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "My Cassava Listings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (farmerListings.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active listings yet", fontWeight = FontWeight.Bold)
                        Text(
                            "Tap 'New Listing' above to post your ready cassava.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(farmerListings) { listing ->
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
                                Text(
                                    text = "Location: ${listing.community}, ${listing.lga} LGA",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }

                            Badge(
                                containerColor = when (listing.status) {
                                    "ACTIVE" -> FarmGreenContainer
                                    "PENDING_REVIEW" -> FarmAmberLight
                                    "RESERVED" -> FarmBlueLight
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                },
                                contentColor = when (listing.status) {
                                    "ACTIVE" -> FarmGreenDark
                                    "PENDING_REVIEW" -> FarmBrownEarth
                                    "RESERVED" -> FarmBlueInfo
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            ) {
                                Text(
                                    text = listing.status.replace("_", " "),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Asking Price & Payout Preview
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Asking Price:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = Money(listing.totalKobo).toFormattedNaira(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = FarmGreenPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Availability:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = listing.harvestDate,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        // Farmer Payout Preview with 50/50 split
                        val listingCharges = remember(listing.totalKobo) {
                            CommissionCalculator.calculateProduceCharges(listing.totalKobo)
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Farmer Payout Preview (After processing fee):",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Net Payout:", fontSize = 12.sp)
                                    Text(
                                        Money(listingCharges.farmerNetPayoutKobo).toFormattedNaira(),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Milestone 1 (50% at Pickup):", fontSize = 11.sp)
                                    Text(Money(listingCharges.milestone1Kobo).toFormattedNaira(), fontSize = 11.sp)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("• Milestone 2 (50% on Delivery):", fontSize = 11.sp)
                                    Text(Money(listingCharges.milestone2Kobo).toFormattedNaira(), fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // New Listing Dialog Form
    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("List Cassava for Sale", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Cassava Form:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilterChip(
                            selected = selectedForm.startsWith("Fresh"),
                            onClick = { selectedForm = "Fresh Roots (TME 419)" },
                            label = { Text("Fresh Roots", fontSize = 11.sp) }
                        )
                        FilterChip(
                            selected = selectedForm.startsWith("Garri"),
                            onClick = { selectedForm = "White Garri" },
                            label = { Text("Garri", fontSize = 11.sp) }
                        )
                    }

                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { quantityText = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = pricePerUnitText,
                        onValueChange = { pricePerUnitText = it },
                        label = { Text("Asking Price per Unit (₦)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = harvestDate,
                        onValueChange = { harvestDate = it },
                        label = { Text("Harvest / Availability Date") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = logisticsNeeded,
                            onCheckedChange = { logisticsNeeded = it }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("I need transport / truck coordination", fontSize = 12.sp)
                    }

                    Surface(
                        color = FarmGreenContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Total Value: ${Money(grossTotalKobo).toFormattedNaira()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = FarmGreenDark
                            )
                            Text(
                                "Est. Net Payout: ${Money(charges.farmerNetPayoutKobo).toFormattedNaira()} (50/50 split)",
                                fontSize = 11.sp,
                                color = FarmGreenDark
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCreateListing(
                            selectedForm,
                            quantity,
                            selectedUnit,
                            priceNaira * 100L,
                            harvestDate,
                            notes,
                            logisticsNeeded
                        ) {
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier.testTag("submit_cassava_listing_button")
                ) {
                    Text("Submit for Review")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
