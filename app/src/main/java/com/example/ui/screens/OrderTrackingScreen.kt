package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderTrackingScreen(
  orderId: String,
  viewModel: FashionViewModel,
  onBack: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val orders by viewModel.orders.collectAsState()
  val order = orders.find { it.id == orderId } ?: orders.firstOrNull()

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Pelacakan Pesanan Real-Time", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text(order?.id ?: orderId, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack, modifier = Modifier.testTag("tracking_back_button")) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    if (order == null) {
      Box(
        modifier = modifier.fillMaxSize().padding(innerPadding),
        contentAlignment = Alignment.Center
      ) {
        Text("Pesanan tidak ditemukan.")
      }
    } else {
      LazyColumn(
        modifier = modifier
          .fillMaxSize()
          .padding(innerPadding)
          .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // 1. Status Progress Header Card
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
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
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.White, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                  )
                }

                Text(
                  text = order.date,
                  style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                )
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Linear Progress
              LinearProgressIndicator(
                progress = { order.progressPercent },
                modifier = Modifier
                  .fillMaxWidth()
                  .height(8.dp)
                  .clip(RoundedCornerShape(4.dp)),
                color = when (order.status) {
                  OrderStatus.PESANAN_SELESAI -> EmeraldGreen
                  else -> RosePrimary
                },
                trackColor = MaterialTheme.colorScheme.surfaceVariant
              )

              Spacer(modifier = Modifier.height(10.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Outlined.Schedule, contentDescription = null, tint = RosePrimary, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = order.estimatedDelivery,
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                  )
                }

                // Interactive Simulator Button
                if (order.status != OrderStatus.PESANAN_SELESAI) {
                  Button(
                    onClick = {
                      viewModel.advanceTracking(order.id)
                      Toast.makeText(context, "Status pesanan diperbarui secara real-time!", Toast.LENGTH_SHORT).show()
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.testTag("advance_tracking_status_button")
                  ) {
                    Icon(Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Perbarui Status", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                  }
                }
              }
            }
          }
        }

        // 2. Interactive Map / Route Simulation Canvas
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(Icons.Default.ElectricMoped, contentDescription = null, tint = EmeraldGreen)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Live Tracking Pengiriman", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
                Surface(
                  shape = RoundedCornerShape(6.dp),
                  color = EmeraldGreen.copy(alpha = 0.15f)
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldGreen))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("LIVE GPS", style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 9.sp))
                  }
                }
              }

              Spacer(modifier = Modifier.height(10.dp))

              // Route Canvas
              val infiniteTransition = rememberInfiniteTransition(label = "pulse")
              val pulseAnim by infiniteTransition.animateFloat(
                initialValue = 0.95f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                  animation = tween(1000, easing = LinearEasing),
                  repeatMode = RepeatMode.Reverse
                ),
                label = "pulse_scale"
              )

              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .height(150.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(
                    Brush.verticalGradient(
                      colors = listOf(
                        Color(0xFFE8F5E9),
                        Color(0xFFF1F8E9)
                      )
                    )
                  )
              ) {
                Canvas(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                  val startY = size.height * 0.5f
                  val startX = 20f
                  val endX = size.width - 20f

                  // Draw route path
                  drawLine(
                    color = Color.LightGray,
                    start = Offset(startX, startY),
                    end = Offset(endX, startY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                  )

                  // Active progressed route
                  val currentDriverX = startX + (endX - startX) * order.progressPercent
                  drawLine(
                    color = EmeraldGreen,
                    start = Offset(startX, startY),
                    end = Offset(currentDriverX, startY),
                    strokeWidth = 6.dp.toPx(),
                    cap = StrokeCap.Round
                  )

                  // Store Point
                  drawCircle(
                    color = RosePrimary,
                    radius = 10.dp.toPx(),
                    center = Offset(startX, startY)
                  )

                  // Destination Point
                  drawCircle(
                    color = if (order.status == OrderStatus.PESANAN_SELESAI) EmeraldGreen else Color.DarkGray,
                    radius = 10.dp.toPx(),
                    center = Offset(endX, startY)
                  )

                  // Driver motorcycle indicator
                  drawCircle(
                    color = EmeraldGreen.copy(alpha = 0.3f),
                    radius = 16.dp.toPx() * pulseAnim,
                    center = Offset(currentDriverX, startY)
                  )
                  drawCircle(
                    color = EmeraldGreen,
                    radius = 11.dp.toPx(),
                    center = Offset(currentDriverX, startY)
                  )
                }

                // Labels overlay
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text("Butik Norise Store", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, color = RosePrimary)
                    Text("Senopati, JKT", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 9.sp)
                  }
                  Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                      text = if (order.status == OrderStatus.PESANAN_SELESAI) "Sampai Tujuan" else "Kurir Sedang Jalan",
                      fontWeight = FontWeight.Bold,
                      style = MaterialTheme.typography.labelSmall,
                      color = EmeraldGreen
                    )
                  }
                  Column(horizontalAlignment = Alignment.End) {
                    Text("Alamat Kamu", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    Text("Kebayoran Baru", style = MaterialTheme.typography.labelSmall, color = Color.Gray, fontSize = 9.sp)
                  }
                }
              }

              Spacer(modifier = Modifier.height(12.dp))

              // Courier driver details
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                  .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(42.dp)
                      .background(EmeraldGreen.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = EmeraldGreen)
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(order.driverName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(order.driverVehicle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text("No. Resi: ${order.trackingNumber}", style = MaterialTheme.typography.labelSmall, color = RosePrimary)
                  }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                  IconButton(
                    onClick = {
                      Toast.makeText(context, "Menghubungi driver ${order.driverName}...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                      .size(36.dp)
                      .background(Color.White, CircleShape)
                  ) {
                    Icon(Icons.Default.Phone, contentDescription = "Telepon", tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                  }
                  IconButton(
                    onClick = {
                      Toast.makeText(context, "Membuka chat dengan driver...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                      .size(36.dp)
                      .background(Color.White, CircleShape)
                  ) {
                    Icon(Icons.Default.Chat, contentDescription = "Chat", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                  }
                }
              }
            }
          }
        }

        // 3. Timeline Step History
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
          ) {
            Column(modifier = Modifier.padding(16.dp)) {
              Text(
                text = "Riwayat Status Pengiriman",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              Spacer(modifier = Modifier.height(14.dp))

              order.timeline.forEachIndexed { index, step ->
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  verticalAlignment = Alignment.Top
                ) {
                  // Timeline Node
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                  ) {
                    Box(
                      modifier = Modifier
                        .size(18.dp)
                        .background(
                          color = when {
                            step.isCompleted -> EmeraldGreen
                            step.isCurrent -> RosePrimary
                            else -> Color.LightGray
                          },
                          shape = CircleShape
                        ),
                      contentAlignment = Alignment.Center
                    ) {
                      if (step.isCompleted) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                      }
                    }

                    if (index < order.timeline.size - 1) {
                      Box(
                        modifier = Modifier
                          .width(2.dp)
                          .height(44.dp)
                          .background(if (step.isCompleted) EmeraldGreen else Color.LightGray.copy(alpha = 0.5f))
                      )
                    }
                  }

                  Spacer(modifier = Modifier.width(10.dp))

                  Column(modifier = Modifier.weight(1f).padding(bottom = 12.dp)) {
                    Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                      Text(
                        text = step.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                          fontWeight = if (step.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                          color = if (step.isCurrent) RosePrimary else MaterialTheme.colorScheme.onSurface
                        )
                      )
                      Text(step.time, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                      text = step.description,
                      style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                  }
                }
              }
            }
          }
        }

        // 4. Order Items & Recipient Summary
        item {
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
              Text(
                text = "Rincian Gaun & Pembayaran",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )

              order.items.forEach { item ->
                Row(
                  modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                  AsyncImage(
                    model = item.product.imageUrl,
                    contentDescription = item.product.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(45.dp).clip(RoundedCornerShape(8.dp))
                  )
                  Column(modifier = Modifier.weight(1f)) {
                    Text(item.product.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    Text("Varian: ${item.selectedColor.name} | Size ${item.selectedSize} (x${item.quantity})", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                  }
                  Text(formatRupiah(item.product.price * item.quantity), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                }
              }

              Divider(modifier = Modifier.padding(vertical = 4.dp))

              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Penerima", style = MaterialTheme.typography.bodySmall)
                Text("${order.recipientName} (${order.recipientPhone})", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Metode Pembayaran", style = MaterialTheme.typography.bodySmall)
                Text(order.paymentMethod, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
              }
              Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total Dibayar", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                Text(formatRupiah(order.total), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary))
              }
            }
          }
        }
      }
    }
  }
}
