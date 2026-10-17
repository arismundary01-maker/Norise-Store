package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.data.model.PaymentMethodItem
import com.example.data.model.ShippingCourier
import com.example.ui.FashionViewModel
import com.example.ui.Screen
import com.example.ui.components.AddressEditDialog
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit,
  onOrderCreated: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val cartItems by viewModel.cartItems.collectAsState()
  val appliedVoucher by viewModel.appliedVoucher.collectAsState()
  val selectedCourier by viewModel.selectedCourier.collectAsState()
  val selectedPayment by viewModel.selectedPaymentMethod.collectAsState()
  val recipientName by viewModel.recipientName.collectAsState()
  val recipientPhone by viewModel.recipientPhone.collectAsState()
  val deliveryAddress by viewModel.deliveryAddress.collectAsState()
  val showAddressDialog by viewModel.showAddressDialog.collectAsState()

  val subtotal = cartItems.sumOf { it.product.price * it.quantity }
  val discount = if (appliedVoucher != null && subtotal >= (appliedVoucher?.minSpend ?: 0L)) {
    val calc = (subtotal * (appliedVoucher?.discountPercent ?: 0)) / 100
    minOf(calc, appliedVoucher?.maxDiscount ?: 0L)
  } else 0L

  val shippingFee = selectedCourier.fee
  val total = (subtotal - discount + shippingFee).coerceAtLeast(0L)

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Checkout & Pembayaran", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("checkout_back_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    },
    bottomBar = {
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
              onClick = {
                val newOrder = viewModel.performCheckout()
                onOrderCreated(newOrder.id)
              },
              modifier = Modifier
                .height(48.dp)
                .testTag("pay_and_create_order_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Bayar Sekarang", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }
  ) { innerPadding ->
    LazyColumn(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 16.dp),
      contentPadding = PaddingValues(top = 12.dp, bottom = 100.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
      // 1. Delivery Address Card
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.LocationOn, contentDescription = null, tint = RosePrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Alamat Pengiriman",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
              }
              TextButton(
                onClick = { viewModel.toggleAddressDialog(true) },
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.testTag("edit_address_button")
              ) {
                Text("Ubah", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "$recipientName ($recipientPhone)",
              style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = deliveryAddress,
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }
        }
      }

      // 2. Shipping Courier Selection
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = RosePrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Pilih Kurir Pengiriman",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FashionData.couriers.forEach { courier ->
              val isSelected = selectedCourier.id == courier.id
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable { viewModel.setCourier(courier) }
                  .testTag("courier_option_${courier.id}"),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    RadioButton(
                      selected = isSelected,
                      onClick = { viewModel.setCourier(courier) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                      Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                          text = courier.name,
                          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        if (courier.isInstant) {
                          Spacer(modifier = Modifier.width(6.dp))
                          Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EmeraldGreen
                          ) {
                            Text(
                              text = "1-2 Jam",
                              style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 9.sp),
                              modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                          }
                        }
                      }
                      Text(
                        text = courier.serviceType,
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                      )
                      Text(
                        text = "Estimasi: ${courier.estimatedTime}",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontSize = 11.sp)
                      )
                    }
                  }

                  Text(
                    text = formatRupiah(courier.fee),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                  )
                }
              }
            }
          }
        }
      }

      // 3. Payment Method Selection
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Outlined.Payment, contentDescription = null, tint = RosePrimary)
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Metode Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FashionData.paymentMethods.forEach { method ->
              val isSelected = selectedPayment.id == method.id
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .border(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                  )
                  .clickable { viewModel.setPaymentMethod(method) }
                  .testTag("payment_method_${method.id}"),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  RadioButton(
                    selected = isSelected,
                    onClick = { viewModel.setPaymentMethod(method) }
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = method.name,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                      )
                      if (method.isPopular) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                          shape = RoundedCornerShape(4.dp),
                          color = GoldAccent
                        ) {
                          Text(
                            text = "Populer",
                            style = MaterialTheme.typography.labelSmall.copy(color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                          )
                        }
                      }
                    }
                    Text(
                      text = method.details,
                      style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    )
                  }
                }
              }
            }
          }
        }
      }

      // 4. Items summary preview
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Text(
              text = "Produk yang Dipesan (${cartItems.size})",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(10.dp))

            cartItems.forEach { item ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                AsyncImage(
                  model = item.product.imageUrl,
                  contentDescription = item.product.name,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp))
                )
                Column(modifier = Modifier.weight(1f)) {
                  Text(item.product.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                  Text("Varian: ${item.selectedColor.name} | Size ${item.selectedSize} (x${item.quantity})", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                Text(
                  formatRupiah(item.product.price * item.quantity),
                  style = MaterialTheme.typography.bodySmall,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // Price Details
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Subtotal", style = MaterialTheme.typography.bodySmall)
              Text(formatRupiah(subtotal), style = MaterialTheme.typography.bodySmall)
            }
            if (discount > 0) {
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Diskon Kupon", style = MaterialTheme.typography.bodySmall, color = EmeraldGreen)
                Text("- ${formatRupiah(discount)}", style = MaterialTheme.typography.bodySmall, color = EmeraldGreen, fontWeight = FontWeight.Bold)
              }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Ongkir (${selectedCourier.name})", style = MaterialTheme.typography.bodySmall)
              Text(formatRupiah(shippingFee), style = MaterialTheme.typography.bodySmall)
            }
            Divider(modifier = Modifier.padding(vertical = 6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text("Total Akhir", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
              Text(formatRupiah(total), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
            }
          }
        }
      }
    }
  }

  // Address Edit Modal
  if (showAddressDialog) {
    AddressEditDialog(
      currentName = recipientName,
      currentPhone = recipientPhone,
      currentAddress = deliveryAddress,
      onDismiss = { viewModel.toggleAddressDialog(false) },
      onSave = { name, phone, address ->
        viewModel.updateAddress(name, phone, address)
      }
    )
  }
}
