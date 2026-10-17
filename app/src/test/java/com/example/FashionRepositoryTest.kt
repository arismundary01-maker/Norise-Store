package com.example

import com.example.data.FashionData
import com.example.data.FashionRepository
import com.example.data.model.ColorOption
import com.example.data.model.OrderStatus
import com.example.data.model.Product
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
  fun testInitialDataIsCleanPerUserRequest() {
    // Initial homepage products are clean and empty until admin uploads
    assertTrue(repository.products.value.isEmpty())
  }

  @Test
  fun testAdminLogin() {
    assertFalse(repository.isAdminLoggedIn.value)
    val loginSuccess = repository.loginAdmin("norise2026")
    assertTrue(loginSuccess)
    assertTrue(repository.isAdminLoggedIn.value)

    repository.logoutAdmin()
    assertFalse(repository.isAdminLoggedIn.value)
  }

  @Test
  fun testAdminUploadAndHomepageRealtimeReflection() {
    val newProduct = Product(
      id = "test_dress_01",
      name = "Gaun Pesta Silk NORANISA",
      brand = "Elegan Women by NORANISA",
      category = "Dress & Gaun",
      price = 450000L,
      uploadedByAdmin = true
    )

    repository.uploadProduct(newProduct)

    val currentProducts = repository.products.value
    assertTrue(currentProducts.any { it.id == "test_dress_01" && it.uploadedByAdmin })
  }

  @Test
  fun testAddToCartAndQuantity() {
    val product = FashionData.adminPresetProducts[0]
    val color = product.colors.firstOrNull() ?: ColorOption("Rose", 0xFF9E3A59)
    val size = "M"

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
