package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.FashionData
import com.example.data.model.ColorOption
import com.example.data.model.Product
import com.example.ui.FashionViewModel
import com.example.ui.components.formatRupiah
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  var pinInput by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text("Login Admin Norise Store", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(24.dp),
      contentAlignment = Alignment.Center
    ) {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
      ) {
        Column(
          modifier = Modifier.padding(24.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(64.dp)
              .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AdminPanelSettings,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Text(
            text = "Portal Admin Norise Store",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Elegan Women by NORANISA",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
          )

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = "Masukkan PIN atau kode akses admin untuk mengunggah dan mengelola koleksi busana yang tampil di beranda aplikasi.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(20.dp))

          OutlinedTextField(
            value = pinInput,
            onValueChange = {
              pinInput = it
              errorMessage = null
            },
            label = { Text("Kode Akses / PIN Admin") },
            placeholder = { Text("Contoh: norise2026") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("admin_pin_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
          )

          if (errorMessage != null) {
            Text(
              text = errorMessage ?: "",
              color = CoralRed,
              style = MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(top = 6.dp)
            )
          }

          Spacer(modifier = Modifier.height(16.dp))

          Button(
            onClick = {
              val ok = viewModel.loginAdmin(pinInput)
              if (ok) {
                onLoginSuccess()
              } else {
                errorMessage = "PIN salah. Gunakan PIN admin: norise2026"
              }
            },
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("admin_login_submit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Text("Masuk sebagai Admin", fontWeight = FontWeight.Bold)
          }

          Spacer(modifier = Modifier.height(14.dp))

          Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Outlined.Info, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("PIN Default: norise2026", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
  viewModel: FashionViewModel,
  onBack: () -> Unit,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val uploadedProducts by viewModel.repository.products.collectAsState()

  var selectedTab by remember { mutableStateOf(0) }
  val tabs = listOf("Upload Baju Baru", "Busana Tayang (${uploadedProducts.size})", "Preset Cepat")

  // Form states
  var name by remember { mutableStateOf("") }
  var brand by remember { mutableStateOf("Elegan Women by NORANISA") }
  var selectedCategory by remember { mutableStateOf("Dress & Gaun") }
  var priceText by remember { mutableStateOf("") }
  var originalPriceText by remember { mutableStateOf("") }
  var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&q=80") }
  var material by remember { mutableStateOf("Sutera Silk Satin & Rayon Twill Premium") }
  var description by remember { mutableStateOf("Gaun elegan berpotongan anggun eksklusif rancangan NORANISA.") }
  var isBestSeller by remember { mutableStateOf(true) }
  var isNewArrival by remember { mutableStateOf(true) }
  var isSubmitting by remember { mutableStateOf(false) }

  val photoPresets = listOf(
    "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&q=80" to "Dress Satin",
    "https://images.unsplash.com/photo-1548624149-f9b1859aa7d0?w=800&q=80" to "Blazer Tweed",
    "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=800&q=80" to "Tunik Modest",
    "https://images.unsplash.com/photo-1564257631407-4deb1f99d992?w=800&q=80" to "Blus Organza",
    "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=800&q=80" to "Rok Plisket",
    "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800&q=80" to "Setelan Linen"
  )

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Column {
            Text("Admin Norise Store", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Text("Elegan Women by NORANISA", style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary))
          }
        },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Kembali ke Beranda")
          }
        },
        actions = {
          TextButton(onClick = {
            viewModel.logoutAdmin()
            onLogout()
          }) {
            Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp), tint = CoralRed)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Keluar", color = CoralRed, fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
      )
    }
  ) { innerPadding ->
    Column(
      modifier = modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      TabRow(
        selectedTabIndex = selectedTab,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
      ) {
        tabs.forEachIndexed { index, title ->
          Tab(
            selected = selectedTab == index,
            onClick = { selectedTab = index },
            text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
          )
        }
      }

      when (selectedTab) {
        0 -> {
          // Tab: Upload Baju Baru
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            item {
              Text(
                text = "Form Upload Busana Baru ke Beranda",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Baju yang diunggah di sini akan langsung tersimpan di Firebase Firestore dan tayang secara real-time di beranda.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            item {
              OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama Busana / Baju") },
                placeholder = { Text("Contoh: Gaun Pesta Silk Organza") },
                modifier = Modifier.fillMaxWidth().testTag("input_admin_product_name")
              )
            }

            item {
              OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Label Brand / Perancang") },
                modifier = Modifier.fillMaxWidth().testTag("input_admin_product_brand")
              )
            }

            item {
              Text("Kategori Busana:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
              Spacer(modifier = Modifier.height(4.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(FashionData.categories.filter { it != "Semua" }) { cat ->
                  FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat) },
                    colors = FilterChipDefaults.filterChipColors(
                      selectedContainerColor = MaterialTheme.colorScheme.primary,
                      selectedLabelColor = Color.White
                    )
                  )
                }
              }
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
              ) {
                OutlinedTextField(
                  value = priceText,
                  onValueChange = { priceText = it },
                  label = { Text("Harga Jual (Rp)") },
                  placeholder = { Text("Contoh: 350000") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f).testTag("input_admin_product_price")
                )
                OutlinedTextField(
                  value = originalPriceText,
                  onValueChange = { originalPriceText = it },
                  label = { Text("Harga Coret (Rp)") },
                  placeholder = { Text("Contoh: 450000") },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.weight(1f)
                )
              }
            }

            item {
              Text("Pilih Preset Foto Busana:", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
              Spacer(modifier = Modifier.height(6.dp))
              LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(photoPresets) { (url, label) ->
                  val isSelected = imageUrl == url
                  Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { imageUrl = url }
                  ) {
                    AsyncImage(
                      model = url,
                      contentDescription = label,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                          width = if (isSelected) 3.dp else 1.dp,
                          color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                          shape = RoundedCornerShape(8.dp)
                        )
                    )
                    Text(label, style = MaterialTheme.typography.labelSmall, fontSize = 10.sp)
                  }
                }
              }
            }

            item {
              OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("URL Gambar Busana") },
                modifier = Modifier.fillMaxWidth()
              )
            }

            item {
              OutlinedTextField(
                value = material,
                onValueChange = { material = it },
                label = { Text("Spesifikasi Bahan & Kain") },
                modifier = Modifier.fillMaxWidth()
              )
            }

            item {
              OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Deskripsi Singkat") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
              )
            }

            item {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isBestSeller, onCheckedChange = { isBestSeller = it })
                  Text("Best Seller", style = MaterialTheme.typography.bodySmall)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it })
                  Text("New Arrival", style = MaterialTheme.typography.bodySmall)
                }
              }
            }

            item {
              Spacer(modifier = Modifier.height(8.dp))
              Button(
                onClick = {
                  val price = priceText.toLongOrNull() ?: 0L
                  val origPrice = originalPriceText.toLongOrNull() ?: price
                  if (name.isBlank() || price <= 0L) {
                    Toast.makeText(context, "Mohon isi nama baju dan harga dengan benar", Toast.LENGTH_SHORT).show()
                    return@Button
                  }

                  val discount = if (origPrice > price) {
                    (((origPrice - price) * 100) / origPrice).toInt()
                  } else 0

                  val newProduct = Product(
                    id = "nr_${System.currentTimeMillis()}",
                    name = name.trim(),
                    brand = brand.trim(),
                    category = selectedCategory,
                    price = price,
                    originalPrice = origPrice,
                    discountPercent = discount,
                    rating = 5.0f,
                    reviewCount = 1,
                    soldCount = 0,
                    description = description.trim(),
                    material = material.trim(),
                    careGuide = "Cuci dengan air dingin, setrika suhu sedang.",
                    fitGuide = "Regular elegant fit standard Indonesia.",
                    imageUrl = imageUrl.trim(),
                    colors = listOf(
                      ColorOption("Rose Blush", 0xFFE8B4B8),
                      ColorOption("Champagne", 0xFFD8C7A5),
                      ColorOption("Noir Black", 0xFF1C1B1F)
                    ),
                    sizes = listOf("S", "M", "L", "XL"),
                    tags = listOfNotNull(
                      if (isBestSeller) "Best Seller" else null,
                      if (isNewArrival) "New Season" else null,
                      "Elegan Women"
                    ),
                    isBestSeller = isBestSeller,
                    isNewArrival = isNewArrival,
                    uploadedByAdmin = true
                  )

                  isSubmitting = true
                  viewModel.uploadProduct(
                    product = newProduct,
                    onSuccess = {
                      isSubmitting = false
                      Toast.makeText(context, "✓ Berhasil diunggah ke Firestore & Beranda!", Toast.LENGTH_LONG).show()
                      name = ""
                      priceText = ""
                      originalPriceText = ""
                      selectedTab = 1
                    },
                    onError = { err ->
                      isSubmitting = false
                      Toast.makeText(context, "Error: $err", Toast.LENGTH_LONG).show()
                    }
                  )
                },
                enabled = !isSubmitting,
                modifier = Modifier
                  .fillMaxWidth()
                  .height(50.dp)
                  .testTag("admin_upload_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
              ) {
                if (isSubmitting) {
                  CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                  Icon(Icons.Default.CloudUpload, contentDescription = null)
                  Spacer(modifier = Modifier.width(8.dp))
                  Text("Unggah Baju ke Beranda Norise Store", fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }

        1 -> {
          // Tab: Busana Tayang di Beranda
          if (uploadedProducts.isEmpty()) {
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Outlined.Checkroom, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text("Belum Ada Baju yang Diunggah", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "Beranda saat ini bersih. Unggah baju baru pada tab 'Upload Baju Baru' atau pilih dari tab 'Preset Cepat'.",
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.Gray
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { selectedTab = 0 }) {
                  Text("Buka Form Upload")
                }
              }
            }
          } else {
            LazyColumn(
              modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
              contentPadding = PaddingValues(vertical = 14.dp),
              verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              item {
                Text(
                  text = "${uploadedProducts.size} Busana Aktif di Beranda Norise Store",
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
              }

              items(uploadedProducts, key = { it.id }) { product ->
                Card(
                  shape = RoundedCornerShape(14.dp),
                  colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                  elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                  ) {
                    AsyncImage(
                      model = product.imageUrl,
                      contentDescription = product.name,
                      contentScale = ContentScale.Crop,
                      modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                    )

                    Column(modifier = Modifier.weight(1f)) {
                      Text(product.brand, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                      Text(product.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), maxLines = 1)
                      Text(formatRupiah(product.price), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                      Text("Kategori: ${product.category}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }

                    IconButton(
                      onClick = {
                        viewModel.deleteProduct(
                          productId = product.id,
                          onSuccess = {
                            Toast.makeText(context, "Baju dihapus dari beranda", Toast.LENGTH_SHORT).show()
                          }
                        )
                      }
                    ) {
                      Icon(Icons.Outlined.Delete, contentDescription = "Hapus", tint = CoralRed)
                    }
                  }
                }
              }
            }
          }
        }

        2 -> {
          // Tab: Preset Cepat by NORANISA
          LazyColumn(
            modifier = Modifier
              .fillMaxSize()
              .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            item {
              Text(
                text = "Koleksi Preset Elegan Women by NORANISA",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Klik 'Upload ke Beranda' untuk mengunggah busana kurasi ini secara instan ke database Firestore Anda.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            items(FashionData.adminPresetProducts) { preset ->
              val isAlreadyUploaded = uploadedProducts.any { it.name == preset.name }
              Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                  AsyncImage(
                    model = preset.imageUrl,
                    contentDescription = preset.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                      .size(64.dp)
                      .clip(RoundedCornerShape(8.dp))
                  )

                  Column(modifier = Modifier.weight(1f)) {
                    Text(preset.brand, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(preset.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), maxLines = 1)
                    Text(formatRupiah(preset.price), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    Text("Kategori: ${preset.category}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                  }

                  Button(
                    onClick = {
                      viewModel.uploadPresetProduct(
                        preset = preset,
                        onSuccess = {
                          Toast.makeText(context, "✓ '${preset.name}' berhasil tampil di Beranda!", Toast.LENGTH_SHORT).show()
                        }
                      )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                      containerColor = if (isAlreadyUploaded) EmeraldGreen else MaterialTheme.colorScheme.primary
                    )
                  ) {
                    if (isAlreadyUploaded) {
                      Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Aktif", style = MaterialTheme.typography.labelSmall)
                    } else {
                      Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                      Spacer(modifier = Modifier.width(4.dp))
                      Text("Upload", style = MaterialTheme.typography.labelSmall)
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
