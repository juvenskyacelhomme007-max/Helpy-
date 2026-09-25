package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.FavoriteItem
import com.example.data.model.Product
import com.example.ui.components.launchPhone
import com.example.ui.components.launchWhatsApp
import com.example.ui.theme.BorderSubtle
import com.example.ui.theme.HelpyGold
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    viewModel: HelpyViewModel,
    onBack: () -> Unit,
    onContactClick: (Product) -> Unit
) {
    val context = LocalContext.current
    val products by viewModel.products.collectAsState()
    val product = products.find { it.id == productId } ?: products.firstOrNull() ?: Product(
        title = "iPhone 15 Pro Max",
        price = 75000.0,
        currency = "G",
        condition = "Neuf",
        city = "Delmas, Port-au-Prince",
        description = "iPhone 15 Pro Max 256GB, état neuf, encore sous garantie. Tout fonctionne parfaitement. Téléphone original Apple."
    )

    val favorites by viewModel.favorites.collectAsState()
    val isFavorite = favorites.any { it.id == product.id }

    val imageModel = if (product.imageUrl.isNotBlank()) product.imageUrl else R.drawable.img_product_phone

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 85.dp)
        ) {
            // Hero Photo Section with Back & Favorite overlays matching Screen 5
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(imageModel)
                        .crossfade(true)
                        .error(R.drawable.img_product_phone)
                        .placeholder(R.drawable.img_product_phone)
                        .build(),
                    contentDescription = product.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Top bar overlay with Back & Favorite icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .clickable { onBack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = TextDark, modifier = Modifier.size(20.dp))
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.85f))
                            .clickable {
                                viewModel.toggleFavorite(
                                    FavoriteItem(
                                        id = product.id,
                                        title = product.title,
                                        type = "product",
                                        category = product.category,
                                        priceOrRate = "${product.price.toInt()} ${product.currency}",
                                        location = product.city
                                    ),
                                    isFavorite
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorite) Color(0xFFEF4444) else TextDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // 1/5 Pagination Badge bottom right matching Screen 5
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(14.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "1/5",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // White Detail Sheet matching Screen 5
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceCard, shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .padding(20.dp)
            ) {
                // Title
                Text(
                    text = product.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Price
                Text(
                    text = "${product.price.toInt()} ${product.currency}",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Badges & Location row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD1FAE5))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = product.condition,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF059669)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Icon(Icons.Default.Place, contentDescription = null, tint = TextMuted, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = product.city.ifBlank { "Delmas, Port-au-Prince" },
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Features Pill Badges matching Screen 5: [Livré par le vendeur] [Paiement sécurisé]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BorderSubtle.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Livré par le vendeur", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BorderSubtle.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = TextDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Paiement sécurisé", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Description Title
                Text(
                    text = "Description",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Description Body
                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = TextDark.copy(alpha = 0.8f),
                    lineHeight = 20.sp
                )
            }
        }

        // Sticky Bottom Buttons matching Screen 5: [Contacter] & [Acheter maintenant]
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(SurfaceCard)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { onContactClick(product) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("detail_contact_btn"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, TextDark)
            ) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = TextDark, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Contacter", color = TextDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Button(
                onClick = {
                    if (product.sellerWhatsApp.isNotBlank()) {
                        launchWhatsApp(context, product.sellerWhatsApp)
                    } else if (product.sellerPhone.isNotBlank()) {
                        launchPhone(context, product.sellerPhone)
                    } else {
                        onContactClick(product)
                    }
                },
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("detail_buy_now_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = HelpyGold,
                    contentColor = Color.Black
                )
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Acheter maintenant", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
