package com.example.smartfarm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.BigActionTile
import com.example.smartfarm.ui.components.TrustBadgeStrip
import com.example.ui.theme.*
import java.util.Calendar

@Composable
fun FarmerHomeScreen(
    currentUser: UserEntity,
    onNavigate: (AppScreen) -> Unit
) {
    var showExpertInfoDialog by remember { mutableStateOf(false) }
    var showWebDialog by remember { mutableStateOf(false) }

    val greeting = remember {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good morning"
            hour < 17 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Hero card with generated farmland banner
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Load hero banner drawable
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val bannerResId = remember {
                        context.resources.getIdentifier(
                            "cassava_hero_banner_1791065865990",
                            "drawable",
                            context.packageName
                        )
                    }
                    if (bannerResId != 0) {
                        Image(
                            painter = painterResource(id = bannerResId),
                            contentDescription = "Cassava Farmland Kogi",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(FarmGreenDark)
                        )
                    }

                    // Scrim overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Text(
                            text = "$greeting, ${currentUser.name.split(" ").firstOrNull() ?: "Farmer"}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = FarmAmberAccent,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentUser.community}, ${currentUser.lga} LGA (Kogi)",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.9f))
                            )
                        }
                    }
                }
            }
        }

        item {
            TrustBadgeStrip()
        }

        // Web Link Access Card (Addressing farmer hesitation to install apps)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showWebDialog = true }
                    .testTag("web_link_access_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(FarmGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Web Access",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Use on Web (No App Needed)",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = FarmGreenDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Badge(
                                containerColor = FarmGreenPrimary,
                                contentColor = Color.White
                            ) {
                                Text("ZERO STORAGE", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Tap to get or share the direct website link for farmers who cannot download apps.",
                            fontSize = 11.sp,
                            color = FarmGreenDark.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Web Link Details",
                        tint = FarmGreenDark
                    )
                }
            }
        }

        item {
            Text(
                text = "What do you want to do today?",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Six Primary Large Action Tiles
        item {
            BigActionTile(
                title = "1. Check My Cassava",
                subtitle = "Take a leaf photo to screen for plant diseases (Free)",
                icon = Icons.Default.CameraAlt,
                badgeText = "FREE",
                onClick = { onNavigate(AppScreen.DISEASE_SCREENING) }
            )
        }

        item {
            BigActionTile(
                title = "2. Get Treatment Help",
                subtitle = "Localized expert plan · Free with recommended input order",
                icon = Icons.Default.Healing,
                badgeText = "₦500 or FREE",
                onClick = { onNavigate(AppScreen.TREATMENT_PLANS) }
            )
        }

        item {
            BigActionTile(
                title = "3. Buy Farm Inputs",
                subtitle = "Certified disease-free stems, fertilizers, safe pest control",
                icon = Icons.Default.ShoppingBag,
                badgeText = "VERIFIED",
                onClick = { onNavigate(AppScreen.INPUT_CATALOGUE) }
            )
        }

        item {
            BigActionTile(
                title = "4. Sell My Cassava",
                subtitle = "Post your cassava harvest to reach verified garri & root buyers",
                icon = Icons.Default.Storefront,
                badgeText = "50/50 PAY",
                onClick = { onNavigate(AppScreen.SELL_CASSAVA) }
            )
        }

        item {
            BigActionTile(
                title = "5. Check Cassava Market",
                subtitle = "Current prices across Anyigba, Lokoja, and Kabba markets",
                icon = Icons.Default.TrendingUp,
                badgeText = "UPDATED",
                onClick = { onNavigate(AppScreen.MARKET_PRICES) }
            )
        }

        item {
            BigActionTile(
                title = "6. Talk to an Agricultural Expert",
                subtitle = "Live consultation with verified extension specialist (₦3,000)",
                icon = Icons.Default.SupportAgent,
                badgeText = "LIVE",
                onClick = { onNavigate(AppScreen.CONSULTATION) }
            )
        }

        // Secondary Row (Quick Links)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    QuickLinkItem(
                        icon = Icons.Default.ReceiptLong,
                        label = "My Orders",
                        onClick = { onNavigate(AppScreen.FARMER_ORDERS) }
                    )
                    QuickLinkItem(
                        icon = Icons.Default.Yard,
                        label = "My Farm",
                        onClick = { onNavigate(AppScreen.SELL_CASSAVA) }
                    )
                    QuickLinkItem(
                        icon = Icons.Default.Gavel,
                        label = "Terms & Help",
                        onClick = { onNavigate(AppScreen.LEGAL_PRIVACY) }
                    )
                }
            }
        }

        // Meet Our Agricultural Expert Profile Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("expert_profile_card")
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
                                imageVector = Icons.Default.Person,
                                contentDescription = "Expert Avatar",
                                tint = FarmGreenDark,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Meet Our Agricultural Expert",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "[Expert name to be added]",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Verified SmartFarm Consultant Badge with Popover Trigger
                    Surface(
                        color = FarmGreenContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showExpertInfoDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = FarmGreenDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Verified SmartFarm AI Agricultural Consultant",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmGreenDark,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Disclaimer Info",
                                tint = FarmGreenDark,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Agricultural consultant & extension agent working with farmers since 2007.\n" +
                               "• Supported thousands of farmers across Kogi State with cassava, maize, & soybean advisory.\n" +
                               "• Specializes in cassava disease diagnosis, stem selection, and safe input management.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.CONSULTATION) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Book 1-on-1 Session (₦3,000)")
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showExpertInfoDialog) {
        AlertDialog(
            onDismissRequest = { showExpertInfoDialog = false },
            title = { Text("About Verified Consultant Badge") },
            text = {
                Text(
                    "This is SmartFarm AI's own internal quality and background check. It is not an official government certification or civil service endorsement."
                )
            },
            confirmButton = {
                TextButton(onClick = { showExpertInfoDialog = false }) {
                    Text("Understood")
                }
            }
        )
    }

    if (showWebDialog) {
        com.example.smartfarm.ui.components.WebDirectAccessDialog(
            onDismiss = { showWebDialog = false }
        )
    }
}

@Composable
private fun QuickLinkItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
