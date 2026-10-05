package com.example.smartfarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.smartfarm.data.model.TreatmentPlanEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.EmptyStateView
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun TreatmentPlanScreen(
    currentUser: UserEntity,
    treatmentPlans: List<TreatmentPlanEntity>,
    onPayForPlan: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedPlanId by remember { mutableStateOf<String?>(treatmentPlans.firstOrNull()?.id) }

    val activePlan = treatmentPlans.find { it.id == selectedPlanId } ?: treatmentPlans.firstOrNull()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TestModeBanner()
        }

        if (treatmentPlans.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.Healing,
                    title = "No Treatment Plans Yet",
                    message = "Screen a cassava leaf with our disease checker or request advice from our agricultural expert.",
                    actionButtonText = "Screen Leaf Now",
                    onActionClicked = { onNavigate(AppScreen.DISEASE_SCREENING) }
                )
            }
        } else {
            // Plan Selector Chips if multiple
            if (treatmentPlans.size > 1) {
                item {
                    Text(
                        text = "Your Treatment Plans",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        treatmentPlans.forEach { plan ->
                            FilterChip(
                                selected = plan.id == activePlan?.id,
                                onClick = { selectedPlanId = plan.id },
                                label = { Text(plan.problemTitle, maxLines = 1) }
                            )
                        }
                    }
                }
            }

            activePlan?.let { plan ->
                val isUnlocked = plan.feeStatus == "PAID" || plan.feeStatus == "WAIVED_INPUT_PURCHASE"

                item {
                    // Fee / Waiver Status Banner
                    Surface(
                        color = when (plan.feeStatus) {
                            "WAIVED_INPUT_PURCHASE" -> FarmGreenContainer
                            "PAID" -> FarmBlueLight
                            else -> FarmAmberLight
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = when (plan.feeStatus) {
                                        "WAIVED_INPUT_PURCHASE" -> Icons.Default.CheckCircle
                                        "PAID" -> Icons.Default.Payment
                                        else -> Icons.Default.Info
                                    },
                                    contentDescription = null,
                                    tint = when (plan.feeStatus) {
                                        "WAIVED_INPUT_PURCHASE" -> FarmGreenDark
                                        "PAID" -> FarmBlueInfo
                                        else -> FarmBrownEarth
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = when (plan.feeStatus) {
                                        "WAIVED_INPUT_PURCHASE" -> "Fee Waived: Input Order Verified"
                                        "PAID" -> "Plan Unlocked (₦500 Paid)"
                                        else -> "Treatment Plan Fee: ₦500"
                                    },
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = when (plan.feeStatus) {
                                        "WAIVED_INPUT_PURCHASE" -> FarmGreenDark
                                        "PAID" -> FarmBlueInfo
                                        else -> FarmBrownEarth
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = when (plan.feeStatus) {
                                    "WAIVED_INPUT_PURCHASE" -> "You bought recommended farm inputs through SmartFarm AI (Order #${plan.waivedByOrderId ?: "Verified"}). This ₦500 plan is 100% free."
                                    "PAID" -> "Full localized treatment plan prepared and approved by our agricultural consultant."
                                    else -> "Get this treatment plan FREE when you buy recommended inputs through SmartFarm AI, or pay ₦500 now."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = when (plan.feeStatus) {
                                    "WAIVED_INPUT_PURCHASE" -> FarmGreenDark
                                    "PAID" -> FarmBlueInfo
                                    else -> FarmBrownEarth
                                }
                            )

                            if (!isUnlocked) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { onPayForPlan(plan.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                                        modifier = Modifier.testTag("pay_500_plan_button")
                                    ) {
                                        Text("Pay ₦500 (Test)")
                                    }
                                    OutlinedButton(
                                        onClick = { onNavigate(AppScreen.INPUT_CATALOGUE) },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("Buy Inputs (Waive Fee)")
                                    }
                                }
                            }
                        }
                    }
                }

                // Treatment Plan Content
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
                                Text(
                                    text = plan.problemTitle,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Badge(
                                    containerColor = FarmGreenLight,
                                    contentColor = FarmGreenDark
                                ) {
                                    Text(
                                        text = "EXPERT APPROVED",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Author: ${plan.authorName}",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                            // Local next steps
                            SectionHeader(title = "Immediate Farm Steps")
                            Text(
                                text = plan.nextSteps,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Local cultural treatments
                            SectionHeader(title = "Local Farm Practices & Sanitation")
                            Text(
                                text = plan.localTreatments,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Organic option
                            SectionHeader(title = "Organic / Biological Option")
                            Surface(
                                color = FarmGreenContainer.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = plan.organicOption,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Chemical option
                            SectionHeader(title = "Chemical Option (If Infestation Severe)")
                            Surface(
                                color = FarmAmberLight.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = plan.chemicalOption,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Strict Safety / Dosage Warning
                            Surface(
                                color = FarmRedLight.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, FarmRedError.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = FarmRedError,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Verified Safety & Label Rules",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = FarmRedError
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = plan.safetyNotes,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FarmRedError
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = { onNavigate(AppScreen.INPUT_CATALOGUE) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Order Verified Inputs from Catalogue")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
        color = FarmGreenDark,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}
