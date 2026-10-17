package com.example.data

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class FashionRepository {

  private val _products = MutableStateFlow<List<Product>>(FashionData.sampleProducts)
  val products: StateFlow<List<Product>> = _products.asStateFlow()

  private val _cartItems = MutableStateFlow<List<CartItem>>(
    listOf(
      // Seed initial item for great first impression
      CartItem(
        id = "cart_init_1",
        product = FashionData.sampleProducts[0],
        selectedColor = FashionData.sampleProducts[0].colors[0],
        selectedSize = "M",
        quantity = 1
      )
    )
  )
  val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

  private val _wishlistIds = MutableStateFlow<Set<String>>(setOf("prod_01", "prod_02"))
  val wishlistIds: StateFlow<Set<String>> = _wishlistIds.asStateFlow()

  private val _appliedVoucher = MutableStateFlow<Voucher?>(FashionData.availableVouchers[0])
  val appliedVoucher: StateFlow<Voucher?> = _appliedVoucher.asStateFlow()

  private val _orders = MutableStateFlow<List<Order>>(emptyList())
  val orders: StateFlow<List<Order>> = _orders.asStateFlow()

  // Default address
  private val _recipientName = MutableStateFlow("Siti Aisyah")
  val recipientName: StateFlow<String> = _recipientName.asStateFlow()

  private val _recipientPhone = MutableStateFlow("0812-8899-7722")
  val recipientPhone: StateFlow<String> = _recipientPhone.asStateFlow()

  private val _deliveryAddress = MutableStateFlow("Jl. Senopati Raya No. 42, Kebayoran Baru, Jakarta Selatan 12190")
  val deliveryAddress: StateFlow<String> = _deliveryAddress.asStateFlow()

  init {
    // Create an initial sample active order so user can immediately test order tracking!
    createInitialSampleOrder()
  }

  private fun createInitialSampleOrder() {
    val initialItem = CartItem(
      id = "item_demo",
      product = FashionData.sampleProducts[1],
      selectedColor = FashionData.sampleProducts[1].colors[0],
      selectedSize = "S",
      quantity = 1
    )
    val sampleOrder = Order(
      id = "NR-GRAB-82914",
      date = "Hari ini, 14:15 WIB",
      items = listOf(initialItem),
      subtotal = initialItem.product.price,
      discount = 50000L,
      shippingFee = 25000L,
      total = initialItem.product.price - 50000L + 25000L,
      status = OrderStatus.DALAM_PENGIRIMAN,
      courierName = "GrabExpress Instant",
      trackingNumber = "GRAB-EXP-99214738",
      recipientName = _recipientName.value,
      recipientPhone = _recipientPhone.value,
      deliveryAddress = _deliveryAddress.value,
      paymentMethod = "QRIS Instant (GoPay / ShopeePay)",
      driverName = "Rahmat Hidayat",
      driverVehicle = "Honda Vario 160 (B 4821 SQP)",
      driverPhone = "0813-9821-4455",
      estimatedDelivery = "Tiba dalam 25 menit (Sedang menuju lokasimu)",
      progressPercent = 0.75f,
      timeline = listOf(
        OrderTimelineStep(
          title = "Pesanan Dibuat & Terbayar",
          time = "14:15 WIB",
          description = "Pembayaran via QRIS berhasil diverifikasi otomatis.",
          isCompleted = true,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Dipacking & Quality Check Butik",
          time = "14:22 WIB",
          description = "Gaun diperiksa kualitas jahitannya, disetrika uap dan dikemas dengan box satin Norise Store by NORANISA.",
          isCompleted = true,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Diserahkan ke Kurir GrabExpress",
          time = "14:35 WIB",
          description = "Driver Rahmat Hidayat telah menerima paket dari Butik Norise Store Senopati.",
          isCompleted = true,
          isCurrent = false
        ),
        OrderTimelineStep(
          title = "Kurir Dalam Perjalanan ke Alamat Tujuan",
          time = "14:40 WIB",
          description = "Kurir sedang melaju menuju lokasi Anda (Radius 3.2 km).",
          isCompleted = true,
          isCurrent = true
        ),
        OrderTimelineStep(
          title = "Pesanan Tiba & Diterima",
          time = "Estimasi 15:05 WIB",
          description = "Paket diserahkan langsung ke penerima di alamat tujuan.",
          isCompleted = false,
          isCurrent = false
        )
      )
    )
    _orders.value = listOf(sampleOrder)
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
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm 'WIB'", Locale("id", "ID"))
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
          time = SimpleDateFormat("HH:mm 'WIB'", Locale("id", "ID")).format(Date()),
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
