package com.example.smartfarm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.smartfarm.data.model.ProductEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.TestModeBanner
import com.example.ui.theme.*

@Composable
fun InputCatalogueScreen(
    currentUser: UserEntity,
    products: List<ProductEntity>,
    cartItems: List<ProductEntity>,
    onAddToCart: (ProductEntity) -> Unit,
    onRemoveFromCart: (ProductEntity) -> Unit,
    onCheckout: ((String) -> Unit) -> Unit,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    var showComparison by remember { mutableStateOf(false) }
    var showCartDialog by remember { mutableStateOf(false) }
    var orderSuccessId by remember { mutableStateOf<String?>(null) }
    var showSupplierInfoDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Stem Cuttings", "Fertilizer", "Biological", "Crop Protection")

    val filteredProducts = products.filter {
        if (selectedCategory == "All") true else it.category == selectedCategory
    }

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

        // Header and Cart Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Farm Inputs & Stems",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Verified suppliers · NAFDAC/NASC registered labels",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }

                // Floating-style Cart Button
                Button(
                    onClick = { showCartDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                    modifier = Modifier.testTag("open_cart_button")
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cart (${cartItems.size})", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Comparison banner button
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showComparison = !showComparison }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CompareArrows,
                        contentDescription = null,
                        tint = FarmGreenDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Chemical vs Organic Comparison",
                            fontWeight = FontWeight.Bold,
                            color = FarmGreenDark,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Compare safety, effectiveness, and harvest waiting times",
                            fontSize = 11.sp,
                            color = FarmGreenDark.copy(alpha = 0.85f)
                        )
                    }
                    Icon(
                        imageVector = if (showComparison) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = FarmGreenDark
                    )
                }
            }
        }

        // Chemical vs Organic Comparison Card (Section 6.5)
        if (showComparison) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Input Comparison for Cassava Protection",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Organic Column
                        Surface(
                            color = FarmGreenContainer.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🌿 Organic / Biological Option",
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenDark
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Example: Bio-Neem Botanical Repellent & Trichoderma Soil Inoculant", fontSize = 12.sp)
                                Text("• Used for: Suppressing whiteflies, mites, and root rot microbes", fontSize = 12.sp)
                                Text("• Benefits: Safe for soil health, non-toxic to beneficial insects, no harsh residues", fontSize = 12.sp)
                                Text("• Harvest Interval: 0 days (safe to harvest immediately)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("• Ease of use: Simple dilution in standard sprayer", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Chemical Column
                        Surface(
                            color = FarmAmberLight.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "🧪 Chemical Option",
                                    fontWeight = FontWeight.Bold,
                                    color = FarmBrownEarth
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Example: Copper Hydroxide Bactericide", fontSize = 12.sp)
                                Text("• Used for: Fast knockdown of severe bacterial leaf blights", fontSize = 12.sp)
                                Text("• Benefits: Rapid curative action in heavy rainy outbreaks", fontSize = 12.sp)
                                Text("• Harvest Interval: 14 days waiting period before eating/selling roots", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("• Safety: Strictly requires goggles, gloves, and mask", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Choose the option you prefer for your cassava farm.",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Category filter chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                categories.forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat },
                        label = { Text(cat, fontSize = 12.sp) }
                    )
                }
            }
        }

        // Products List
        items(filteredProducts) { product ->
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.simpleName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = product.officialName,
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }

                        Badge(
                            containerColor = when (product.type) {
                                "ORGANIC_BIOLOGICAL" -> FarmGreenContainer
                                "CHEMICAL" -> FarmAmberLight
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            contentColor = when (product.type) {
                                "ORGANIC_BIOLOGICAL" -> FarmGreenDark
                                "CHEMICAL" -> FarmBrownEarth
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        ) {
                            Text(
                                text = when (product.type) {
                                    "ORGANIC_BIOLOGICAL" -> "Biological"
                                    "CHEMICAL" -> "Chemical"
                                    else -> "Certified Stem"
                                },
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = product.purpose,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Supplier & Verification Badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showSupplierInfoDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = FarmGreenPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = product.supplierName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmGreenDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Verification Info",
                            tint = FarmGreenDark,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    if (product.registrationNumber.isNotBlank()) {
                        Text(
                            text = "Registration: ${product.registrationNumber}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Price & Add to Cart
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = Money(product.priceKobo).toFormattedNaira(),
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = FarmGreenPrimary
                                )
                            )
                            Text(
                                text = "per ${product.unit}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = { onAddToCart(product) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("add_to_cart_${product.id.lowercase()}")
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add to Cart")
                        }
                    }
                }
            }
        }
    }

    // Cart & "Check and Pay" Dialog
    if (showCartDialog) {
        val subtotalKobo = cartItems.sumOf { it.priceKobo }
        val deliveryFeeKobo = if (cartItems.isNotEmpty()) 200000L else 0L // ₦2,000 quote
        val totalKobo = subtotalKobo + deliveryFeeKobo

        AlertDialog(
            onDismissRequest = { showCartDialog = false },
            title = {
                Text(
                    text = "Check and Pay",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (cartItems.isEmpty()) {
                        Text("Your cart is empty. Add products to check out.")
                    } else {
                        Text(
                            text = "Items in Order:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = item.simpleName,
                                    fontSize = 12.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = Money(item.priceKobo).toFormattedNaira(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                IconButton(
                                    onClick = { onRemoveFromCart(item) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        // Clear Fee Breakdown (Section 6.6)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Products Subtotal:", fontSize = 13.sp)
                            Text(Money(subtotalKobo).toFormattedNaira(), fontSize = 13.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Delivery to Kogi LGA:", fontSize = 13.sp)
                            Text(Money(deliveryFeeKobo).toFormattedNaira(), fontSize = 13.sp)
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total Payable:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(
                                Money(totalKobo).toFormattedNaira(),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = FarmGreenPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Surface(
                            color = FarmGreenContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "🎉 BONUS: Buying these recommended inputs waives your ₦500 Treatment Plan fee!",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FarmGreenDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                if (cartItems.isNotEmpty()) {
                    Button(
                        onClick = {
                            onCheckout { orderId ->
                                showCartDialog = false
                                orderSuccessId = orderId
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary),
                        modifier = Modifier.testTag("confirm_pay_input_order_button")
                    ) {
                        Text("Pay with Paystack (Test)")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCartDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    // Success dialog
    orderSuccessId?.let { orderId ->
        AlertDialog(
            onDismissRequest = { orderSuccessId = null },
            title = { Text("Order Placed Successfully!") },
            text = {
                Text(
                    "Order #$orderId has been placed successfully. Payment was confirmed and the supplier has been notified to prepare your inputs.\n\nYour ₦500 treatment plan fee has been automatically waived!"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        orderSuccessId = null
                        onNavigate(AppScreen.FARMER_ORDERS)
                    }
                ) {
                    Text("View My Orders")
                }
            }
        )
    }

    if (showSupplierInfoDialog) {
        AlertDialog(
            onDismissRequest = { showSupplierInfoDialog = false },
            title = { Text("Verified Supplier Badge") },
            text = {
                Text(
                    "Checked by SmartFarm AI for physical agro-dealer location, registered seed sources, and compliant product labels in Kogi State. Not an official government certification."
                )
            },
            confirmButton = {
                TextButton(onClick = { showSupplierInfoDialog = false }) {
                    Text("OK")
                }
            }
        )
    }
}
