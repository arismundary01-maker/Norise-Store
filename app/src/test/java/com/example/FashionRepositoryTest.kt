package com.example

import com.example.data.FashionData
import com.example.data.FashionRepository
import com.example.data.model.OrderStatus
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FashionRepositoryTest {

  private lateinit var repository: FashionRepository

  @Before
  fun setUp() {
    repository = FashionRepository()
  }

  @Test
  fun testInitialData() {
    assertTrue(repository.products.value.isNotEmpty())
    assertTrue(repository.orders.value.isNotEmpty())
  }

  @Test
  fun testAddToCartAndQuantity() {
    val product = FashionData.sampleProducts[0]
    val color = product.colors[0]
    val size = "M"

    val initialCount = repository.cartItems.value.size
    repository.addToCart(product, color, size, 2)

    val cartItem = repository.cartItems.value.find {
      it.product.id == product.id && it.selectedColor.name == color.name && it.selectedSize == size
    }
    assertNotNull(cartItem)
    assertTrue(cartItem!!.quantity >= 2)
  }

  @Test
  fun testVoucherDiscountCalculation() {
    val voucher = FashionData.availableVouchers.find { it.code == "NORISE20" }!!
    repository.applyVoucher(voucher)

    val subtotal = 200000L
    val discount = repository.getDiscountAmount(subtotal)
    // 20% of 200,000 = 40,000 (max discount is 50,000)
    assertEquals(40000L, discount)
  }

  @Test
  fun testWishlistToggle() {
    val productId = "prod_05"
    val isInitiallyWishlisted = repository.isWishlisted(productId)
    repository.toggleWishlist(productId)
    assertEquals(!isInitiallyWishlisted, repository.isWishlisted(productId))
  }

  @Test
  fun testCheckoutAndTrackingAdvancement() {
    val courier = FashionData.couriers[0]
    val payment = FashionData.paymentMethods[0]

    val order = repository.checkout(
      courier = courier,
      paymentMethod = payment,
      subtotal = 300000L,
      discount = 20000L,
      shippingFee = courier.fee,
      total = 305000L
    )

    assertNotNull(order)
    assertEquals(OrderStatus.PESANAN_DITERIMA, order.status)

    // Advance order
    repository.advanceOrderStatus(order.id)
    val updatedOrder = repository.orders.value.find { it.id == order.id }!!
    assertEquals(OrderStatus.SEDANG_DIKEMAS, updatedOrder.status)
  }
}
