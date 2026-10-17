package com.example.data.model

data class ColorOption(
  val name: String,
  val hexCode: Long
)

data class SizeGuideItem(
  val size: String,
  val bust: String,
  val waist: String,
  val hip: String,
  val length: String
)

data class ReviewItem(
  val reviewerName: String,
  val rating: Int,
  val date: String,
  val size: String,
  val color: String,
  val comment: String,
  val verified: Boolean = true
)

data class Product(
  val id: String,
  val name: String,
  val brand: String,
  val category: String,
  val price: Long,
  val originalPrice: Long,
  val discountPercent: Int,
  val rating: Float,
  val reviewCount: Int,
  val soldCount: Int,
  val description: String,
  val material: String,
  val careGuide: String,
  val fitGuide: String,
  val imageUrl: String,
  val colors: List<ColorOption>,
  val sizes: List<String>,
  val tags: List<String>,
  val isBestSeller: Boolean = false,
  val isNewArrival: Boolean = false
)

data class CartItem(
  val id: String,
  val product: Product,
  val selectedColor: ColorOption,
  val selectedSize: String,
  val quantity: Int
)

enum class OrderStatus(val label: String, val stepIndex: Int) {
  MENUNGGU_PEMBAYARAN("Menunggu Pembayaran", 0),
  PESANAN_DITERIMA("Pesanan Diterima", 1),
  SEDANG_DIKEMAS("Sedang Dikemas Butik", 2),
  DISERAHKAN_KE_KURIR("Diserahkan ke Kurir", 3),
  DALAM_PENGIRIMAN("Dalam Pengiriman", 4),
  PESANAN_SELESAI("Pesanan Selesai", 5)
}

data class OrderTimelineStep(
  val title: String,
  val time: String,
  val description: String,
  val isCompleted: Boolean,
  val isCurrent: Boolean
)

data class Order(
  val id: String,
  val date: String,
  val items: List<CartItem>,
  val subtotal: Long,
  val discount: Long,
  val shippingFee: Long,
  val total: Long,
  val status: OrderStatus,
  val courierName: String,
  val trackingNumber: String,
  val recipientName: String,
  val recipientPhone: String,
  val deliveryAddress: String,
  val paymentMethod: String,
  val driverName: String,
  val driverVehicle: String,
  val driverPhone: String,
  val estimatedDelivery: String,
  val progressPercent: Float,
  val timeline: List<OrderTimelineStep>
)

data class ShippingCourier(
  val id: String,
  val name: String,
  val serviceType: String,
  val fee: Long,
  val estimatedTime: String,
  val isInstant: Boolean = false
)

data class PaymentMethodItem(
  val id: String,
  val name: String,
  val category: String,
  val details: String,
  val isPopular: Boolean = false
)

data class Voucher(
  val code: String,
  val discountPercent: Int,
  val maxDiscount: Long,
  val minSpend: Long,
  val description: String
)
