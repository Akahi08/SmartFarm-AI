package com.example.smartfarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.smartfarm.data.model.UserEntity
import com.example.ui.theme.*

@Composable
fun LegalPrivacyScreen(
    currentUser: UserEntity,
    onUpdateConsent: (Boolean, Boolean) -> Unit
) {
    var imageConsent by remember { mutableStateOf(currentUser.consentGiven) }
    var locationConsent by remember { mutableStateOf(currentUser.locationConsent) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Surface(
                color = FarmAmberLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "⚠️ LEGAL DRAFT: All text below is drafted for Nigerian agricultural context and needs final review by a licensed Nigerian attorney prior to commercial deployment.",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = FarmBrownEarth,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        item {
            Text(
                text = "Legal Terms & Privacy",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        }

        // Consent Settings Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "My Privacy Preferences",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Store Leaf Screening Photos", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Used to provide screening history and calibrate cassava models.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = imageConsent,
                            onCheckedChange = {
                                imageConsent = it
                                onUpdateConsent(imageConsent, locationConsent)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Share Farm Community Location", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text("Used only for regional pest outbreak alerts in Kogi State.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = locationConsent,
                            onCheckedChange = {
                                locationConsent = it
                                onUpdateConsent(imageConsent, locationConsent)
                            }
                        )
                    }
                }
            }
        }

        // Terms Sections
        item {
            LegalExpandableCard(
                title = "1. Cassava Marketplace & Milestone Payments",
                content = "• SmartFarm AI operates a milestone coordination service for cassava transactions in Kogi State.\n" +
                        "• This is NOT an escrow service and SmartFarm AI does not operate as an escrow agent.\n" +
                        "• Milestone 1 (50%) is eligible upon physical pickup verified by logistics carrier.\n" +
                        "• Milestone 2 (50%) is eligible upon buyer confirmation or expiration of the 48-hour dispute window."
            )
        }

        item {
            LegalExpandableCard(
                title = "2. Farm Inputs & Pesticide Disclaimer",
                content = "• All pesticide and fertilizer dosage recommendations shown in SmartFarm AI are sourced exclusively from manufacturer labels registered with NAFDAC and NASC.\n" +
                        "• SmartFarm AI does not use autonomous AI to prescribe pesticide dosages.\n" +
                        "• Farmers must follow safety guidelines, wearing protective clothing and adhering to harvest intervals."
            )
        }

        item {
            LegalExpandableCard(
                title = "3. Consultation & Refund Policy",
                content = "• Agricultural consultation bookings (₦3,000) connect farmers to certified extension agents.\n" +
                        "• If a scheduled session is cancelled by SmartFarm AI, a full test refund is issued.\n" +
                        "• Treatment plans (₦500) are waived when purchasing recommended inputs through the platform within 14 days."
            )
        }

        item {
            LegalExpandableCard(
                title = "4. Verified Badges & Non-Government Notice",
                content = "• Badges such as 'Verified Supplier' and 'Verified SmartFarm AI Consultant' represent internal platform vetting.\n" +
                        "• These badges do not constitute official endorsement by the Federal Ministry of Agriculture or the Kogi State Government."
            )
        }
    }
}

@Composable
private fun LegalExpandableCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
