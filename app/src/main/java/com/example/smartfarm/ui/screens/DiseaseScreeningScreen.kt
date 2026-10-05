package com.example.smartfarm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.DiseaseCaseEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.DemoBanner
import com.example.ui.theme.*

@Composable
fun DiseaseScreeningScreen(
    currentUser: UserEntity,
    latestResult: DiseaseCaseEntity?,
    isAnalyzing: Boolean,
    onAnalyze: (String) -> Unit,
    onClearResult: () -> Unit,
    onUpdateConsent: (Boolean, Boolean) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedSample by remember { mutableStateOf("mosaic") }
    var imageConsent by remember { mutableStateOf(currentUser.consentGiven) }
    var locationConsent by remember { mutableStateOf(currentUser.locationConsent) }
    var showTipsDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            DemoBanner("AI Diagnostic Screening: Based on verified Kogi cassava pathology guidelines.")
        }

        if (latestResult == null) {
            // STEP 1: Photo Guidelines & Tips Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = FarmAmberAccent,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "4 Tips for Clear Leaf Pictures",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        TipRow(number = "1", text = "Use bright daylight. Avoid harsh shadows or dark rooms.")
                        TipRow(number = "2", text = "Hold the leaf flat on your palm or a clean surface.")
                        TipRow(number = "3", text = "Fill the picture with one single affected leaf.")
                        TipRow(number = "4", text = "Take one close-up picture and one whole-plant picture.")
                    }
                }
            }

            // STEP 2: Leaf Image Preview & Sample Selector
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Select Sample Cassava Leaf to Screen",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Leaf Guide Asset Display
                        val guideResId = remember {
                            context.resources.getIdentifier(
                                "cassava_leaf_guide_1791065875241",
                                "drawable",
                                context.packageName
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FarmGreenLight)
                                .border(1.dp, FarmGreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (guideResId != 0) {
                                Image(
                                    painter = painterResource(id = guideResId),
                                    contentDescription = "Cassava Leaf Sample",
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Eco,
                                    contentDescription = null,
                                    tint = FarmGreenPrimary,
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Choose a test case for Demo Screening:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Sample selector chips
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = selectedSample == "mosaic",
                                    onClick = { selectedSample = "mosaic" },
                                    label = { Text("Mosaic Disease") }
                                )
                                FilterChip(
                                    selected = selectedSample == "bacterial",
                                    onClick = { selectedSample = "bacterial" },
                                    label = { Text("Bacterial Blight") }
                                )
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                FilterChip(
                                    selected = selectedSample == "mite",
                                    onClick = { selectedSample = "mite" },
                                    label = { Text("Green Mite") }
                                )
                                FilterChip(
                                    selected = selectedSample == "healthy",
                                    onClick = { selectedSample = "healthy" },
                                    label = { Text("Healthy Leaf") }
                                )
                                FilterChip(
                                    selected = selectedSample == "blurry",
                                    onClick = { selectedSample = "blurry" },
                                    label = { Text("Unclear / Low Conf.") }
                                )
                            }
                        }
                    }
                }
            }

            // STEP 3: Mandatory Consent & Optional Location
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = imageConsent,
                                onCheckedChange = {
                                    imageConsent = it
                                    onUpdateConsent(imageConsent, locationConsent)
                                },
                                modifier = Modifier.testTag("image_consent_checkbox")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "I agree to store this leaf photo to help check my cassava and improve SmartFarm AI. (Required)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = locationConsent,
                                onCheckedChange = {
                                    locationConsent = it
                                    onUpdateConsent(imageConsent, locationConsent)
                                }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Share farm location (Optional)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = "Helps map regional cassava pest outbreaks in Kogi State.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // STEP 4: Analyze Button
            item {
                Button(
                    onClick = { onAnalyze(selectedSample) },
                    enabled = imageConsent && !isAnalyzing,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("analyze_leaf_button")
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Analyzing leaf symptoms...")
                    } else {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Check Leaf Now (Free)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        } else {
            // RESULT SCREEN
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
                                text = "Screening Result",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            AssistChip(
                                onClick = onClearResult,
                                label = { Text("Check Another") },
                                leadingIcon = {
                                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (latestResult.isConfident && latestResult.label != null) {
                            // Confident result
                            Surface(
                                color = if (latestResult.label.contains("Healthy")) FarmGreenContainer else FarmAmberLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = latestResult.label,
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = if (latestResult.label.contains("Healthy")) FarmGreenDark else FarmBrownEarth
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Confidence: ${latestResult.confidenceGrade} (${(latestResult.confidence * 100).toInt()}%)",
                                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    LinearProgressIndicator(
                                        progress = { latestResult.confidence.toFloat() },
                                        color = if (latestResult.label.contains("Healthy")) FarmGreenPrimary else FarmAmberAccent,
                                        trackColor = Color.White,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "What this means:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = latestResult.problemExplanation,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "What to do next:",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = latestResult.whatToDoNext,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            if (!latestResult.label.contains("Healthy")) {
                                Button(
                                    onClick = { onNavigate(AppScreen.TREATMENT_PLANS) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("get_treatment_plan_button")
                                ) {
                                    Icon(Icons.Default.Healing, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Get Expert Treatment Plan (₦500)")
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            OutlinedButton(
                                onClick = { onNavigate(AppScreen.CONSULTATION) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Talk to Our Agricultural Expert (₦3,000)")
                            }
                        } else {
                            // Low-Confidence Result / Unclear Photo Handling
                            Surface(
                                color = FarmAmberLight,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.HelpOutline,
                                            contentDescription = null,
                                            tint = FarmBrownEarth,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Leaf Disease Not Recognized",
                                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                            color = FarmBrownEarth
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = "We are not sure from this picture. The leaf may be too blurry, too dark, or taken from too far away. We do not guess when we are not sure.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = FarmBrownEarth
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onClearResult,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Take Another Clear Picture")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = { onNavigate(AppScreen.CONSULTATION) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(Icons.Default.SupportAgent, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Ask Our Agricultural Expert Directly")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TipRow(number: String, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(FarmGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = FarmGreenDark
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
