package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FashionData
import com.example.data.model.CartItem
import com.example.data.model.Voucher
import com.example.ui.FashionViewModel
import com.example.ui.Screen
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun CartScreen(
  viewModel: FashionViewModel,
  onNavigateToCheckout: () -> Unit,
  onStartShopping: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cartItems by viewModel.cartItems.collectAsState()
  val appliedVoucher by viewModel.appliedVoucher.collectAsState()
  var inputPromoCode by remember { mutableStateOf("") }
  var promoError by remember { mutableStateOf<String?>(null) }

  val subtotal = cartItems.sumOf { it.product.price * it.quantity }
  val discount = if (appliedVoucher != null && subtotal >= (appliedVoucher?.minSpend ?: 0L)) {
    val calc = (subtotal * (appliedVoucher?.discountPercent ?: 0)) / 100
    minOf(calc, appliedVoucher?.maxDiscount ?: 0L)
  } else 0L

  val estimatedShipping = if (cartItems.isNotEmpty()) 25000L else 0L
  val total = (subtotal - discount + estimatedShipping).coerceAtLeast(0L)

  Scaffold(
    bottomBar = {
      if (cartItems.isNotEmpty()) {
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars),
          shadowElevation = 12.dp,
          color = MaterialTheme.colorScheme.surface
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Total Pembayaran",
                  style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                  text = formatRupiah(total),
                  style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }

              Button(
                onClick = onNavigateToCheckout,
                modifier = Modifier
                  .height(48.dp)
                  .testTag("checkout_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                Text("Checkout (${cartItems.sumOf { it.quantity }})", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }
          }
        }
      }
    }
  ) { innerPadding ->
    if (cartItems.isEmpty()) {
      Box(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(24.dp)
        ) {
          Box(
            modifier = Modifier
              .size(90.dp)
              .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Outlined.ShoppingBag,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(48.dp)
            )
          }
          Spacer(modifier = Modifier.height(16.dp))
          Text(
            text = "Keranjang Belanja Masih Kosong",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Temukan berbagai gaun, tunik, dan busana wanita cantik di katalog butik kami.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(20.dp))
          Button(
            onClick = onStartShopping,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("empty_cart_browse_button")
          ) {
            Text("Jelajahi Busana Sekarang")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Daftar Belanjaan (${cartItems.size} Item)",
              style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            TextButton(
              onClick = { viewModel.repository.clearCart() },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text("Kosongkan", color = CoralRed, style = MaterialTheme.typography.bodySmall)
            }
          }
        }

        // Cart items list
        items(cartItems, key = { it.id }) { item ->
          CartItemCard(
            item = item,
            onQuantityChange = { newQty -> viewModel.updateCartQuantity(item.id, newQty) },
            onRemove = { viewModel.removeCartItem(item.id) }
          )
        }

        // Voucher / Promo Code Box
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = RosePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voucher & Kupon Diskon", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              }

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedTextField(
                  value = inputPromoCode,
                  onValueChange = {
                    inputPromoCode = it
                    promoError = null
                  },
                  placeholder = { Text("Masukkan kode kupon...") },
                  modifier = Modifier
                    .weight(1f)
                    .testTag("voucher_input"),
                  shape = RoundedCornerShape(10.dp),
                  singleLine = true
                )

                Button(
                  onClick = {
                    val code = inputPromoCode.trim().uppercase()
                    val match = FashionData.availableVouchers.find { it.code == code }
                    if (match != null) {
                      viewModel.applyVoucher(match)
                      promoError = null
                    } else {
                      promoError = "Kupon '$code' tidak valid"
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.testTag("apply_voucher_button")
                ) {
                  Text("Gunakan")
                }
              }

              if (promoError != null) {
                Text(
                  text = promoError ?: "",
                  color = CoralRed,
                  style = MaterialTheme.typography.bodySmall,
                  modifier = Modifier.padding(top = 4.dp)
                )
              }

              Spacer(modifier = Modifier.height(10.dp))

              Text(
                text = "Pilih kupon promo yang tersedia:",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
              )

              Spacer(modifier = Modifier.height(6.dp))

              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FashionData.availableVouchers.forEach { voucher ->
                  val isApplied = appliedVoucher?.code == voucher.code
                  SuggestionChip(
                    onClick = {
                      if (isApplied) viewModel.applyVoucher(null) else viewModel.applyVoucher(voucher)
                    },
                    label = { Text(voucher.code, fontWeight = FontWeight.Bold) },
                    icon = {
                      Icon(
                        if (isApplied) Icons.Default.Check else Icons.Outlined.Discount,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (isApplied) EmeraldGreen else MaterialTheme.colorScheme.primary
                      )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                      containerColor = if (isApplied) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.testTag("voucher_chip_${voucher.code}")
                  )
                }
              }

              if (appliedVoucher != null) {
                Surface(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                  shape = RoundedCornerShape(8.dp),
                  color = EmeraldGreen.copy(alpha = 0.1f)
                ) {
                  Row(
                    modifier = Modifier.padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = "Kupon Aktif: ${appliedVoucher?.description}",
                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldGreen, fontWeight = FontWeight.SemiBold)
                      )
                    }
                    IconButton(
                      onClick = { viewModel.applyVoucher(null) },
                      modifier = Modifier.size(24.dp)
                    ) {
                      Icon(Icons.Default.Close, contentDescription = "Hapus Kupon", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    }
                  }
                }
              }
            }
          }
        }

        // Summary Card
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(
              modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Text(
                text = "Ringkasan Belanja",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              Divider(modifier = Modifier.padding(vertical = 4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Subtotal Produk", style = MaterialTheme.typography.bodyMedium)
                Text(formatRupiah(subtotal), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
              }

              if (discount > 0) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text("Diskon Kupon (${appliedVoucher?.code})", style = MaterialTheme.typography.bodyMedium, color = EmeraldGreen)
                  Text("- ${formatRupiah(discount)}", style = MaterialTheme.typography.bodyMedium, color = EmeraldGreen, fontWeight = FontWeight.Bold)
                }
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Estimasi Ongkos Kirim (Grab/Kurir)", style = MaterialTheme.typography.bodyMedium)
                Text(formatRupiah(estimatedShipping), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
              }

              Divider(modifier = Modifier.padding(vertical = 4.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("Total Akhir", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Text(
                  formatRupiah(total),
                  style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                  )
                )
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun CartItemCard(
  item: CartItem,
  onQuantityChange: (Int) -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("cart_item_${item.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Thumbnail
      AsyncImage(
        model = item.product.imageUrl,
        contentDescription = item.product.name,
        contentScale = ContentScale.Crop,
        modifier = Modifier
          .size(85.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant)
      )

      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = item.product.brand,
              style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            )
            Text(
              text = item.product.name,
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
              maxLines = 1
            )
          }

          IconButton(
            onClick = onRemove,
            modifier = Modifier.size(24.dp).testTag("delete_cart_item_${item.id}")
          ) {
            Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(18.dp))
          }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Color and size badges
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(Color(item.selectedColor.hexCode))
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(item.selectedColor.name, style = MaterialTheme.typography.labelSmall)
            }
          }

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          ) {
            Text(
              text = "Size ${item.selectedSize}",
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Price and Quantity Stepper
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = formatRupiah(item.product.price),
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
          ) {
            IconButton(
              onClick = { onQuantityChange(item.quantity - 1) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(14.dp))
            }
            Text(
              text = item.quantity.toString(),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 8.dp)
            )
            IconButton(
              onClick = { onQuantityChange(item.quantity + 1) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(14.dp))
            }
          }
        }
      }
    }
  }
}
