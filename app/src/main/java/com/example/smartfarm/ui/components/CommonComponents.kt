package com.example.smartfarm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.data.model.UserRole
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartTopBar(
    title: String,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    currentUser: UserEntity,
    onRoleSelected: (UserRole) -> Unit,
    onLegalClicked: () -> Unit,
    onWebLinkClicked: () -> Unit = {}
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1
                )
                Text(
                    text = "${currentUser.name} (${currentUser.role.name})",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("nav_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 8.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FarmGreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "SmartFarm Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        },
        actions = {
            // Role switcher chip
            AssistChip(
                onClick = { showRoleMenu = true },
                label = { Text("Switch Role", fontSize = 11.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Role",
                        modifier = Modifier.size(16.dp)
                    )
                },
                modifier = Modifier
                    .padding(end = 4.dp)
                    .testTag("role_switcher_chip")
            )

            DropdownMenu(
                expanded = showRoleMenu,
                onDismissRequest = { showRoleMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("👨‍🌾 Musa Ibrahim (Farmer - Kogi)") },
                    onClick = {
                        onRoleSelected(UserRole.FARMER)
                        showRoleMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("🏭 Alhaji Garba (Garri Buyer)") },
                    onClick = {
                        onRoleSelected(UserRole.BUYER)
                        showRoleMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("📦 Kogi Agro-Allied (Supplier)") },
                    onClick = {
                        onRoleSelected(UserRole.SUPPLIER)
                        showRoleMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("🔬 Agricultural Expert (Consultant)") },
                    onClick = {
                        onRoleSelected(UserRole.CONSULTANT)
                        showRoleMenu = false
                    }
                )
                DropdownMenuItem(
                    text = { Text("⚙️ Admin Console (SmartFarm)") },
                    onClick = {
                        onRoleSelected(UserRole.ADMIN)
                        showRoleMenu = false
                    }
                )
            }

            IconButton(
                onClick = onWebLinkClicked,
                modifier = Modifier.testTag("web_link_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Web Access Link",
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onLegalClicked,
                modifier = Modifier.testTag("info_help_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Privacy & Legal"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        )
    )
}

@Composable
fun WebDirectAccessDialog(
    onDismiss: () -> Unit,
    webUrl: String = "https://ais-pre-ogle2dewyqnmnqjsmoznzh-43248366371.europe-west2.run.app"
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var copied by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(FarmGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = FarmGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Instant Web Access Link",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = FarmGreenContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "💡 No App Download Required: Farmers do not need to install an app or use phone storage. They can simply tap the web link to open SmartFarm AI immediately in Chrome, Opera Mini, or any mobile browser.",
                        fontSize = 12.sp,
                        color = FarmGreenDark,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Text(
                    text = "Direct Website Link:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Selectable / displayed URL box
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = webUrl,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy button
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("SmartFarm Web Link", webUrl)
                            clipboard.setPrimaryClip(clip)
                            copied = true
                            android.widget.Toast.makeText(context, "Link copied to clipboard!", android.widget.Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_web_link_button")
                    ) {
                        Icon(
                            imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (copied) "Copied!" else "Copy Link", fontSize = 12.sp)
                    }

                    // Open in Browser
                    Button(
                        onClick = {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(webUrl))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("open_browser_link_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Web", fontSize = 12.sp)
                    }
                }

                // WhatsApp Share Button
                Button(
                    onClick = {
                        val shareMessage = "Hello! Check your cassava leaf sickness and see current Kogi cassava market prices directly in your browser without downloading any app:\n\n$webUrl"
                        val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                            setPackage("com.whatsapp")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback to general share sheet if WhatsApp is not installed
                            val fallback = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(android.content.Intent.EXTRA_TEXT, shareMessage)
                            }
                            context.startActivity(android.content.Intent.createChooser(fallback, "Share SmartFarm AI Link"))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("share_whatsapp_link_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Link via WhatsApp to Farmers", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "🔗 App Link Integration: If a farmer taps this link on a phone where the SmartFarm AI app is installed, Android will open the app directly. If not, it opens smoothly in their web browser.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 15.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun TestModeBanner() {
    Surface(
        color = FarmGreenLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Badge(
                containerColor = FarmGreenPrimary,
                contentColor = Color.White
            ) {
                Text(
                    text = "PROTECTED",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Secure Paystack payments & milestone protections active.",
                fontSize = 12.sp,
                color = FarmGreenDark,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun DemoBanner(text: String = "AI Cassava Diagnostic Screening") {
    Surface(
        color = FarmGreenLight,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FarmGreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Diagnostic Verified",
                tint = FarmGreenPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = FarmGreenDark
            )
        }
    }
}

@Composable
fun TrustBadgeStrip() {
    Surface(
        color = FarmGreenContainer,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrustItem(icon = Icons.Default.Verified, label = "Verified Expert")
            TrustItem(icon = Icons.Default.Store, label = "Verified Inputs")
            TrustItem(icon = Icons.Default.Payments, label = "Clear Prices")
            TrustItem(icon = Icons.Default.SupportAgent, label = "Farmer Support")
        }
    }
}

@Composable
private fun TrustItem(icon: ImageVector, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FarmGreenDark,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = FarmGreenDark
        )
    }
}

@Composable
fun BigActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    badgeText: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp)
            .testTag("action_tile_${title.replace(" ", "_").lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(FarmGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = FarmGreenPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (badgeText != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Badge(
                            containerColor = FarmAmberAccent,
                            contentColor = Color.White
                        ) {
                            Text(badgeText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun EmptyStateView(
    icon: ImageVector,
    title: String,
    message: String,
    actionButtonText: String? = null,
    onActionClicked: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionButtonText != null && onActionClicked != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onActionClicked,
                modifier = Modifier
                    .height(48.dp)
                    .testTag("empty_state_action_button")
            ) {
                Text(actionButtonText)
            }
        }
    }
}
