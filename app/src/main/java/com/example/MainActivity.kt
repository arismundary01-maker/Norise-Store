package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Product
import com.example.ui.FashionViewModel
import com.example.ui.Screen
import com.example.ui.components.BoutiqueBottomNavigation
import com.example.ui.components.BoutiqueTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        NoriseStorApp()
      }
    }
  }
}

@Composable
fun NoriseStorApp(viewModel: FashionViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()
  val cartItems by viewModel.cartItems.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val orders by viewModel.orders.collectAsState()

  val activeOrdersCount = orders.count {
    it.status != com.example.data.model.OrderStatus.PESANAN_SELESAI
  }

  // Handle system back navigation across screens
  val canGoBack = currentScreen !is Screen.Home
  BackHandler(enabled = canGoBack) {
    viewModel.navigateBack()
  }

  val isBottomNavVisible = when (currentScreen) {
    is Screen.ProductDetail, is Screen.Checkout, is Screen.AdminLogin, is Screen.AdminDashboard -> false
    else -> true
  }

  val isTopBarVisible = when (currentScreen) {
    is Screen.ProductDetail, is Screen.Checkout, is Screen.OrderTracking, is Screen.AdminLogin, is Screen.AdminDashboard -> false
    else -> true
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      if (isTopBarVisible) {
        val topTitle = when (currentScreen) {
          is Screen.Catalog -> "Katalog Busana"
          is Screen.Cart -> "Keranjang Belanja"
          is Screen.OrdersHistory -> "Pesanan Saya"
          is Screen.Wishlist -> "Favorit Saya"
          is Screen.Profile -> "Profil Akun"
          else -> "Norise Store"
        }
        val topSubtitle = if (currentScreen is Screen.Home) "Elegan Women by NORANISA" else null

        BoutiqueTopBar(
          title = topTitle,
          subtitle = topSubtitle,
          cartCount = cartItems.sumOf { it.quantity },
          wishlistCount = wishlistIds.size,
          onCartClick = { viewModel.navigateTo(Screen.Cart) },
          onWishlistClick = { viewModel.navigateTo(Screen.Wishlist) },
          onSearchClick = {
            viewModel.navigateTo(Screen.Home)
          },
          canNavigateBack = canGoBack,
          onBackClick = { viewModel.navigateBack() }
        )
      }
    },
    bottomBar = {
      if (isBottomNavVisible) {
        BoutiqueBottomNavigation(
          currentScreen = currentScreen,
          cartCount = cartItems.sumOf { it.quantity },
          activeOrderCount = activeOrdersCount,
          onNavigate = { screen ->
            viewModel.navigateTo(screen)
          }
        )
      }
    }
  ) { innerPadding ->
    Crossfade(
      targetState = currentScreen,
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding),
      label = "screen_crossfade"
    ) { screen ->
      when (screen) {
        is Screen.Home -> {
          HomeScreen(
            viewModel = viewModel,
            onProductClick = { product -> viewModel.selectProduct(product) },
            onExploreCatalog = { viewModel.navigateTo(Screen.Catalog) }
          )
        }
        is Screen.Catalog -> {
          HomeScreen(
            viewModel = viewModel,
            onProductClick = { product -> viewModel.selectProduct(product) },
            onExploreCatalog = { /* Already in catalog */ }
          )
        }
        is Screen.ProductDetail -> {
          ProductDetailScreen(
            product = screen.product,
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() },
            onNavigateToCart = { viewModel.navigateTo(Screen.Cart) },
            onNavigateToCheckout = { viewModel.navigateTo(Screen.Checkout) }
          )
        }
        is Screen.Cart -> {
          CartScreen(
            viewModel = viewModel,
            onNavigateToCheckout = { viewModel.navigateTo(Screen.Checkout) },
            onStartShopping = { viewModel.navigateTo(Screen.Home) }
          )
        }
        is Screen.Checkout -> {
          CheckoutScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() },
            onOrderCreated = { orderId ->
              viewModel.navigateTo(Screen.OrderTracking(orderId))
            }
          )
        }
        is Screen.OrderTracking -> {
          OrderTrackingScreen(
            orderId = screen.orderId,
            viewModel = viewModel,
            onBack = {
              viewModel.navigateTo(Screen.OrdersHistory)
            }
          )
        }
        is Screen.OrdersHistory -> {
          OrdersHistoryScreen(
            viewModel = viewModel,
            onTrackOrder = { orderId ->
              viewModel.navigateTo(Screen.OrderTracking(orderId))
            },
            onStartShopping = { viewModel.navigateTo(Screen.Home) }
          )
        }
        is Screen.Wishlist -> {
          WishlistScreen(
            viewModel = viewModel,
            onProductClick = { product -> viewModel.selectProduct(product) },
            onStartShopping = { viewModel.navigateTo(Screen.Home) }
          )
        }
        is Screen.Profile -> {
          ProfileScreen(
            viewModel = viewModel,
            onNavigateToOrders = { viewModel.navigateTo(Screen.OrdersHistory) },
            onNavigateToWishlist = { viewModel.navigateTo(Screen.Wishlist) }
          )
        }
        is Screen.AdminLogin -> {
          AdminLoginScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateBack() },
            onLoginSuccess = { viewModel.navigateTo(Screen.AdminDashboard) }
          )
        }
        is Screen.AdminDashboard -> {
          AdminDashboardScreen(
            viewModel = viewModel,
            onBack = { viewModel.navigateTo(Screen.Home) },
            onLogout = { viewModel.navigateTo(Screen.Home) }
          )
        }
      }
    }
  }
}
