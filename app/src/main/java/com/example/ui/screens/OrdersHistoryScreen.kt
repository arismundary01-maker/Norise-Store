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
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.ui.FashionViewModel
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@Composable
fun OrdersHistoryScreen(
  viewModel: FashionViewModel,
  onTrackOrder: (String) -> Unit,
  onStartShopping: () -> Unit,
  modifier: Modifier = Modifier
) {
  val orders by viewModel.orders.collectAsState()
  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Semua", "Dalam Proses", "Selesai")

  val filteredOrders = remember(orders, selectedTab) {
    when (selectedTab) {
      1 -> orders.filter { it.status != OrderStatus.PESANAN_SELESAI }
      2 -> orders.filter { it.status == OrderStatus.PESANAN_SELESAI }
      else -> orders
    }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = "Pesanan Saya",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
    )

    TabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              text = title,
              fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
            )
          },
          modifier = Modifier.testTag("order_tab_$index")
        )
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (filteredOrders.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
          Icon(
            imageVector = Icons.Outlined.LocalShipping,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text("Belum Ada Pesanan", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Belum ada riwayat belanja untuk kategori ini.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onStartShopping,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text("Mulai Belanja")
          }
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        items(filteredOrders, key = { it.id }) { order ->
          OrderCard(
            order = order,
            onTrackClick = { onTrackOrder(order.id) }
          )
        }
      }
    }
  }
}

@Composable
fun OrderCard(
  order: Order,
  onTrackClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onTrackClick() }
      .testTag("order_card_${order.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Order ID & Status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(order.id, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
          Text(order.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when (order.status) {
            OrderStatus.PESANAN_SELESAI -> EmeraldGreen
            OrderStatus.DALAM_PENGIRIMAN -> RosePrimary
            else -> ChampagneSecondary
          }
        ) {
          Text(
            text = order.status.label,
            style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Divider(modifier = Modifier.padding(vertical = 10.dp))

      // Products preview
      order.items.forEach { item ->
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          AsyncImage(
            model = item.product.imageUrl,
            contentDescription = item.product.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
              .size(48.dp)
              .clip(RoundedCornerShape(8.dp))
          )
          Column(modifier = Modifier.weight(1f)) {
            Text(item.product.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text("Warna: ${item.selectedColor.name} | Size: ${item.selectedSize} (x${item.quantity})", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
          }
        }
      }

      Divider(modifier = Modifier.padding(vertical = 10.dp))

      // Footer
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text("Total Belanja", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
          Text(
            formatRupiah(order.total),
            style = MaterialTheme.typography.titleSmall.copy(
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
          )
        }

        Button(
          onClick = onTrackClick,
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
          shape = RoundedCornerShape(10.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.testTag("track_order_btn_${order.id}")
        ) {
          Icon(Icons.Default.ElectricMoped, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Lacak Paket", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
