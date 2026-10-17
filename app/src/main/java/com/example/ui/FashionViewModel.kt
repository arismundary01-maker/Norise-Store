package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FashionData
import com.example.data.FashionRepository
import com.example.data.model.*
import kotlinx.coroutines.flow.*

sealed class Screen {
  object Home : Screen()
  object Catalog : Screen()
  data class ProductDetail(val product: Product) : Screen()
  object Cart : Screen()
  object Checkout : Screen()
  data class OrderTracking(val orderId: String) : Screen()
  object OrdersHistory : Screen()
  object Wishlist : Screen()
  object Profile : Screen()
  object AdminLogin : Screen()
  object AdminDashboard : Screen()
}

enum class SortOption(val title: String) {
  POPULAR("Paling Populer"),
  NEWEST("Terbaru"),
  RATING("Rating Tertinggi"),
  PRICE_LOW("Harga Terendah"),
  PRICE_HIGH("Harga Tertinggi")
}

class FashionViewModel(
  application: Application
) : AndroidViewModel(application) {

  val repository: FashionRepository = FashionRepository(application)

  private val _screenStack = MutableStateFlow<List<Screen>>(listOf(Screen.Home))
  val currentScreen: StateFlow<Screen> = _screenStack.map { it.lastOrNull() ?: Screen.Home }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Screen.Home)

  private val _selectedCategory = MutableStateFlow("Semua")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedSort = MutableStateFlow(SortOption.POPULAR)
  val selectedSort: StateFlow<SortOption> = _selectedSort.asStateFlow()

  private val _selectedProduct = MutableStateFlow<Product?>(null)
  val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

  private val _selectedColor = MutableStateFlow<ColorOption?>(null)
  val selectedColor: StateFlow<ColorOption?> = _selectedColor.asStateFlow()

  private val _selectedSize = MutableStateFlow("M")
  val selectedSize: StateFlow<String> = _selectedSize.asStateFlow()

  private val _selectedCourier = MutableStateFlow(FashionData.couriers[0]) // Default GrabExpress Instant
  val selectedCourier: StateFlow<ShippingCourier> = _selectedCourier.asStateFlow()

  private val _selectedPaymentMethod = MutableStateFlow(FashionData.paymentMethods[0]) // Default QRIS Instant
  val selectedPaymentMethod: StateFlow<PaymentMethodItem> = _selectedPaymentMethod.asStateFlow()

  private val _showSizeGuideDialog = MutableStateFlow(false)
  val showSizeGuideDialog: StateFlow<Boolean> = _showSizeGuideDialog.asStateFlow()

  private val _showAddressDialog = MutableStateFlow(false)
  val showAddressDialog: StateFlow<Boolean> = _showAddressDialog.asStateFlow()

  val cartItems = repository.cartItems
  val wishlistIds = repository.wishlistIds
  val appliedVoucher = repository.appliedVoucher
  val orders = repository.orders
  val recipientName = repository.recipientName
  val recipientPhone = repository.recipientPhone
  val deliveryAddress = repository.deliveryAddress
  val isAdminLoggedIn = repository.isAdminLoggedIn

  val filteredProducts: StateFlow<List<Product>> = combine(
    repository.products,
    _selectedCategory,
    _searchQuery,
    _selectedSort
  ) { products, category, query, sort ->
    var result = products

    if (category != "Semua") {
      result = result.filter { it.category.equals(category, ignoreCase = true) }
    }

    if (query.isNotBlank()) {
      val trimmed = query.trim().lowercase()
      result = result.filter {
        it.name.lowercase().contains(trimmed) ||
          it.brand.lowercase().contains(trimmed) ||
          it.description.lowercase().contains(trimmed) ||
          it.tags.any { tag -> tag.lowercase().contains(trimmed) }
      }
    }

    when (sort) {
      SortOption.POPULAR -> result.sortedByDescending { it.soldCount }
      SortOption.NEWEST -> result.sortedByDescending { if (it.isNewArrival) 1 else 0 }
      SortOption.RATING -> result.sortedByDescending { it.rating }
      SortOption.PRICE_LOW -> result.sortedBy { it.price }
      SortOption.PRICE_HIGH -> result.sortedByDescending { it.price }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun navigateTo(screen: Screen) {
    val current = _screenStack.value.toMutableList()
    if (current.lastOrNull() != screen) {
      current.add(screen)
      _screenStack.value = current
    }
  }

  fun navigateBack(): Boolean {
    val current = _screenStack.value.toMutableList()
    if (current.size > 1) {
      current.removeAt(current.size - 1)
      _screenStack.value = current
      return true
    }
    return false
  }

  fun selectProduct(product: Product) {
    _selectedProduct.value = product
    _selectedColor.value = product.colors.firstOrNull() ?: ColorOption("Rose Blush", 0xFF9E3A59)
    _selectedSize.value = product.sizes.firstOrNull() ?: "M"
    navigateTo(Screen.ProductDetail(product))
  }

  fun setCategory(category: String) {
    _selectedCategory.value = category
  }

  fun setSearchQuery(query: String) {
    _searchQuery.value = query
  }

  fun setSortOption(sort: SortOption) {
    _selectedSort.value = sort
  }

  fun setColor(color: ColorOption) {
    _selectedColor.value = color
  }

  fun setSize(size: String) {
    _selectedSize.value = size
  }

  fun setCourier(courier: ShippingCourier) {
    _selectedCourier.value = courier
  }

  fun setPaymentMethod(method: PaymentMethodItem) {
    _selectedPaymentMethod.value = method
  }

  fun toggleSizeGuide(show: Boolean) {
    _showSizeGuideDialog.value = show
  }

  fun toggleAddressDialog(show: Boolean) {
    _showAddressDialog.value = show
  }

  fun updateAddress(name: String, phone: String, address: String) {
    repository.updateAddress(name, phone, address)
    _showAddressDialog.value = false
  }

  fun toggleWishlist(productId: String) {
    repository.toggleWishlist(productId)
  }

  fun addToCartCurrentProduct(quantity: Int = 1) {
    val product = _selectedProduct.value ?: return
    val color = _selectedColor.value ?: product.colors.firstOrNull() ?: ColorOption("Rose", 0xFF9E3A59)
    val size = _selectedSize.value
    repository.addToCart(product, color, size, quantity)
  }

  fun addToCart(product: Product, quantity: Int = 1) {
    val color = product.colors.firstOrNull() ?: ColorOption("Default", 0xFF9E3A59)
    val size = product.sizes.firstOrNull() ?: "M"
    repository.addToCart(product, color, size, quantity)
  }

  fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
    repository.updateCartQuantity(cartItemId, newQuantity)
  }

  fun removeCartItem(cartItemId: String) {
    repository.removeCartItem(cartItemId)
  }

  fun applyVoucher(voucher: Voucher?) {
    repository.applyVoucher(voucher)
  }

  fun performCheckout(): Order {
    val subtotal = repository.getCartSubtotal()
    val discount = repository.getDiscountAmount(subtotal)
    val shippingFee = _selectedCourier.value.fee
    val total = (subtotal - discount + shippingFee).coerceAtLeast(0L)

    val newOrder = repository.checkout(
      courier = _selectedCourier.value,
      paymentMethod = _selectedPaymentMethod.value,
      subtotal = subtotal,
      discount = discount,
      shippingFee = shippingFee,
      total = total
    )

    _screenStack.value = listOf(Screen.Home, Screen.OrderTracking(newOrder.id))
    return newOrder
  }

  fun advanceTracking(orderId: String) {
    repository.advanceOrderStatus(orderId)
  }

  // Admin Features
  fun loginAdmin(pin: String): Boolean {
    val success = repository.loginAdmin(pin)
    if (success) {
      navigateTo(Screen.AdminDashboard)
    }
    return success
  }

  fun logoutAdmin() {
    repository.logoutAdmin()
    navigateTo(Screen.Home)
  }

  fun uploadProduct(
    product: Product,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    repository.uploadProduct(
      product = product,
      onSuccess = { onSuccess() },
      onError = onError
    )
  }

  fun deleteProduct(
    productId: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    repository.deleteProduct(productId, onSuccess, onError)
  }

  fun uploadPresetProduct(
    preset: Product,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    repository.uploadProduct(preset, { onSuccess() }, onError)
  }
}
