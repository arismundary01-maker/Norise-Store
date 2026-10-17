package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FashionData
import com.example.data.model.Product
import com.example.ui.Screen
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

fun formatRupiah(amount: Long): String {
  val formatter = NumberFormat.getNumberInstance(Locale("id", "ID"))
  return "Rp " + formatter.format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoutiqueTopBar(
  title: String = "Norise Store",
  subtitle: String? = "Elegan Women by NORANISA",
  cartCount: Int,
  wishlistCount: Int,
  onCartClick: () -> Unit,
  onWishlistClick: () -> Unit,
  onSearchClick: () -> Unit,
  canNavigateBack: Boolean = false,
  onBackClick: () -> Unit = {}
) {
  TopAppBar(
    title = {
      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.headlineLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    },
    navigationIcon = {
      if (canNavigateBack) {
        IconButton(
          onClick = onBackClick,
          modifier = Modifier.testTag("back_button")
        ) {
          Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Kembali",
            tint = MaterialTheme.colorScheme.onSurface
          )
        }
      }
    },
    actions = {
      IconButton(
        onClick = onSearchClick,
        modifier = Modifier.testTag("topbar_search_button")
      ) {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = "Cari Baju",
          tint = MaterialTheme.colorScheme.onSurface
        )
      }
      IconButton(
        onClick = onWishlistClick,
        modifier = Modifier.testTag("topbar_wishlist_button")
      ) {
        BadgedBox(
          badge = {
            if (wishlistCount > 0) {
              Badge(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
              ) {
                Text(text = wishlistCount.toString(), style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        ) {
          Icon(
            imageVector = if (wishlistCount > 0) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Favorit",
            tint = if (wishlistCount > 0) RosePrimary else MaterialTheme.colorScheme.onSurface
          )
        }
      }
      IconButton(
        onClick = onCartClick,
        modifier = Modifier.testTag("topbar_cart_button")
      ) {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
              ) {
                Text(text = cartCount.toString(), style = MaterialTheme.typography.labelSmall)
              }
            }
          }
        ) {
          Icon(
            imageVector = Icons.Outlined.ShoppingBag,
            contentDescription = "Keranjang Belanja",
            tint = MaterialTheme.colorScheme.primary
          )
        }
      }
    },
    colors = TopAppBarDefaults.topAppBarColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  )
}

@Composable
fun BoutiqueBottomNavigation(
  currentScreen: Screen,
  cartCount: Int,
  activeOrderCount: Int,
  onNavigate: (Screen) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
    containerColor = MaterialTheme.colorScheme.surface,
    tonalElevation = 8.dp
  ) {
    NavigationBarItem(
      selected = currentScreen is Screen.Home,
      onClick = { onNavigate(Screen.Home) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Home) Icons.Filled.Home else Icons.Outlined.Home,
          contentDescription = "Beranda"
        )
      },
      label = { Text("Beranda") },
      modifier = Modifier.testTag("nav_home")
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Catalog,
      onClick = { onNavigate(Screen.Catalog) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Catalog) Icons.Filled.GridView else Icons.Outlined.GridView,
          contentDescription = "Koleksi"
        )
      },
      label = { Text("Katalog") },
      modifier = Modifier.testTag("nav_catalog")
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Cart,
      onClick = { onNavigate(Screen.Cart) },
      icon = {
        BadgedBox(
          badge = {
            if (cartCount > 0) {
              Badge(containerColor = MaterialTheme.colorScheme.primary) {
                Text(text = cartCount.toString())
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentScreen is Screen.Cart) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
            contentDescription = "Keranjang"
          )
        }
      },
      label = { Text("Keranjang") },
      modifier = Modifier.testTag("nav_cart")
    )
    NavigationBarItem(
      selected = currentScreen is Screen.OrdersHistory || currentScreen is Screen.OrderTracking,
      onClick = { onNavigate(Screen.OrdersHistory) },
      icon = {
        BadgedBox(
          badge = {
            if (activeOrderCount > 0) {
              Badge(containerColor = EmeraldGreen) {
                Text(text = activeOrderCount.toString())
              }
            }
          }
        ) {
          Icon(
            imageVector = if (currentScreen is Screen.OrdersHistory || currentScreen is Screen.OrderTracking)
              Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
            contentDescription = "Pesanan"
          )
        }
      },
      label = { Text("Pesanan") },
      modifier = Modifier.testTag("nav_orders")
    )
    NavigationBarItem(
      selected = currentScreen is Screen.Profile,
      onClick = { onNavigate(Screen.Profile) },
      icon = {
        Icon(
          imageVector = if (currentScreen is Screen.Profile) Icons.Filled.Person else Icons.Outlined.Person,
          contentDescription = "Akun"
        )
      },
      label = { Text("Akun") },
      modifier = Modifier.testTag("nav_profile")
    )
  }
}

