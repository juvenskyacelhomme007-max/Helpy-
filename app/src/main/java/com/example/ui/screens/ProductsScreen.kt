package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppConstants
import com.example.data.model.FavoriteItem
import com.example.ui.components.CategoryFilterRow
import com.example.ui.components.ProductCard
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.InkSlate
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun ProductsScreen(
    viewModel: HelpyViewModel,
    onProductClick: (String) -> Unit
) {
    val products by viewModel.products.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val favIds = favorites.map { it.id }.toSet()

    var searchQuery by remember { mutableStateOf("") }

    val filteredProducts = products.filter {
        it.title.contains(searchQuery, ignoreCase = true) ||
        it.description.contains(searchQuery, ignoreCase = true) ||
        it.city.contains(searchQuery, ignoreCase = true) ||
        it.category.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Rechercher un téléphone, véhicule, pièce...", fontSize = 14.sp, color = TextMuted) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("search_products_input"),
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = InkSlate,
                unfocusedBorderColor = BorderSubtle,
                focusedContainerColor = SurfaceCard,
                unfocusedContainerColor = SurfaceCard
            )
        )

        CategoryFilterRow(
            categories = AppConstants.PRODUCT_CATEGORIES,
            selectedCategory = selectedCategory,
            onCategorySelected = { viewModel.selectedCategory.value = it }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(filteredProducts) { product ->
                Box(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProductCard(
                        product = product,
                        isFavorite = favIds.contains(product.id),
                        onToggleFavorite = {
                            viewModel.toggleFavorite(
                                FavoriteItem(
                                    id = product.id,
                                    title = product.title,
                                    type = "product",
                                    category = product.category,
                                    priceOrRate = "${product.price} ${product.currency}",
                                    location = "${product.city}, ${product.country}"
                                ),
                                favIds.contains(product.id)
                            )
                        },
                        onClick = { onProductClick(product.id) }
                    )
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                    Text(
                        text = "Aucune annonce ne correspond à votre recherche.",
                        color = TextMuted,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
