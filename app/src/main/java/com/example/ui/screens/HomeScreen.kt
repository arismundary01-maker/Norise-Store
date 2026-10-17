package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FashionData
import com.example.data.model.Product
import com.example.ui.FashionViewModel
import com.example.ui.Screen
import com.example.ui.SortOption
import com.example.ui.components.ProductCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  viewModel: FashionViewModel,
  onProductClick: (Product) -> Unit,
  onExploreCatalog: () -> Unit,
  modifier: Modifier = Modifier
) {
  val products by viewModel.filteredProducts.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val searchQuery by viewModel.searchQuery.collectAsState()
  val selectedSort by viewModel.selectedSort.collectAsState()
  val wishlistIds by viewModel.wishlistIds.collectAsState()

  LazyVerticalGrid(
    columns = GridCells.Fixed(2),
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 12.dp),
    contentPadding = PaddingValues(bottom = 80.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalArrangement = Arrangement.spacedBy(10.dp)
  ) {
    // 1. Search Bar Header
    item(span = { GridItemSpan(2) }) {
      Column(modifier = Modifier.padding(top = 4.dp, bottom = 8.dp)) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = { viewModel.setSearchQuery(it) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_search_input"),
          placeholder = { Text("Cari gaun, tunik, blus, outer...") },
          leadingIcon = {
            Icon(Icons.Outlined.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { viewModel.setSearchQuery("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Hapus teks")
              }
            }
          },
          shape = RoundedCornerShape(24.dp),
          colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
          ),
          singleLine = true
        )
      }
    }

    // 2. Promo Banner
    if (searchQuery.isEmpty() && selectedCategory == "Semua") {
      item(span = { GridItemSpan(2) }) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onExploreCatalog() }
            .testTag("promo_hero_card"),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(
                    RosePrimary,
                    DarkBurgundy
                  )
                )
              )
              .padding(20.dp)
          ) {
            Column(modifier = Modifier.fillMaxWidth(0.72f)) {
              Surface(
                color = GoldAccent,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "ELEGAN WOMEN BY NORANISA",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                  ),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "Norise Store Exclusive",
                style = MaterialTheme.typography.headlineLarge.copy(
                  color = Color.White,
                  fontWeight = FontWeight.Bold
                )
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Koleksi busana wanita elegan by NORANISA dengan sentuhan mewah dan pengantaran kilat GrabExpress.",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = Color.White.copy(alpha = 0.9f)
                )
              )
              Spacer(modifier = Modifier.height(12.dp))
              Button(
                onClick = onExploreCatalog,
                colors = ButtonDefaults.buttonColors(
                  containerColor = Color.White,
                  contentColor = RosePrimary
                ),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
              ) {
                Text("Lihat Katalog", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }

            // Delivery Badge on Right
            Column(
              modifier = Modifier.align(Alignment.CenterEnd),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Box(
                modifier = Modifier
                  .size(54.dp)
                  .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.ElectricMoped,
                  contentDescription = "Pengiriman Instan",
                  tint = Color.White,
                  modifier = Modifier.size(32.dp)
                )
              }
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "GrabExpress",
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
              )
              Text(
                text = "1 Jam Tiba",
                style = MaterialTheme.typography.labelSmall.copy(color = GoldAccent, fontSize = 9.sp)
              )
            }
          }
        }
      }
    }

    // 3. Category Filter Chips
    item(span = { GridItemSpan(2) }) {
      Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Kategori Busana",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          if (selectedCategory != "Semua") {
            TextButton(
              onClick = { viewModel.setCategory("Semua") },
              contentPadding = PaddingValues(0.dp)
            ) {
              Text("Reset Kategori", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          contentPadding = PaddingValues(vertical = 2.dp)
        ) {
          items(FashionData.categories) { cat ->
            val isSelected = selectedCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = { viewModel.setCategory(cat) },
              label = {
                Text(
                  text = cat,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              leadingIcon = if (isSelected) {
                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
              } else null,
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = Color.White,
                selectedLeadingIconColor = Color.White
              ),
              modifier = Modifier.testTag("filter_chip_$cat")
            )
          }
        }
      }
    }

    // 4. Sort and Count Header
    item(span = { GridItemSpan(2) }) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "${products.size} Model Pilihan",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
          )
        )

        // Sort Pill Menu
        var showSortMenu by remember { mutableStateOf(false) }
        Box {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.clickable { showSortMenu = true }
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Outlined.Sort, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = selectedSort.title,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
              )
            }
          }

          DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
          ) {
            SortOption.values().forEach { option ->
              DropdownMenuItem(
                text = { Text(option.title) },
                onClick = {
                  viewModel.setSortOption(option)
                  showSortMenu = false
                },
                leadingIcon = {
                  if (selectedSort == option) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }
                }
              )
            }
          }
        }
      }
    }

    // 5. Empty State or Products
    if (products.isEmpty()) {
      item(span = { GridItemSpan(2) }) {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Outlined.SentimentDissatisfied,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
              text = "Busana Tidak Ditemukan",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tidak ada produk yang cocok dengan pencarian \"$searchQuery\". Coba cari dengan kata kunci lain.",
              style = MaterialTheme.typography.bodySmall,
              textAlign = TextAlign.Center,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = {
                viewModel.setSearchQuery("")
                viewModel.setCategory("Semua")
              },
              colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
              Text("Tampilkan Semua Busana")
            }
          }
        }
      }
    } else {
      items(products, key = { it.id }) { product ->
        ProductCard(
          product = product,
          isWishlisted = wishlistIds.contains(product.id),
          onProductClick = { onProductClick(product) },
          onWishlistToggle = { viewModel.toggleWishlist(product.id) },
          onAddToCart = { viewModel.addToCart(product, 1) }
        )
      }
    }
  }
}
