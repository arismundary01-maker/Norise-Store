package com.example.data

import com.example.data.model.*

object FashionData {

  val categories = listOf(
    "Semua",
    "Dress & Gaun",
    "Blus & Kemeja",
    "Tunik & Gamis",
    "Outer & Blazer",
    "Rok & Bawahan",
    "Setelan & Formal"
  )

  val sizeGuideList = listOf(
    SizeGuideItem("XS", "78-82 cm", "60-64 cm", "86-90 cm", "110-125 cm"),
    SizeGuideItem("S", "83-87 cm", "65-69 cm", "91-95 cm", "112-127 cm"),
    SizeGuideItem("M", "88-92 cm", "70-74 cm", "96-100 cm", "114-129 cm"),
    SizeGuideItem("L", "93-98 cm", "75-80 cm", "101-106 cm", "116-131 cm"),
    SizeGuideItem("XL", "99-105 cm", "81-87 cm", "107-113 cm", "118-133 cm"),
    SizeGuideItem("XXL", "106-112 cm", "88-94 cm", "114-120 cm", "120-135 cm")
  )

  val sampleReviews = listOf(
    ReviewItem(
      reviewerName = "Clara Shinta",
      rating = 5,
      date = "2 hari lalu",
      size = "M",
      color = "Rose Blush",
      comment = "Bahan super adem dan jatuh banget saat dipakai! Jahitan butik sangat rapi, tidak menerawang sama sekali. Worth it banget harganya!"
    ),
    ReviewItem(
      reviewerName = "Dewi Anggraini",
      rating = 5,
      date = "5 hari lalu",
      size = "S",
      color = "Broken White",
      comment = "Cuttingnya bikin terlihat langsing dan anggun. Dipakai untuk acara wisuda dan pesta sangat pas. Pengiriman kilat via GrabExpress cuma 40 menit sampai!"
    ),
    ReviewItem(
      reviewerName = "Nadya Putri",
      rating = 4,
      date = "1 minggu lalu",
      size = "L",
      color = "Sage Green",
      comment = "Warnanya elegan soft pastel persis seperti foto. Detail kancing dan kerahnya mewah. Bakal langganan terus di Norise Store."
    )
  )

  val couriers = listOf(
    ShippingCourier(
      id = "grab_instant",
      name = "GrabExpress Instant",
      serviceType = "Pengantaran Kilat 1 - 2 Jam (Pelacakan Real-time)",
      fee = 25000L,
      estimatedTime = "Tiba dalam 1-2 Jam",
      isInstant = true
    ),
    ShippingCourier(
      id = "grab_sameday",
      name = "GrabExpress Sameday",
      serviceType = "Tiba di hari yang sama (Hemat & Cepat)",
      fee = 18000L,
      estimatedTime = "Tiba hari ini sebelum 18:00",
      isInstant = true
    ),
    ShippingCourier(
      id = "sicepat_best",
      name = "SiCepat BEST",
      serviceType = "Besok Sampai Tujuan (Garansi Kilat)",
      fee = 15000L,
      estimatedTime = "Besok Sore",
      isInstant = false
    ),
    ShippingCourier(
      id = "jnt_reguler",
      name = "J&T Express Reguler",
      serviceType = "Pengiriman Standar Terpercaya ke Seluruh Indonesia",
      fee = 12000L,
      estimatedTime = "2 - 3 Hari Kerja",
      isInstant = false
    )
  )

  val paymentMethods = listOf(
    PaymentMethodItem(
      id = "qris",
      name = "QRIS Instant (GoPay, OVO, ShopeePay, Dana)",
      category = "E-Wallet & QR",
      details = "Pindai kode QR instan tanpa biaya admin, konfirmasi otomatis detik itu juga",
      isPopular = true
    ),
    PaymentMethodItem(
      id = "bca_va",
      name = "BCA Virtual Account",
      category = "Virtual Account Bank",
      details = "Bayar melalui m-BCA, KlikBCA atau ATM, bebas verifikasi manual",
      isPopular = true
    ),
    PaymentMethodItem(
      id = "mandiri_va",
      name = "Mandiri Virtual Account (Livin')",
      category = "Virtual Account Bank",
      details = "Transfer instan via aplikasi Livin' by Mandiri"
    ),
    PaymentMethodItem(
      id = "bri_va",
      name = "BRI Virtual Account (BRIMO)",
      category = "Virtual Account Bank",
      details = "Konfirmasi langsung via aplikasi BRImo"
    ),
    PaymentMethodItem(
      id = "credit_card",
      name = "Kartu Kredit / Debit Online (Visa/Mastercard)",
      category = "Kartu Pembayaran",
      details = "Keamanan 3D Secure dengan cicilan 0% hingga 12 bulan"
    ),
    PaymentMethodItem(
      id = "cod",
      name = "COD (Bayar Tunai di Tempat)",
      category = "Bayar Tunai",
      details = "Bayar langsung ke kurir saat paket gaun tiba di rumahmu"
    )
  )

