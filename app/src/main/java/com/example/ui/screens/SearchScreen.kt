package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppConstants
import com.example.data.model.FavoriteItem
import com.example.ui.components.ProductSearchRowItem
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.HelpyGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun SearchScreen(
    viewModel: HelpyViewModel,
    initialQuery: String = "iPhone",
    onProductClick: (String) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favIds = favorites.map { it.id }.toSet()
    val selectedCountry by viewModel.selectedCountry.collectAsState()

    var searchQuery by remember { mutableStateOf(initialQuery) }
    var selectedConditionFilter by remember { mutableStateOf("Toutes") } // "Toutes", "Neuf", "Occasion"
    var locationDropdownExpanded by remember { mutableStateOf(false) }

    val filteredProducts = products.filter { product ->
        val matchesQuery = searchQuery.isBlank() ||
                product.title.contains(searchQuery, ignoreCase = true) ||
                product.category.contains(searchQuery, ignoreCase = true) ||
                product.description.contains(searchQuery, ignoreCase = true)

        val matchesCondition = when (selectedConditionFilter) {
            "Neuf" -> product.condition == "Neuf"
            "Occasion" -> product.condition == "Occasion" || product.condition == "Très bon état"
            else -> true
        }

        matchesQuery && matchesCondition
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search Input matching Screen 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un produit...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = "Effacer", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        IconButton(onClick = { /* Filter */ }, modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Tune, contentDescription = "Filtres", tint = TextDark, modifier = Modifier.size(20.dp))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_page_input"),
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BorderSubtle,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )
        }

        // Location Chip matching Screen 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { locationDropdownExpanded = true }
                        .clip(RoundedCornerShape(8.dp))
                        .background(BorderSubtle.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = HelpyGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (selectedCountry.contains("Haïti")) "Port-au-Prince" else selectedCountry,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextDark
                    )
                }

                DropdownMenu(
                    expanded = locationDropdownExpanded,
                    onDismissRequest = { locationDropdownExpanded = false }
                ) {
                    AppConstants.SUPPORTED_COUNTRIES.forEach { cnt ->
                        DropdownMenuItem(
                            text = { Text(cnt, fontSize = 13.sp) },
                            onClick = {
                                viewModel.selectedCountry.value = cnt
                                locationDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Filter Chips Row: [Toutes] [Neuf] [Occasion] [Prix ∨] matching Screen 4
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Toutes", "Neuf", "Occasion").forEach { filter ->
                val isSelected = filter == selectedConditionFilter
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) HelpyGold else SurfaceCard)
                        .clickable { selectedConditionFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.Black else TextDark
                    )
                }
            }

            // Price filter pill with dropdown arrow
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceCard)
                    .clickable { /* Price range filter */ }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Prix", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Vertical List matching Screen 4
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProducts) { product ->
                ProductSearchRowItem(
                    product = product,
                    isFavorite = favIds.contains(product.id),
                    onToggleFavorite = {
                        viewModel.toggleFavorite(
                            FavoriteItem(
                                id = product.id,
                                title = product.title,
                                type = "product",
                                category = product.category,
                                priceOrRate = "${product.price.toInt()} ${product.currency}",
                                location = product.city
                            ),
                            favIds.contains(product.id)
                        )
                    },
                    onClick = { onProductClick(product.id) }
                )
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Aucun produit trouvé pour '$searchQuery'",
                        color = TextMuted,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
