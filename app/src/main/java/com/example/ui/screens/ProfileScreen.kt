package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FashionData
import com.example.ui.FashionViewModel
import com.example.ui.components.AddressEditDialog
import com.example.ui.components.SizeGuideDialog
import com.example.ui.theme.*

@Composable
fun ProfileScreen(
  viewModel: FashionViewModel,
  onNavigateToOrders: () -> Unit,
  onNavigateToWishlist: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val recipientName by viewModel.recipientName.collectAsState()
  val recipientPhone by viewModel.recipientPhone.collectAsState()
  val deliveryAddress by viewModel.deliveryAddress.collectAsState()
  val showAddressDialog by viewModel.showAddressDialog.collectAsState()
  val showSizeGuide by viewModel.showSizeGuideDialog.collectAsState()
  val orders by viewModel.orders.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()

  Column(
    modifier = modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .padding(bottom = 80.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. VIP Member Profile Header Card
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.horizontalGradient(
              listOf(RosePrimary, DarkBurgundy)
            )
          )
          .padding(18.dp)
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(GoldAccent),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = recipientName.take(1),
              style = MaterialTheme.typography.headlineLarge.copy(color = Color.Black, fontWeight = FontWeight.Bold)
            )
          }

          Spacer(modifier = Modifier.width(14.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = recipientName,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.White, fontWeight = FontWeight.Bold)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = GoldAccent
              ) {
                Text(
                  text = "VIP Gold",
                  style = MaterialTheme.typography.labelSmall.copy(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = recipientPhone,
              style = MaterialTheme.typography.bodySmall.copy(color = Color.White.copy(alpha = 0.85f))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Pelanggan Setia Norise Store - Elegan Women by NORANISA",
              style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent)
            )
          }
        }
      }
    }

    // 2. Stats Row
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onNavigateToOrders() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Outlined.LocalShipping, contentDescription = null, tint = RosePrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("${orders.size} Pesanan", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Text("Riwayat Belanja", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
      }

      Card(
        modifier = Modifier
          .weight(1f)
          .clickable { onNavigateToWishlist() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Outlined.Favorite, contentDescription = null, tint = RosePrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("${wishlistIds.size} Busana", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Text("Favorit Saya", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
      }

      Card(
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(
          modifier = Modifier.padding(12.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Icon(Icons.Outlined.LocalOffer, contentDescription = null, tint = RosePrimary)
          Spacer(modifier = Modifier.height(4.dp))
          Text("${FashionData.availableVouchers.size} Kupon", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Text("Voucher Aktif", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
      }
    }

    // 3. Size Preferences Card
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
            Icon(Icons.Outlined.Straighten, contentDescription = null, tint = RosePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Preferensi Ukuran Saya", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          }
          TextButton(
            onClick = { viewModel.toggleSizeGuide(true) },
            contentPadding = PaddingValues(0.dp)
          ) {
            Text("Tabel Ukuran", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Ukuran Favorit", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
              Text("Size M", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = RosePrimary)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Lingkar Dada", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
              Text("88 - 92 cm", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text("Panjang Gaun", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
              Text("114 cm", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            }
          }
        }
      }
    }

    // 4. Delivery Address
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
            Icon(Icons.Outlined.Home, contentDescription = null, tint = RosePrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Alamat Pengiriman Utama", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          }
          TextButton(
            onClick = { viewModel.toggleAddressDialog(true) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.testTag("profile_edit_address_button")
          ) {
            Text("Ubah", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(recipientName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        Text(recipientPhone, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text(deliveryAddress, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      }
    }

    // 5. Customer Care & Guarantee
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Layanan & Keamanan Belanja", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .clickable {
              Toast.makeText(context, "Membuka WhatsApp Customer Care Norise Store by NORANISA...", Toast.LENGTH_SHORT).show()
            }
            .padding(vertical = 4.dp)
        ) {
          Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = RosePrimary)
          Spacer(modifier = Modifier.width(10.dp))
          Column(modifier = Modifier.weight(1f)) {
            Text("WhatsApp Customer Support", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text("Hubungi stylist butik untuk konsultasi ukuran & pesanan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
          }
          Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }

        Divider()

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
        ) {
          Icon(Icons.Outlined.VerifiedUser, contentDescription = null, tint = EmeraldGreen)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text("Garansi Retur / Tukar Size 3 Hari", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
            Text("Tukar ukuran mudah bila gaun tidak pas di badan", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
          }
        }
      }
    }

    // Footer info
    Box(
      modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Norise Store - Elegan Women by NORANISA", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
        Text("Versi 1.0 • GrabExpress Official Logistics Partner", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
      }
    }
  }

  // Dialogs
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

  if (showSizeGuide) {
    SizeGuideDialog(onDismiss = { viewModel.toggleSizeGuide(false) })
  }
}