@Composable
fun ProductCard(
  product: Product,
  isWishlisted: Boolean,
  onProductClick: () -> Unit,
  onWishlistToggle: () -> Unit,
  onAddToCart: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .clickable { onProductClick() }
      .testTag("product_card_${product.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp)
          .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
          .background(
            Brush.verticalGradient(
              colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.surfaceVariant
              )
            )
          )
      ) {
        AsyncImage(
          model = product.imageUrl,
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )

        // Discount Tag
        if (product.discountPercent > 0) {
          Surface(
            modifier = Modifier
              .padding(8.dp)
              .align(Alignment.TopStart),
            shape = RoundedCornerShape(8.dp),
            color = CoralRed
          ) {
            Text(
              text = "-${product.discountPercent}%",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Bold
              ),
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
            )
          }
        }

        // Wishlist button
        IconButton(
          onClick = onWishlistToggle,
          modifier = Modifier
            .padding(6.dp)
            .size(36.dp)
            .align(Alignment.TopEnd)
            .background(Color.White.copy(alpha = 0.85f), CircleShape)
            .testTag("wishlist_toggle_${product.id}")
        ) {
          val heartColor by animateColorAsState(
            targetValue = if (isWishlisted) RosePrimary else Color.Gray,
            label = "heart_color"
          )
          Icon(
            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = "Suka produk",
            tint = heartColor,
            modifier = Modifier.size(20.dp)
          )
        }

        // Color dots on card bottom
        Row(
          modifier = Modifier
            .padding(8.dp)
            .align(Alignment.BottomStart)
            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          product.colors.take(3).forEach { colorOpt ->
            Box(
              modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(Color(colorOpt.hexCode))
            )
          }
          if (product.colors.size > 3) {
            Text(
              text = "+${product.colors.size - 3}",
              style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontSize = 9.sp
              )
            )
          }
        }
      }

      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = product.brand,
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
          )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = product.name,
          style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold
          ),
          maxLines = 2,
          overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(6.dp))

        // Price Section
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Text(
            text = formatRupiah(product.price),
            style = MaterialTheme.typography.titleMedium.copy(
              color = MaterialTheme.colorScheme.primary,
              fontWeight = FontWeight.Bold
            )
          )
          if (product.originalPrice > product.price) {
            Text(
              text = formatRupiah(product.originalPrice),
              style = MaterialTheme.typography.bodySmall.copy(
                textDecoration = TextDecoration.LineThrough,
                color = Color.Gray,
                fontSize = 11.sp
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Rating & Sold
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Filled.Star,
              contentDescription = null,
              tint = GoldAccent,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${product.rating} (${product.soldCount} terjual)",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            )
          }

          IconButton(
            onClick = onAddToCart,
            modifier = Modifier
              .size(32.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
              .testTag("add_to_cart_quick_${product.id}")
          ) {
            Icon(
              imageVector = Icons.Default.Add,
              contentDescription = "Tambah Cepat",
              tint = MaterialTheme.colorScheme.onPrimaryContainer,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun SizeGuideDialog(
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.testTag("close_size_guide_button")
      ) {
        Text("Mengerti")
      }
    },
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Outlined.Straighten,
          contentDescription = null,
          tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Panduan Ukuran Busana Wanita",
          style = MaterialTheme.typography.titleLarge
        )
      }
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "Standar ukuran tubuh wanita Indonesia dalam sentimeter (cm):",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Header Table
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Size", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
          Text("LD (Dada)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f))
          Text("LP (Pinggang)", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f))
          Text("Pinggul", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f))
        }

        Spacer(modifier = Modifier.height(6.dp))

        FashionData.sizeGuideList.forEachIndexed { index, item ->
          val bg = if (index % 2 == 0) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else Color.Transparent
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .background(bg, RoundedCornerShape(4.dp))
              .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(item.size, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
            Text(item.bust, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
            Text(item.waist, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
            Text(item.hip, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
          }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Card(
          shape = RoundedCornerShape(8.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
          Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Outlined.Info,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Tips: Bila ukuran dada Anda berada di antara 2 ukuran, pilihlah ukuran yang lebih besar untuk kenyamanan pergerakan.",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  )
}

@Composable
fun AddressEditDialog(
  currentName: String,
  currentPhone: String,
  currentAddress: String,
  onDismiss: () -> Unit,
  onSave: (name: String, phone: String, address: String) -> Unit
) {
  var name by remember { mutableStateOf(currentName) }
  var phone by remember { mutableStateOf(currentPhone) }
  var address by remember { mutableStateOf(currentAddress) }

  AlertDialog(
    onDismissRequest = onDismiss,
    confirmButton = {
      Button(
        onClick = { onSave(name, phone, address) },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.testTag("save_address_button")
      ) {
        Text("Simpan Alamat")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    },
    title = {
      Text("Ubah Alamat Pengiriman", style = MaterialTheme.typography.titleLarge)
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Nama Penerima") },
          modifier = Modifier.fillMaxWidth().testTag("input_address_name")
        )
        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Nomor Telepon / WhatsApp") },
          modifier = Modifier.fillMaxWidth().testTag("input_address_phone")
        )
        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Alamat Lengkap & Patokan") },
          minLines = 3,
          modifier = Modifier.fillMaxWidth().testTag("input_address_text")
        )
      }
    }
  )
}
