package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.FashionData
import com.example.data.model.Product
import com.example.ui.FashionViewModel
import com.example.ui.components.ProductCard
import com.example.ui.theme.RosePrimary

@Composable
fun WishlistScreen(
  viewModel: FashionViewModel,
  onProductClick: (Product) -> Unit,
  onStartShopping: () -> Unit,
  modifier: Modifier = Modifier
) {
  val wishlistIds by viewModel.wishlistIds.collectAsState()
  val allProducts by viewModel.repository.products.collectAsState()
  val wishlistProducts = remember(wishlistIds, allProducts) {
    allProducts.filter { wishlistIds.contains(it.id) }
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp)
  ) {
    Text(
      text = "Koleksi Favorit Saya",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
    Text(
      text = "${wishlistProducts.size} busana tersimpan",
      style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
      modifier = Modifier.padding(bottom = 12.dp)
    )

    if (wishlistProducts.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
          Icon(
            imageVector = Icons.Outlined.FavoriteBorder,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(54.dp)
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Belum Ada Busana Favorit",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Sentuh ikon hati pada gaun atau busana yang Anda sukai untuk menyimpannya di sini.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
          )
          Spacer(modifier = Modifier.height(16.dp))
          Button(
            onClick = onStartShopping,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.testTag("wishlist_start_shopping_button")
          ) {
            Text("Jelajahi Gaun & Busana")
          }
        }
      }
    } else {
      LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.weight(1f),
        contentPadding = PaddingValues(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(wishlistProducts, key = { it.id }) { product ->
          ProductCard(
            product = product,
            isWishlisted = true,
            onProductClick = { onProductClick(product) },
            onWishlistToggle = { viewModel.toggleWishlist(product.id) },
            onAddToCart = { viewModel.addToCart(product, 1) }
          )
        }
      }
    }
  }
}
