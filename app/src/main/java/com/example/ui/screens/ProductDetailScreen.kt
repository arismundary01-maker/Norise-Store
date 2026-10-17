package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FashionData
import com.example.data.model.ColorOption
import com.example.data.model.Product
import com.example.ui.FashionViewModel
import com.example.ui.Screen
import com.example.ui.components.SizeGuideDialog
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
  product: Product,
  viewModel: FashionViewModel,
  onBack: () -> Unit,
  onNavigateToCart: () -> Unit,
  onNavigateToCheckout: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }

  val selectedColor by viewModel.selectedColor.collectAsState()
  val selectedSize by viewModel.selectedSize.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val showSizeGuide by viewModel.showSizeGuideDialog.collectAsState()

  var quantity by remember { mutableStateOf(1) }
  var showSnackbar by remember { mutableStateOf(false) }
  var snackbarText by remember { mutableStateOf("") }
  var showChatModal by remember { mutableStateOf(false) }

  val isWishlisted = wishlistIds.contains(product.id)

  Scaffold(
    bottomBar = {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .windowInsetsPadding(WindowInsets.navigationBars),
        shadowElevation = 12.dp,
        color = MaterialTheme.colorScheme.surface
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          // Chat with Boutique button
          OutlinedIconButton(
            onClick = { showChatModal = true },
            modifier = Modifier
              .size(48.dp)
              .testTag("detail_chat_button"),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = Icons.Outlined.Chat,
              contentDescription = "Chat Butik",
              tint = MaterialTheme.colorScheme.primary
            )
          }

          // Add to Cart button
          OutlinedButton(
            onClick = {
              val color = selectedColor ?: product.colors.first()
              viewModel.addToCart(product, quantity)
              snackbarText = "✓ Dimasukkan ke Keranjang (${color.name}, $selectedSize, x$quantity)"
              showSnackbar = true
            },
            modifier = Modifier
              .weight(1f)
              .height(48.dp)
              .testTag("detail_add_cart_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
              contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder.copy(
              brush = Brush.horizontalGradient(listOf(RosePrimary, ChampagneSecondary))
            )
          ) {
            Icon(Icons.Outlined.ShoppingBag, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Keranjang", fontWeight = FontWeight.Bold)
          }

          // Buy Now button
          Button(
            onClick = {
              val color = selectedColor ?: product.colors.first()
              viewModel.addToCart(product, quantity)
              onNavigateToCheckout()
            },
            modifier = Modifier
              .weight(1.2f)
              .height(48.dp)
              .testTag("detail_buy_now_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = MaterialTheme.colorScheme.primary
            )
          ) {
            Text("Beli Sekarang", fontWeight = FontWeight.Bold)
          }
        }
      }
    },
    snackbarHost = {
      if (showSnackbar) {
        Snackbar(
          modifier = Modifier.padding(16.dp),
          action = {
            TextButton(
              onClick = {
                showSnackbar = false
                onNavigateToCart()
              }
            ) {
              Text("Lihat", color = GoldAccent, fontWeight = FontWeight.Bold)
            }
          },
          dismissAction = {
            IconButton(onClick = { showSnackbar = false }) {
              Icon(Icons.Default.Close, contentDescription = "Tutup", tint = Color.White)
            }
          }
        ) {
          Text(snackbarText)
        }
      }
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .verticalScroll(rememberScrollState())
    ) {
      // 1. Image Header with Overlays
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(380.dp)
          .background(MaterialTheme.colorScheme.surfaceVariant)
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Gradient at top for clear icons
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
              Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
              )
            )
        )

        // Top Navigation Buttons
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          IconButton(
            onClick = onBack,
            modifier = Modifier
              .size(40.dp)
              .background(Color.White.copy(alpha = 0.85f), CircleShape)
              .testTag("detail_back_button")
          ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.Black)
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            IconButton(
              onClick = { viewModel.toggleWishlist(product.id) },
              modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
                .testTag("detail_wishlist_button")
            ) {
              val heartColor by animateColorAsState(
                targetValue = if (isWishlisted) RosePrimary else Color.DarkGray,
                label = "heart"
              )
              Icon(
                imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Favorit",
                tint = heartColor
              )
            }

            IconButton(
              onClick = onNavigateToCart,
              modifier = Modifier
                .size(40.dp)
                .background(Color.White.copy(alpha = 0.85f), CircleShape)
                .testTag("detail_cart_shortcut")
            ) {
              Icon(Icons.Outlined.ShoppingBag, contentDescription = "Keranjang", tint = Color.Black)
            }
          }
        }

        // Tags on bottom left
        Row(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          product.tags.forEach { tag ->
            Surface(
              shape = RoundedCornerShape(8.dp),
              color = Color.Black.copy(alpha = 0.65f)
            ) {
              Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }

      // 2. Product Information Card
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp)
      ) {
        Text(
          text = product.brand,
          style = MaterialTheme.typography.titleMedium.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = product.name,
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Price Section
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Text(
            text = formatRupiah(product.price),
            style = MaterialTheme.typography.displayMedium.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          )
          if (product.originalPrice > product.price) {
            Text(
              text = formatRupiah(product.originalPrice),
              style = MaterialTheme.typography.titleMedium.copy(
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray
              )
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = CoralRed.copy(alpha = 0.15f)
            ) {
              Text(
                text = "Hemat ${product.discountPercent}%",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = CoralRed,
                  fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Rating & Delivery badge row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${product.rating}",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Text(
              text = " (${product.reviewCount} ulasan • ${product.soldCount} terjual)",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
          }

          // Fast Delivery badge
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                Icons.Default.ElectricMoped,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "Grab Instant Ready",
                style = MaterialTheme.typography.labelSmall.copy(
                  color = MaterialTheme.colorScheme.onSecondaryContainer,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }
        }

        Divider(modifier = Modifier.padding(vertical = 14.dp))

        // 3. Color Selection
        Text(
          text = "Pilihan Warna: ${(selectedColor ?: product.colors.first()).name}",
          style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          product.colors.forEach { colorOpt ->
            val isColorSelected = (selectedColor?.name ?: product.colors.first().name) == colorOpt.name
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier
                .clickable { viewModel.setColor(colorOpt) }
                .testTag("color_option_${colorOpt.name}")
            ) {
              Box(
                modifier = Modifier
                  .size(38.dp)
                  .clip(CircleShape)
                  .background(Color(colorOpt.hexCode))
                  .border(
                    width = if (isColorSelected) 3.dp else 1.dp,
                    color = if (isColorSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                    shape = CircleShape
                  ),
                contentAlignment = Alignment.Center
              ) {
                if (isColorSelected) {
                  Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = if (colorOpt.hexCode > 0xFF888888) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = colorOpt.name,
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = if (isColorSelected) FontWeight.Bold else FontWeight.Normal
                )
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. Size Selection + Size Guide link
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Pilih Ukuran (Size)",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          TextButton(
            onClick = { viewModel.toggleSizeGuide(true) },
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.testTag("open_size_guide_link")
          ) {
            Icon(Icons.Outlined.Straighten, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "Panduan Ukuran (cm)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          product.sizes.forEach { size ->
            val isSizeSelected = selectedSize == size
            FilterChip(
              selected = isSizeSelected,
              onClick = { viewModel.setSize(size) },
              label = {
                Text(
                  text = size,
                  fontWeight = if (isSizeSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White
              ),
              modifier = Modifier.testTag("size_chip_$size")
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Quantity Stepper
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Jumlah Pesanan",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )

          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
              .padding(4.dp)
          ) {
            IconButton(
              onClick = { if (quantity > 1) quantity-- },
              modifier = Modifier.size(32.dp).testTag("quantity_decrease")
            ) {
              Icon(Icons.Default.Remove, contentDescription = "Kurang", modifier = Modifier.size(16.dp))
            }
            Text(
              text = quantity.toString(),
              style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
              modifier = Modifier.padding(horizontal = 12.dp)
            )
            IconButton(
              onClick = { quantity++ },
              modifier = Modifier.size(32.dp).testTag("quantity_increase")
            ) {
              Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(16.dp))
            }
          }
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // 6. Product Description & Specifications
        Text(
          text = "Deskripsi Busana",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = product.description,
          style = MaterialTheme.typography.bodyMedium.copy(
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row {
              Text("• Bahan: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
              Text(product.material, style = MaterialTheme.typography.bodySmall)
            }
            Row {
              Text("• Potongan: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
              Text(product.fitGuide, style = MaterialTheme.typography.bodySmall)
            }
            Row {
              Text("• Perawatan: ", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
              Text(product.careGuide, style = MaterialTheme.typography.bodySmall)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 7. Customer Reviews Section
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Ulasan Pembeli (${FashionData.sampleReviews.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(3.dp))
            Text("4.9 / 5.0", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        FashionData.sampleReviews.forEach { review ->
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(28.dp)
                      .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    Text(
                      text = review.reviewerName.take(1),
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer,
                      style = MaterialTheme.typography.labelSmall
                    )
                  }
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(review.reviewerName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text("Varian: ${review.color}, Size ${review.size}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                  }
                }
                Text(review.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
              }
              Spacer(modifier = Modifier.height(6.dp))
              Row {
                repeat(review.rating) {
                  Icon(Icons.Filled.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                }
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(review.comment, style = MaterialTheme.typography.bodySmall)
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Size Guide Dialog
  if (showSizeGuide) {
    SizeGuideDialog(onDismiss = { viewModel.toggleSizeGuide(false) })
  }

  // Boutique Customer Service Chat simulation dialog
  if (showChatModal) {
    AlertDialog(
      onDismissRequest = { showChatModal = false },
      confirmButton = {
        Button(
          onClick = { showChatModal = false },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("Tutup Konsultasi")
        }
      },
      title = {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Outlined.SupportAgent, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          Spacer(modifier = Modifier.width(8.dp))
          Text("Konsultan Busana Butik", style = MaterialTheme.typography.titleMedium)
        }
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = "Halo Kak! Ada yang bisa kami bantu terkait busana \"${product.name}\"?",
            style = MaterialTheme.typography.bodyMedium
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Text("• Stok warna ready kirim hari ini via GrabExpress.", style = MaterialTheme.typography.bodySmall)
              Text("• Garansi tukar size dalam 3 hari bila kekecilan/kebesaran.", style = MaterialTheme.typography.bodySmall)
              Text("• Free konsultasi padu padan hijab/aksesori.", style = MaterialTheme.typography.bodySmall)
            }
          }
        }
      }
    )
  }
}
