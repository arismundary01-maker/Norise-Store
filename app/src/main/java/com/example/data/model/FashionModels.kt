package com.example.data.model

data class ColorOption(
  val name: String = "",
  val hexCode: Long = 0xFF9E3A59
)

data class SizeGuideItem(
  val size: String = "",
  val bust: String = "",
  val waist: String = "",
  val hip: String = "",
  val length: String = ""
)

data class ReviewItem(
  val reviewerName: String = "",
  val rating: Int = 5,
  val date: String = "",
  val size: String = "",
  val color: String = "",
  val comment: String = "",
  val verified: Boolean = true
)

data class Product(
  val id: String = "",
  val name: String = "",
  val brand: String = "Elegan Women by NORANISA",
  val category: String = "Dress & Gaun",
  val price: Long = 0L,
  val originalPrice: Long = 0L,
  val discountPercent: Int = 0,
  val rating: Float = 5.0f,
  val reviewCount: Int = 0,
  val soldCount: Int = 0,
  val description: String = "",
  val material: String = "",
  val careGuide: String = "",
  val fitGuide: String = "",
  val imageUrl: String = "",
  val colors: List<ColorOption> = emptyList(),
  val sizes: List<String> = emptyList(),
  val tags: List<String> = emptyList(),
  val isBestSeller: Boolean = false,
  val isNewArrival: Boolean = false,
  val uploadedByAdmin: Boolean = true
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
