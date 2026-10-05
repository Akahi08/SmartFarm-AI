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
import com.example.smartfarm.data.model.ConsultationEntity
import com.example.smartfarm.data.model.DiseaseCaseEntity
import com.example.smartfarm.data.model.TreatmentPlanEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun ConsultantDashboardScreen(
    currentUser: UserEntity,
    diseaseCases: List<DiseaseCaseEntity>,
    treatmentPlans: List<TreatmentPlanEntity>,
    consultations: List<ConsultationEntity>,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Disease Cases (${diseaseCases.size})", "Treatment Plans (${treatmentPlans.size})", "Consultations (${consultations.size})")

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
                                text = "Agricultural Consultant Workspace",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Extension advisory & localized treatment plan review for Kogi State",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Badge(containerColor = FarmGreenContainer, contentColor = FarmGreenDark) {
                            Text("CONSULTANT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🔒 Privacy Shield: Farmer names/phones protected. Phone visible only on paid scheduled sessions.",
                        fontSize = 11.sp,
                        color = FarmGreenDark
                    )
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
                // DISEASE CASES
                items(diseaseCases) { case ->
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
                                Text(case.label ?: "Uncertain Leaf Case", fontWeight = FontWeight.Bold)
                                Badge(
                                    containerColor = if (case.isConfident) FarmGreenContainer else FarmAmberLight,
                                    contentColor = if (case.isConfident) FarmGreenDark else FarmBrownEarth
                                ) {
                                    Text("${case.confidenceGrade} (${(case.confidence * 100).toInt()}%)", fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Farmer ID: ${case.farmerId} (Dekina, Kogi)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(case.problemExplanation, fontSize = 12.sp)
                        }
                    }
                }
            }
            1 -> {
                // TREATMENT PLANS EDITOR & REVIEW
                items(treatmentPlans) { plan ->
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
                                Text(plan.problemTitle, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Badge(containerColor = FarmGreenContainer, contentColor = FarmGreenDark) {
                                    Text(plan.status, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Fee Status: ${plan.feeStatus}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Organic Option: ${plan.organicOption}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Chemical Option: ${plan.chemicalOption}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Safety: ${plan.safetyNotes}", fontSize = 11.sp, color = FarmRedError)
                        }
                    }
                }
            }
            2 -> {
                // CONSULTATIONS
                items(consultations) { cons ->
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
                                Text("Session #${cons.id}", fontWeight = FontWeight.Bold)
                                Badge(containerColor = FarmGreenContainer, contentColor = FarmGreenDark) {
                                    Text(cons.status, fontSize = 10.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            // Phone visible because it is paid/scheduled
                            Text("Farmer: ${cons.farmerName} (${cons.farmerLga} LGA)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text("Phone: ${cons.farmerPhone}", fontSize = 12.sp, color = FarmGreenDark, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Problem: ${cons.problemText}", fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Timing: ${cons.scheduledTime}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
