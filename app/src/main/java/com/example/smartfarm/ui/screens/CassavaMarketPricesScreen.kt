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
import com.example.smartfarm.data.model.MarketPriceEntity
import com.example.smartfarm.data.model.UserEntity
import com.example.smartfarm.domain.Money
import com.example.smartfarm.ui.AppScreen
import com.example.smartfarm.ui.components.EmptyStateView
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun CassavaMarketPricesScreen(
    currentUser: UserEntity,
    marketPrices: List<MarketPriceEntity>,
    onNavigate: (AppScreen) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }
    val forms = listOf("All", "Fresh Roots", "Garri (White)", "Dried Cassava Chips")

    val filteredPrices = marketPrices.filter {
        if (selectedFilter == "All") true else it.cassavaForm.contains(selectedFilter, ignoreCase = true)
    }

    val now = System.currentTimeMillis()
    val sevenDaysMillis = TimeUnit.DAYS.toMillis(7)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Cassava Market Prices",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Local Kogi State market benchmarks for roots, garri, and chips",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
            }
        }

        // Filter chips
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                forms.forEach { form ->
                    FilterChip(
                        selected = selectedFilter == form,
                        onClick = { selectedFilter = form },
                        label = { Text(form, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (filteredPrices.isEmpty()) {
            item {
                EmptyStateView(
                    icon = Icons.Default.TrendingUp,
                    title = "No Price Information Yet",
                    message = "Market prices are surveyed weekly by the SmartFarm AI team across major Kogi markets."
                )
            }
        } else {
            items(filteredPrices) { priceItem ->
                val isStale = (now - priceItem.updatedAt) > sevenDaysMillis
                val dateStr = remember(priceItem.updatedAt) {
                    val sdf = SimpleDateFormat("MMM d, yyyy", Locale.US)
                    sdf.format(Date(priceItem.updatedAt))
                }

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
                                    text = priceItem.location,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = priceItem.cassavaForm,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = FarmGreenDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            Badge(
                                containerColor = FarmGreenLight,
                                contentColor = FarmGreenDark
                            ) {
                                Text("VERIFIED BENCHMARK", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = Money(priceItem.priceKobo).toFormattedNaira(),
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = FarmGreenPrimary
                                    )
                                )
                                Text(
                                    text = "per ${priceItem.unit}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (priceItem.buyersLookingCount > 0) {
                                Surface(
                                    color = FarmGreenContainer,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Group,
                                            contentDescription = null,
                                            tint = FarmGreenDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${priceItem.buyersLookingCount} buyers searching",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = FarmGreenDark
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Source,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = priceItem.sourceLabel,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Updated: $dateStr",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // Stale Warning past 7 days (Section 6.7)
                        if (isStale) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                color = FarmAmberLight,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = FarmBrownEarth,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Old price: check before you sell (over 7 days old)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FarmBrownEarth
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = FarmGreenLight),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Have cassava ready to harvest?",
                        fontWeight = FontWeight.Bold,
                        color = FarmGreenDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "List your harvest directly to sell to processors with 50/50 milestone payout protection.",
                        fontSize = 12.sp,
                        color = FarmGreenDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { onNavigate(AppScreen.SELL_CASSAVA) },
                        colors = ButtonDefaults.buttonColors(containerColor = FarmGreenPrimary)
                    ) {
                        Text("List Cassava for Sale")
                    }
                }
            }
        }
    }
}
