package com.example.smartfarm.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.ConsultationEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun ConsultationScreen(
    currentUser: UserEntity,
    consultations: List<ConsultationEntity>,
    onBookConsultation: (String, (String) -> Unit) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    val context = LocalContext.current
    var problemText by remember { mutableStateOf("My cassava stems have yellow mottled leaves and small tubers. Need advice on stem selection and field hygiene.") }
    var selectedSlot by remember { mutableStateOf("Today, 3:00 PM - 4:00 PM") }
    var showBookingHandoffId by remember { mutableStateOf<String?>(null) }
    var showExpertInfoDialog by remember { mutableStateOf(false) }

    val fee = Money.fromNaira(3000L) // ₦3,000

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

        // Expert Header Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(FarmGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SupportAgent,
                                contentDescription = null,
                                tint = FarmGreenDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Talk to Our Agricultural Expert",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "[Expert name to be added]",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Badge(
                            containerColor = FarmGreenContainer,
                            contentColor = FarmGreenDark
                        ) {
                            Text(
                                text = fee.toFormattedNaira(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Get direct 1-on-1 audio/video or WhatsApp advisory with an experienced extension consultant who has worked with thousands of Kogi farmers since 2007.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { showExpertInfoDialog = true },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Verified Consultant Badge Details", fontSize = 11.sp)
                    }
                }
            }
        }

        // Booking Form Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Book Your Session",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Choose Preferred Consultation Time:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedSlot.startsWith("Today"),
                                onClick = { selectedSlot = "Today, 3:00 PM - 4:00 PM" },
                                label = { Text("Today 3:00 PM", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = selectedSlot.startsWith("Tomorrow Morning"),
                                onClick = { selectedSlot = "Tomorrow Morning, 10:00 AM - 11:00 AM" },
                                label = { Text("Tomorrow 10:00 AM", fontSize = 11.sp) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = problemText,
                        onValueChange = { problemText = it },
                        label = { Text("Describe Your Cassava Problem") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Price Breakdown
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Consultation Fee:", fontSize = 13.sp)
                        Text(fee.toFormattedNaira(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = FarmGreenPrimary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            onBookConsultation(problemText) { consId ->
                                showBookingHandoffId = consId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("book_consultation_button")
                    ) {
                        Icon(imageVector = Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay ₦3,000 & Confirm Booking (Test)")
                    }
                }
            }
        }

        // Consultation History
        if (consultations.isNotEmpty()) {
            item {
                Text(
                    text = "My Booked Consultations",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            items(consultations) { item ->
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
                            Text("Session #${item.id}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Badge(
                                containerColor = FarmGreenContainer,
                                contentColor = FarmGreenDark
                            ) {
                                Text(item.status, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.problemText,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Timing: ${item.scheduledTime}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmGreenDark
                            )
                            TextButton(
                                onClick = { showBookingHandoffId = item.id },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                            ) {
                                Text("Contact Channels")
                            }
                        }
                    }
                }
            }
        }
    }

    // Booking Handoff Dialog (Section 6.3)
    showBookingHandoffId?.let { id ->
        AlertDialog(
            onDismissRequest = { showBookingHandoffId = null },
            title = {
                Text(
                    text = "Session Confirmed & Paid",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Your booking (#$id) is confirmed and ₦3,000 payment was successfully processed.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Choose your preferred channel to connect with our agricultural consultant:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // WhatsApp Button (Deep Link)
                    Button(
                        onClick = {
                            val url = "https://wa.me/2348000000000?text=Hello%20SmartFarm%20AI%20Expert,%20I%20have%20booked%20consultation%20$id"
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Chat, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open WhatsApp Chat")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Direct Call Button
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+2348000000000"))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Call, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Call Consultant Directly")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBookingHandoffId = null }) {
                    Text("Done")
                }
            }
        )
    }

    if (showExpertInfoDialog) {
        AlertDialog(
            onDismissRequest = { showExpertInfoDialog = false },
            title = { Text("About Verified Consultant Badge") },
            text = {
                Text(
                    "This is SmartFarm AI's own internal quality verification. It is not an official government certificate or civil service endorsement."
                )
            },
            confirmButton = {
                TextButton(onClick = { showExpertInfoDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
