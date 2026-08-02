package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RetailerEntity
import com.example.data.repository.CartItem
import com.example.ui.model.UserRole

@Composable
fun SrOrderBookingScreen(
    userRole: UserRole,
    products: List<ProductEntity>,
    retailers: List<RetailerEntity>,
    selectedRetailer: RetailerEntity?,
    cart: List<CartItem>,
    searchQuery: String,
    selectedCategory: String,
    onSelectRetailer: (RetailerEntity) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onCategoryChange: (String) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onUpdateQuantity: (productId: Long, newQty: Int) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isShopDropdownExpanded by remember { mutableStateOf(false) }
    var isGridView by remember { mutableStateOf(true) }
    var selectedProductForDetail by remember { mutableStateOf<ProductEntity?>(null) }

    val categories = remember(products) {
        listOf("সব") + products.map { it.category }.distinct()
    }

    val filteredProducts = products.filter { product ->
        val matchesCategory = (selectedCategory == "সব" || product.category == selectedCategory)
        val matchesQuery = searchQuery.isEmpty() ||
                product.nameBangla.contains(searchQuery, ignoreCase = true) ||
                product.name.contains(searchQuery, ignoreCase = true) ||
                product.code.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesQuery
    }

    val currentRetailer = selectedRetailer ?: retailers.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        // Retailer / Shop Selector Header
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "দোকান / রিটেইলার নির্বাচন করুন:",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A).copy(alpha = 0.04f))
                        .clickable { isShopDropdownExpanded = true }
                        .padding(12.dp)
                        .testTag("shop_dropdown_trigger")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0284C7).copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = "Shop",
                                    tint = Color(0xFF0284C7),
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = currentRetailer?.shopName ?: "দোকান নির্বাচন করুন",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${currentRetailer?.routeBeat ?: ""} • প্রোপার্টি: ${currentRetailer?.ownerName ?: ""} • বকেয়া: ৳${currentRetailer?.dueBalance ?: 0.0}",
                                    fontSize = 11.sp,
                                    color = if ((currentRetailer?.dueBalance ?: 0.0) > 0) Color(0xFFDC2626) else Color(0xFF059669),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Text("পরিবর্তন ▼", fontSize = 11.sp, color = Color(0xFF0284C7), fontWeight = FontWeight.Bold)
                    }

                    DropdownMenu(
                        expanded = isShopDropdownExpanded,
                        onDismissRequest = { isShopDropdownExpanded = false }
                    ) {
                        retailers.forEach { shop ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(shop.shopName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${shop.ownerName} (${shop.routeBeat}) • বকেয়া: ৳${shop.dueBalance}", fontSize = 10.sp, color = Color.Gray)
                                    }
                                },
                                onClick = {
                                    onSelectRetailer(shop)
                                    isShopDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Search & Layout Control Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("পণ্যের নাম বা কোড দিয়ে খুঁজুন...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_search_product")
                )

                // Layout View Switcher (Grid vs List)
                IconButton(
                    onClick = { isGridView = !isGridView },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                        .testTag("btn_toggle_view")
                ) {
                    Icon(
                        imageVector = if (isGridView) Icons.Default.List else Icons.Default.GridView,
                        contentDescription = "Toggle View",
                        tint = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF0F172A) else Color.White)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFF0F172A) else Color(0xFFCBD5E1),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onCategoryChange(category) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("category_chip_$category")
                    ) {
                        Text(
                            text = category,
                            fontSize = 12.sp,
                            color = if (isSelected) Color.White else Color(0xFF334155),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Product Catalog Display (Grid vs Dense List)
        if (isGridView) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts) { product ->
                    val cartQty = cart.find { it.product.id == product.id }?.quantity ?: 0

                    ProductGridCard(
                        product = product,
                        cartQuantity = cartQty,
                        onAddToCart = { onAddToCart(product) },
                        onIncrement = { onUpdateQuantity(product.id, cartQty + 1) },
                        onDecrement = { onUpdateQuantity(product.id, cartQty - 1) },
                        onImageClick = { selectedProductForDetail = product }
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(filteredProducts) { product ->
                    val cartQty = cart.find { it.product.id == product.id }?.quantity ?: 0

                    ProductListCard(
                        product = product,
                        cartQuantity = cartQty,
                        onAddToCart = { onAddToCart(product) },
                        onIncrement = { onUpdateQuantity(product.id, cartQty + 1) },
                        onDecrement = { onUpdateQuantity(product.id, cartQty - 1) },
                        onImageClick = { selectedProductForDetail = product }
                    )
                }
            }
        }

        // Cart Summary & Checkout Bar
        val totalCartItems = cart.sumOf { it.quantity }
        val totalCartAmount = cart.sumOf { it.product.tradeOfferPrice * it.quantity }

        if (totalCartItems > 0) {
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 10.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "$totalCartItems টি আইটেম নির্বাচিত",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Text(
                            text = "মোট: ৳$totalCartAmount BDT",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF38BDF8)
                        )
                    }

                    Button(
                        onClick = onCheckoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_checkout_order")
                    ) {
                        Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Checkout")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("পেমেন্ট ও অর্ডার নিশ্চিত >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Product Image & Details Zoom Dialog
    if (selectedProductForDetail != null) {
        val item = selectedProductForDetail!!
        var customQtyInput by remember { mutableStateOf("1") }
        val currentCartQty = cart.find { it.product.id == item.id }?.quantity ?: 0

        Dialog(onDismissRequest = { selectedProductForDetail = null }) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Large Image Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.imageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.nameBangla,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "No Image",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        // Code Tag
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F172A))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "কোড: ${item.code}",
                                fontSize = 10.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.nameBangla,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Text(
                        text = item.name,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "ট্রেড অফার প্রাইস:",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "৳${item.tradeOfferPrice}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0284C7)
                                )
                                if (item.tradeOfferPrice < item.price) {
                                    Text(
                                        text = "MRP: ৳${item.price}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "একক / পরিমাপ:",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = item.unit,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Stock Info
                    val isLow = item.stockQuantity <= item.minStockThreshold
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isLow) Color(0xFFFEF2F2) else Color(0xFFF0FDF4))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "গুদাম মজুদ (Stock): ${item.stockQuantity} ${item.unit}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLow) Color(0xFFDC2626) else Color(0xFF16A34A)
                            )
                            Text(
                                text = if (item.stockQuantity > 0) "পর্যাপ্ত স্টক আছে" else "স্টক খালি",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLow) Color(0xFFDC2626) else Color(0xFF16A34A)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Quantity Buttons
                    Text(
                        text = "দ্রুত পরিমাণ নির্ধারণ করুন (${item.unit}):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF334155)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(1, 5, 10, 25, 50).forEach { qty ->
                            Button(
                                onClick = {
                                    customQtyInput = qty.toString()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (customQtyInput == qty.toString()) Color(0xFF0F172A) else Color(0xFFE2E8F0),
                                    contentColor = if (customQtyInput == qty.toString()) Color.White else Color(0xFF334155)
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("+$qty", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customQtyInput,
                        onValueChange = { customQtyInput = it },
                        label = { Text("নির্ধারিত পরিমাণ (${item.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { selectedProductForDetail = null }) {
                            Text("বন্ধ করুন")
                        }

                        Button(
                            onClick = {
                                val enteredQty = customQtyInput.toIntOrNull() ?: 1
                                if (enteredQty > 0) {
                                    onUpdateQuantity(item.id, currentCartQty + enteredQty)
                                    selectedProductForDetail = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.AddShoppingCart, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("কার্টে যোগ করুন")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onImageClick: () -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockThreshold

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_grid_${product.id}")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // E-Commerce Style Image Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color(0xFFF8FAFC))
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.nameBangla,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "No Image",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Offer Tag Top Left
                if (product.tradeOfferPrice < product.price) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFDC2626))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("অফার", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                    }
                }

                // Zoom Icon Top Right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.4f))
                        .padding(4.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Zoom", tint = Color.White, modifier = Modifier.size(12.dp))
                }

                // Stock Badge Bottom Right
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isLowStock) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "স্টক: ${product.stockQuantity}",
                        fontSize = 9.sp,
                        color = if (isLowStock) Color(0xFFDC2626) else Color(0xFF15803D),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Info Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = product.nameBangla,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Text(
                    text = "${product.unit} • ${product.code}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "৳${product.tradeOfferPrice}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0284C7)
                        )
                        if (product.tradeOfferPrice < product.price) {
                            Text(
                                text = "৳${product.price}",
                                fontSize = 10.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    if (cartQuantity == 0) {
                        Button(
                            onClick = onAddToCart,
                            enabled = product.stockQuantity > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_add_grid_${product.id}")
                        ) {
                            Text(if (product.stockQuantity > 0) "+ যোগ" else "স্টক শেষ", fontSize = 10.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(8.dp))
                                .padding(horizontal = 2.dp, vertical = 1.dp)
                        ) {
                            IconButton(
                                onClick = onDecrement,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Dec", tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                            }

                            Text(
                                text = "$cartQuantity",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )

                            IconButton(
                                onClick = onIncrement,
                                enabled = cartQuantity < product.stockQuantity,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Inc", tint = Color(0xFF0284C7), modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductListCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onImageClick: () -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockThreshold

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_list_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Product Image Thumbnail
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { onImageClick() },
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(product.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.nameBangla,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(imageVector = Icons.Default.Image, contentDescription = "No Image", tint = Color.Gray, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = product.nameBangla,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isLowStock) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "স্টক: ${product.stockQuantity}",
                            fontSize = 9.sp,
                            color = if (isLowStock) Color(0xFFDC2626) else Color(0xFF16A34A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Text(
                    text = "কোড: ${product.code} • একক: ${product.unit}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "৳${product.tradeOfferPrice}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0284C7)
                    )
                    if (product.tradeOfferPrice < product.price) {
                        Text(
                            text = "৳${product.price}",
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Action Button
            if (cartQuantity == 0) {
                Button(
                    onClick = onAddToCart,
                    enabled = product.stockQuantity > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_list_${product.id}")
                ) {
                    Text(text = if (product.stockQuantity > 0) "+ যোগ" else "স্টক নেই", fontSize = 11.sp)
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .border(1.dp, Color(0xFF0284C7), RoundedCornerShape(10.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Dec", tint = Color(0xFF0284C7))
                    }

                    Text(
                        text = "$cartQuantity",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        enabled = cartQuantity < product.stockQuantity,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Inc", tint = Color(0xFF0284C7))
                    }
                }
            }
        }
    }
}
