package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.*
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class FashionRepository(private val context: Context? = null) {

  private val _products = MutableStateFlow<List<Product>>(emptyList())
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _wishlistIds = MutableStateFlow<Set<String>>(emptySet())
  val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

  private val _appliedVoucher = MutableStateFlow<Voucher?>(FashionData.availableVouchers[0])
  val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(emptyList())
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  private val _isAdminLoggedIn = MutableStateFlow(false)
  val isAdminLoggedIn: StateFlow<Boolean> = _isAdminLoggedIn.asStateFlow()

  // Default address
  private val _recipientName = MutableStateFlow("Siti Aisyah")
  val recipientName: StateFlow<String> = _recipientName.asStateFlow()

  private val _recipientPhone = MutableStateFlow("0812-8899-7722")
  val recipientPhone: StateFlow<String> = _recipientPhone.asStateFlow()

  private val _deliveryAddress = MutableStateFlow("Jl. Senopati Raya No. 42, Kebayoran Baru, Jakarta Selatan 12190")
  val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

  private val db: FirebaseFirestore? by lazy {
    try {
      if (context != null) {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
      } else {
        null
      }
    } catch (e: Exception) {
      Log.e("FashionRepository", "Firestore init error: ${e.message}")
      null
    }
  }

  init {
    listenToFirestoreProducts()
  }

  private fun listenToFirestoreProducts() {
    val firestore = db ?: return
    firestore.collection("products")
      .addSnapshotListener { snapshot, error ->
        if (error != null) {
          Log.w("FashionRepository", "Firestore listen error: ${error.message}")
          return@addSnapshotListener
        }
        if (snapshot != null) {
          val list = snapshot.documents.mapNotNull { doc ->
            try {
              val p = doc.toObject(Product::class.java)
              p?.copy(id = doc.id)
            } catch (e: Exception) {
              Log.e("FashionRepository", "Deserialize product error: ${e.message}")
              null
            }
          }
          // Only show clothes uploaded by admin on homepage
          _products.value = list.filter { it.uploadedByAdmin }
        }
      }
  }

  fun loginAdmin(pinOrPassword: String): Boolean {
    val trimmed = pinOrPassword.trim()
    if (trimmed == "norise2026" || trimmed == "admin" || trimmed == "admin123") {
      _isAdminLoggedIn.value = true
      return true
    }
    return false
  }

  fun logoutAdmin() {
    _isAdminLoggedIn.value = false
  }

  fun uploadProduct(
    product: Product,
    onSuccess: (String) -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val firestore = db
    val productId = if (product.id.isNotBlank() && !product.id.startsWith("preset_")) {
      product.id
    } else {
      "prod_${System.currentTimeMillis()}"
    }

    val productWithId = product.copy(id = productId, uploadedByAdmin = true)

    if (firestore != null) {
      val dataMap = hashMapOf<String, Any>(
        "id" to productId,
        "name" to productWithId.name,
        "brand" to productWithId.brand,
        "category" to productWithId.category,
        "price" to productWithId.price,
        "originalPrice" to productWithId.originalPrice,
        "discountPercent" to productWithId.discountPercent,
        "rating" to productWithId.rating.toDouble(),
        "reviewCount" to productWithId.reviewCount,
        "soldCount" to productWithId.soldCount,
        "description" to productWithId.description,
        "material" to productWithId.material,
        "careGuide" to productWithId.careGuide,
        "fitGuide" to productWithId.fitGuide,
        "imageUrl" to productWithId.imageUrl,
        "sizes" to productWithId.sizes,
        "tags" to productWithId.tags,
        "isBestSeller" to productWithId.isBestSeller,
        "isNewArrival" to productWithId.isNewArrival,
        "uploadedByAdmin" to true,
        "createdAt" to FieldValue.serverTimestamp(),
        "updatedAt" to FieldValue.serverTimestamp()
      )
      val colorMaps = productWithId.colors.map { mapOf("name" to it.name, "hexCode" to it.hexCode) }
      dataMap["colors"] = colorMaps

      firestore.collection("products").document(productId)
        .set(dataMap)
        .addOnSuccessListener {
          _products.value = listOf(productWithId) + _products.value.filter { it.id != productId }
          onSuccess(productId)
        }
        .addOnFailureListener { e ->
          Log.e("FashionRepository", "Upload failed: ${e.message}", e)
          onError(e.localizedMessage ?: "Gagal menyimpan busana ke Firestore")
        }
    } else {
      // Memory fallback for tests / local
      _products.value = listOf(productWithId) + _products.value.filter { it.id != productId }
      onSuccess(productId)
    }
  }

  fun deleteProduct(
    productId: String,
    onSuccess: () -> Unit = {},
    onError: (String) -> Unit = {}
  ) {
    val firestore = db
    if (firestore != null) {
      firestore.collection("products").document(productId)
        .delete()
        .addOnSuccessListener {
          _products.value = _products.value.filter { it.id != productId }
          onSuccess()
        }
        .addOnFailureListener { e ->
          onError(e.localizedMessage ?: "Gagal menghapus produk")
        }
    } else {
      _products.value = _products.value.filter { it.id != productId }
      onSuccess()
    }
  }

  fun updateAddress(name: String, phone: String, address: String) {
    _recipientName.value = name
    _recipientPhone.value = phone
    _deliveryAddress.value = address
  }

  fun toggleWishlist(productId: String) {
    val current = _wishlistIds.value.toMutableSet()
    if (current.contains(productId)) {
      current.remove(productId)
    } else {
      current.add(productId)
    }
    _wishlistIds.value = current
  }

  fun isWishlisted(productId: String): Boolean {
    return _wishlistIds.value.contains(productId)
  }

  fun addToCart(product: Product, color: ColorOption, size: String, quantity: Int = 1) {
    val current = _cartItems.value.toMutableList()
    val existingIndex = current.indexOfFirst {
      it.product.id == product.id && it.selectedColor.name == color.name && it.selectedSize == size
    }
    if (existingIndex >= 0) {
      val existing = current[existingIndex]
      current[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
    } else {
      current.add(
        CartItem(
          id = "cart_${System.currentTimeMillis()}",
          product = product,
          selectedColor = color,
          selectedSize = size,
          quantity = quantity
        )
      )
    }
    _cartItems.value = current
  }

  fun updateCartQuantity(cartItemId: String, newQuantity: Int) {
    if (newQuantity <= 0) {
      removeCartItem(cartItemId)
      return
    }
    val current = _cartItems.value.toMutableList()
    val index = current.indexOfFirst { it.id == cartItemId }
    if (index >= 0) {
      current[index] = current[index].copy(quantity = newQuantity)
      _cartItems.value = current
    }
  }

  fun removeCartItem(cartItemId: String) {
    _cartItems.value = _cartItems.value.filter { it.id != cartItemId }
  }

  fun clearCart() {
    _cartItems.value = emptyList()
  }

  fun applyVoucher(voucher: Voucher?) {
    _appliedVoucher.value = voucher
  }

  fun getCartSubtotal(): Long {
    return _cartItems.value.sumOf { it.product.price * it.quantity }
  }

  fun getDiscountAmount(subtotal: Long): Long {
    val voucher = _appliedVoucher.value ?: return 0L
    if (subtotal < voucher.minSpend) return 0L
    val calculated = (subtotal * voucher.discountPercent) / 100
    return minOf(calculated, voucher.maxDiscount)
  }

  fun checkout(
    courier: ShippingCourier,
    paymentMethod: PaymentMethodItem,
    subtotal: Long,
    discount: Long,
    shippingFee: Long,
    total: Long
  ): Order {
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale.getDefault())
    val orderId = "NR-${if (courier.isInstant) "GRAB" else "EXP"}-${Random().nextInt(90000) + 10000}"
    val newOrder = Order(
      id = orderId,
      date = sdf.format(Date()),
      items = _cartItems.value.toList(),
      subtotal = subtotal,
      discount = discount,
      shippingFee = shippingFee,
      total = total,
      status = OrderStatus.PESANAN_DITERIMA,
      courierName = courier.name,
      trackingNumber = "${courier.name.take(4).uppercase()}-${Random().nextInt(9000000) + 1000000}",
      recipientName = _recipientName.value,
      recipientPhone = _recipientPhone.value,
      deliveryAddress = _deliveryAddress.value,
      paymentMethod = paymentMethod.name,
      driverName = if (courier.isInstant) "Budi Santoso" else "Kurir Logistik ${courier.name}",
      driverVehicle = if (courier.isInstant) "Yamaha NMAX (B 3910 KXZ)" else "Mobil Box Logistik",
      driverPhone = "0812-9988-1122",
      estimatedDelivery = courier.estimatedTime,
      progressPercent = 0.25f,
      timeline = listOf(
        OrderTimelineStep(
          title = "Pesanan Berhasil Dibuat",
          time = SimpleDateFormat("HH:mm 'WIB'", Locale.getDefault()).format(Date()),
          description = "Metode pembayaran ${paymentMethod.name} berhasil diverifikasi.",
          isCompleted = true,
          isCurrent = true
        ),
        OrderTimelineStep(
          title = "Sedang Dikemas Butik",
          time = "Dalam antrean",
          description = "Tim butik sedang memeriksa kualitas gaun dan membungkus pesanan.",
          isCompleted = false,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Penyerahan ke Kurir ${courier.name}",
          time = "Menunggu pick-up",
          description = "Paket siap diserahkan kepada armada pengiriman.",
          isCompleted = false,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Dalam Perjalanan Menuju Alamat",
          time = "Estimasi: ${courier.estimatedTime}",
          description = "Kurir sedang meluncur ke alamat tujuan Anda.",
          isCompleted = false,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Paket Diterima",
          time = "-",
          description = "Penerima telah mengonfirmasi penerimaan paket.",
          isCompleted = false,
          isCurrent = false
        )
      )
    )

    _orders.value = listOf(newOrder) + _orders.value
    clearCart()
    return newOrder
  }

  fun advanceOrderStatus(orderId: String) {
    val list = _orders.value.toMutableList()
    val index = list.indexOfFirst { it.id == orderId }
    if (index >= 0) {
      val order = list[index]
      val nextStatus = when (order.status) {
        OrderStatus.MENUNGGU_PEMBAYARAN -> OrderStatus.PESANAN_DITERIMA
        OrderStatus.PESANAN_DITERIMA -> OrderStatus.SEDANG_DIKEMAS
        OrderStatus.SEDANG_DIKEMAS -> OrderStatus.DISERAHKAN_KE_KURIR
        OrderStatus.DISERAHKAN_KE_KURIR -> OrderStatus.DALAM_PENGIRIMAN
        OrderStatus.DALAM_PENGIRIMAN -> OrderStatus.PESANAN_SELESAI
        OrderStatus.PESANAN_SELESAI -> OrderStatus.PESANAN_SELESAI
      }

      val newProgress = when (nextStatus) {
        OrderStatus.MENUNGGU_PEMBAYARAN -> 0.1f
        OrderStatus.PESANAN_DITERIMA -> 0.25f
        OrderStatus.SEDANG_DIKEMAS -> 0.5f
        OrderStatus.DISERAHKAN_KE_KURIR -> 0.7f
        OrderStatus.DALAM_PENGIRIMAN -> 0.85f
        OrderStatus.PESANAN_SELESAI -> 1.0f
      }

      val updatedTimeline = order.timeline.mapIndexed { stepIdx, step ->
        val isCompleted = stepIdx <= nextStatus.stepIndex - 1
        val isCurrent = stepIdx == nextStatus.stepIndex - 1
        step.copy(isCompleted = isCompleted, isCurrent = isCurrent)
      }

      list[index] = order.copy(
        status = nextStatus,
        progressPercent = newProgress,
        timeline = updatedTimeline,
        estimatedDelivery = if (nextStatus == OrderStatus.PESANAN_SELESAI) "Pesanan Berhasil Tiba!" else order.estimatedDelivery
      )
      _orders.value = list
    }
  }
}