  val availableVouchers = listOf(
    Voucher(
      code = "NORISE20",
      discountPercent = 20,
      maxDiscount = 50000L,
      minSpend = 150000L,
      description = "Diskon 20% s/d Rp 50.000 untuk koleksi Elegan Women by NORANISA"
    ),
    Voucher(
      code = "GRATISONGKIR",
      discountPercent = 100,
      maxDiscount = 25000L,
      minSpend = 100000L,
      description = "Gratis ongkos kirim s/d Rp 25.000 (Semua Kurir termasuk GrabExpress)"
    ),
    Voucher(
      code = "NORANISA10",
      discountPercent = 10,
      maxDiscount = 30000L,
      minSpend = 100000L,
      description = "Potongan 10% s/d Rp 30.000 untuk busana by NORANISA"
    )
  )

  // Homepage starts completely clean per user request
  val sampleProducts = emptyList<Product>()

  // Presets available for Admin to upload with 1-click
  val adminPresetProducts = listOf(
    Product(
      id = "preset_01",
      name = "Aurelia Silk Pleated Maxi Dress",
      brand = "Elegan Women by NORANISA",
      category = "Dress & Gaun",
      price = 389000L,
      originalPrice = 520000L,
      discountPercent = 25,
      rating = 4.9f,
      reviewCount = 142,
      soldCount = 428,
      description = "Gaun maxi berpotongan A-line dengan aksen lipit plisket halus dan pita serut pinggang yang menonjolkan siluet tubuh feminin nan elegan. Dibuat dari perpaduan sutra premium dan serat rayon sejuk yang jatuh sempurna.",
      material = "Sutera Silk Satin & Pleated Rayon Premium",
      careGuide = "Cuci dengan air dingin, hindari pemutih, setrika suhu sedang dengan alas kain.",
      fitGuide = "Model memakai ukuran S (Tinggi 172cm, LD 84cm). Regular elegant fit.",
      imageUrl = "https://images.unsplash.com/photo-1595777457583-95e059d581b8?w=800&q=80",
      colors = listOf(
        ColorOption("Rose Blush", 0xFFE8B4B8),
        ColorOption("Emerald Olive", 0xFF4A6B5B),
        ColorOption("Champagne Gold", 0xFFD8C7A5),
        ColorOption("Midnight Black", 0xFF1C1B1F)
      ),
      sizes = listOf("XS", "S", "M", "L", "XL"),
      tags = listOf("Best Seller", "New Season", "Pesta & Kasual"),
      isBestSeller = true,
      isNewArrival = true
    ),
    Product(
      id = "prod_02",
      name = "Seraphina French Tweed Blazer Outer",
      brand = "NORANISA Luxe Outer",
      category = "Outer & Blazer",
      price = 459000L,
      originalPrice = 650000L,
      discountPercent = 29,
      rating = 4.9f,
      reviewCount = 98,
      soldCount = 310,
      description = "Outer blazer gaya Parisian chic dengan tekstur rajut tweed Prancis berpadu kancing mutiara vintage. Sangat anggun dipadukan dengan celana kulot, rok plisket, maupun gaun pesta.",
      material = "French Wool Tweed Texture dengan Lining Katun Satin Dingin",
      careGuide = "Dry clean disarankan, simpan dengan gantungan busa agar bentuk bahu tetap tegap.",
      fitGuide = "Struktur bahu semi-padded yang mempertegas postur tubuh tetap ramping.",
      imageUrl = "https://images.unsplash.com/photo-1548624149-f9b1859aa7d0?w=800&q=80",
      colors = listOf(
        ColorOption("Vanilla Cream", 0xFFF3ECE0),
        ColorOption("Dusty Mauve", 0xFFAB8793),
        ColorOption("Classic Noir", 0xFF232227)
      ),
      sizes = listOf("S", "M", "L", "XL"),
      tags = listOf("Best Seller", "Office & Party", "Chic"),
      isBestSeller = true
    ),
    Product(
      id = "prod_03",
      name = "Zahara Embroidery Chiffon Tunik Gamis",
      brand = "Elegan Women by NORANISA",
      category = "Tunik & Gamis",
      price = 329000L,
      originalPrice = 420000L,
      discountPercent = 21,
      rating = 4.8f,
      reviewCount = 215,
      soldCount = 680,
      description = "Tunik gamis modern dengan sulaman bordir floral benang emas di dada dan ujung lengan lonceng. Menggunakan bahan ceruty babydoll berlapis furing katun adem, wudhu-friendly dan busui-friendly dengan resleting depan.",
      material = "Ceruty Babydoll Premium Full Furing Katun Rayon",
      careGuide = "Cuci tangan lembut, keringkan di tempat teduh.",
      fitGuide = "Cutting loose flowy ramah berbagai ukuran tubuh, panjang menutup sempurna.",
      imageUrl = "https://images.unsplash.com/photo-1585487000160-6ebcfceb0d03?w=800&q=80",
      colors = listOf(
        ColorOption("Sage Green", 0xFF9CAF88),
        ColorOption("Terracotta Rose", 0xFFC97A63),
        ColorOption("Dusty Lilac", 0xFFB39EB5),
        ColorOption("Pure Pearl", 0xFFFAF9F6)
      ),
      sizes = listOf("S", "M", "L", "XL", "XXL"),
      tags = listOf("Modest Wear", "Busui Friendly", "Best Seller"),
      isBestSeller = true
    ),
    Product(
      id = "prod_04",
      name = "Eleanor Organza Puffy Sleeve Blouse",
      brand = "Elegan Women by NORANISA",
      category = "Blus & Kemeja",
      price = 249000L,
      originalPrice = 330000L,
      discountPercent = 24,
      rating = 4.7f,
      reviewCount = 84,
      soldCount = 265,
      description = "Blus wanita kontemporer berkerut di leher dengan lengan puff organza transparan berlapis lembut. Menampilkan siluet elegan dan glamor untuk bekerja maupun kencan makan malam.",
      material = "Silky Crepe dipadukan Lengan Organza Halus",
      careGuide = "Setrika suhu rendah bagian organza, jangan peras terlalu kencang.",
      fitGuide = "Pas di badan bagian torso dengan aksen bervolume manis di lengan.",
      imageUrl = "https://images.unsplash.com/photo-1564257631407-4deb1f99d992?w=800&q=80",
      colors = listOf(
        ColorOption("Soft Peach", 0xFFFFDAB9),
        ColorOption("Sky Blue", 0xFFB0C4DE),
        ColorOption("Broken White", 0xFFFDFBF7)
      ),
      sizes = listOf("XS", "S", "M", "L"),
      tags = listOf("New Season", "Trending"),
      isNewArrival = true
    ),
    Product(
      id = "prod_05",
      name = "Camille Pleated Satin Mermaid Skirt",
      brand = "NORANISA Luxe",
      category = "Rok & Bawahan",
      price = 225000L,
      originalPrice = 299000L,
      discountPercent = 25,
      rating = 4.8f,
      reviewCount = 112,
      soldCount = 390,
      description = "Rok panjang model duyung (mermaid) dengan detail plisket satin bergelombang indah saat melangkah. Dilengkapi ban pinggang elastis tersembunyi yang nyaman tanpa rasa sesak.",
      material = "Heavyweight Satin Silk Anti-Kusut",
      careGuide = "Gantung langsung setelah dicuci tanpa perlu disetrika agar plisket awet.",
      fitGuide = "High-waist fit yang memberi ilusi kaki lebih jenjang.",
      imageUrl = "https://images.unsplash.com/photo-1583496661160-fb5886a0aaaa?w=800&q=80",
      colors = listOf(
        ColorOption("Champagne", 0xFFF7E7CE),
        ColorOption("Rosewood", 0xFF9E4760),
        ColorOption("Olive Glaze", 0xFF556B2F),
        ColorOption("Jet Black", 0xFF1A1A1A)
      ),
      sizes = listOf("S", "M", "L", "XL"),
      tags = listOf("Diskon 25%", "High Waist")
    ),
    Product(
      id = "prod_06",
      name = "Valencia Korean Linen Co-Ord Set",
      brand = "NORANISA Casual",
      category = "Setelan & Formal",
      price = 399000L,
      originalPrice = 520000L,
      discountPercent = 23,
      rating = 4.9f,
      reviewCount = 176,
      soldCount = 512,
      description = "Setelan two-piece wanita ala Korea yang terdiri dari cropped vest berkancing tortoise dan celana kulot wide-leg berpotongan rapi. Terbuat dari serat linen Prancis ramah lingkungan yang sangat sejuk.",
      material = "100% French Washed Linen Premium",
      careGuide = "Cuci dengan deterjen lembut, jemur di tempat berangin.",
      fitGuide = "Relaxed tailored fit yang tetap memberi lekuk tubuh proporsional.",
      imageUrl = "https://images.unsplash.com/photo-1515886657613-9f3515b0c78f?w=800&q=80",
      colors = listOf(
        ColorOption("Matcha Green", 0xFF8F9779),
        ColorOption("Buttercream", 0xFFFFF1C5),
        ColorOption("Oatmeal Beige", 0xFFD8CCB5)
      ),
      sizes = listOf("S", "M", "L", "XL"),
      tags = listOf("Best Seller", "Korean Look", "Two-Piece Set"),
      isBestSeller = true,
      isNewArrival = true
    ),
    Product(
      id = "prod_07",
      name = "Isabella Floral Tiered Bohemian Dress",
      brand = "Elegan Women by NORANISA",
      category = "Dress & Gaun",
      price = 345000L,
      originalPrice = 445000L,
      discountPercent = 22,
      rating = 4.8f,
      reviewCount = 93,
      soldCount = 280,
      description = "Dress bohemian bertingkat dengan motif bunga mawar vintage nan manis. Dilengkapi kerah sweetheart dan karet di bagian punggung untuk kenyamanan maksimal saat bergerak.",
      material = "Rayon Voile Silk Super Adem",
      careGuide = "Cuci terpisah saat pencucian pertama.",
      fitGuide = "Flowy midi dress dengan panjang di bawah lutut.",
      imageUrl = "https://images.unsplash.com/photo-1572804013309-59a88b7e92f1?w=800&q=80",
      colors = listOf(
        ColorOption("Rose Floral", 0xFFDDA7A5),
        ColorOption("Sky Blossom", 0xFFA0C4DF)
      ),
      sizes = listOf("XS", "S", "M", "L"),
      tags = listOf("Summer Chic", "Vacation Ready")
    ),
    Product(
      id = "prod_08",
      name = "Rosalie Knit Cardigan with Pearl Buttons",
      brand = "NORANISA Casual",
      category = "Outer & Blazer",
      price = 265000L,
      originalPrice = 340000L,
      discountPercent = 22,
      rating = 4.9f,
      reviewCount = 138,
      soldCount = 440,
      description = "Cardigan rajut halus lembut dengan tekstur cable knit elegan dan barisan kancing mutiara berhias kristal. Nyaman untuk ruangan ber-AC di kantor maupun gaya santai akhir pekan.",
      material = "Premium Cotton Yarn Knit (Tidak Gatal & Tidak Panas)",
      careGuide = "Jangan digantung saat basah agar rajutan tidak melar.",
      fitGuide = "Semi crop fit yang manis dipadukan dengan celana high-waist.",
      imageUrl = "https://images.unsplash.com/photo-1434389677669-e08b4cac3105?w=800&q=80",
      colors = listOf(
        ColorOption("Pastel Pink", 0xFFF4C2C2),
        ColorOption("Ivory White", 0xFFFFFFF0),
        ColorOption("Mocha Brown", 0xFF967969)
      ),
      sizes = listOf("S", "M", "L"),
      tags = listOf("Cozy Knit", "Best Seller"),
      isBestSeller = true
    )
  )
}
