package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HelpyBottomNavigationBar
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.FavoritesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.PublishBusinessDialog
import com.example.ui.screens.PublishProductDialog
import com.example.ui.screens.PublishScreen
import com.example.ui.screens.PublishServiceDialog
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.BackgroundAlabaster
import com.example.ui.theme.HelpyTheme
import com.example.ui.viewmodel.HelpyViewModel

@Composable
fun HelpyApp(viewModel: HelpyViewModel = viewModel()) {
    var hasStarted by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf("home") }
    var screenHistory by remember { mutableStateOf(listOf("home")) }

    var selectedProductId by remember { mutableStateOf("p1") }
    var chatSellerName by remember { mutableStateOf("Marie Style") }

    var showPublishProductDialog by remember { mutableStateOf(false) }
    var showPublishServiceDialog by remember { mutableStateOf(false) }
    var showPublishBusinessDialog by remember { mutableStateOf(false) }

    fun navigateTo(screen: String) {
        if (screen != currentScreen) {
            screenHistory = screenHistory + screen
            currentScreen = screen
        }
    }

    fun handleBack() {
        if (screenHistory.size > 1) {
            val updated = screenHistory.dropLast(1)
            screenHistory = updated
            currentScreen = updated.last()
        }
    }

    BackHandler(enabled = hasStarted && screenHistory.size > 1) {
        handleBack()
    }

    HelpyTheme {
        if (!hasStarted) {
            // Screen 1: Onboarding / Welcome Splash Screen
            OnboardingScreen(
                onStartClick = { hasStarted = true }
            )
        } else {
            val showBottomBar = currentScreen in listOf("home", "search", "categories", "messages", "profile", "publish")

            Scaffold(
                containerColor = BackgroundAlabaster,
                bottomBar = {
                    if (showBottomBar) {
                        HelpyBottomNavigationBar(
                            currentRoute = currentScreen,
                            onNavigate = { navigateTo(it) }
                        )
                    }
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentScreen) {
                        "home" -> HomeScreen(
                            viewModel = viewModel,
                            onNavigate = { navigateTo(it) },
                            onProductClick = { id ->
                                selectedProductId = id
                                navigateTo("detail")
                            },
                            onOpenPublishChooser = { navigateTo("publish") }
                        )

                        "categories" -> CategoriesScreen(
                            viewModel = viewModel,
                            onBack = { handleBack() },
                            onCategoryClick = { navigateTo("search") },
                            onProductClick = { id ->
                                selectedProductId = id
                                navigateTo("detail")
                            }
                        )

                        "search" -> SearchScreen(
                            viewModel = viewModel,
                            initialQuery = "iPhone",
                            onProductClick = { id ->
                                selectedProductId = id
                                navigateTo("detail")
                            }
                        )

                        "detail" -> ProductDetailScreen(
                            productId = selectedProductId,
                            viewModel = viewModel,
                            onBack = { handleBack() },
                            onContactClick = { product ->
                                chatSellerName = product.sellerName.ifBlank { "Marie Style" }
                                navigateTo("messages")
                            }
                        )

                        "messages" -> ChatScreen(
                            sellerName = chatSellerName,
                            onBack = { handleBack() }
                        )

                        "publish" -> PublishScreen(
                            onPublishProduct = { showPublishProductDialog = true },
                            onPublishService = { showPublishServiceDialog = true },
                            onPublishBusiness = { showPublishBusinessDialog = true }
                        )

                        "profile" -> ProfileScreen(
                            viewModel = viewModel,
                            onBack = { handleBack() },
                            onNavigateToFavorites = { navigateTo("favorites") },
                            onNavigateToMessages = { navigateTo("messages") }
                        )

                        "favorites" -> FavoritesScreen(
                            viewModel = viewModel,
                            onBack = { handleBack() }
                        )
                    }
                }
            }

            // Dialogs for publishing with photo picker
            if (showPublishProductDialog) {
                PublishProductDialog(
                    viewModel = viewModel,
                    onDismiss = { showPublishProductDialog = false }
                )
            }

            if (showPublishServiceDialog) {
                PublishServiceDialog(
                    viewModel = viewModel,
                    onDismiss = { showPublishServiceDialog = false }
                )
            }

            if (showPublishBusinessDialog) {
                PublishBusinessDialog(
                    viewModel = viewModel,
                    onDismiss = { showPublishBusinessDialog = false }
                )
            }
        }
    }
}
