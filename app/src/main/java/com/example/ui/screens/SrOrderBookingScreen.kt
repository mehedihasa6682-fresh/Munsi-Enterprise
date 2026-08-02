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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color(0xFFF8FAF8))
    ) {
        // Retailer / Shop Selector Bar
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
                        .background(Color(0xFF1F2937).copy(alpha = 0.05f))
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
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Shop",
                                tint = Color(0xFF0284C7),
                                modifier = Modifier.size(20.dp)
                            )
                            Column {
                                Text(
                                    text = currentRetailer?.shopName ?: "দোকান নির্বাচন করুন",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${currentRetailer?.routeBeat ?: ""} • বকেয়া: ৳${currentRetailer?.dueBalance ?: 0.0}",
                                    fontSize = 10.sp,
                                    color = Color(0xFFDC2626)
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
                                        Text(shop.shopName, fontWeight = FontWeight.Bold)
                                        Text("${shop.ownerName} (${shop.routeBeat})", fontSize = 10.sp, color = Color.Gray)
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

        // Search & Filter Category Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("পণ্য অথবা কোড দিয়ে খুঁজুন...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_product")
            )

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF0F172A) else Color.White)
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

        // Product Catalog List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredProducts) { product ->
                val cartQuantity = cart.find { it.product.id == product.id }?.quantity ?: 0

                ProductOrderCard(
                    product = product,
                    cartQuantity = cartQuantity,
                    onAddToCart = { onAddToCart(product) },
                    onIncrement = { onUpdateQuantity(product.id, cartQuantity + 1) },
                    onDecrement = { onUpdateQuantity(product.id, cartQuantity - 1) }
                )
            }
        }

        // Bottom Cart Summary Bar
        val totalCartItems = cart.sumOf { it.quantity }
        val totalCartAmount = cart.sumOf { it.product.tradeOfferPrice * it.quantity }

        if (totalCartItems > 0) {
            Surface(
                color = Color(0xFF0F172A),
                shadowElevation = 8.dp,
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
                            text = "$totalCartItems টি আইটেম সিলেক্টেড",
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
                        Text("পেমেন্ট ও ইনভয়েস >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProductOrderCard(
    product: ProductEntity,
    cartQuantity: Int,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    val isLowStock = product.stockQuantity <= product.minStockThreshold

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = product.nameBangla,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    // Stock Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isLowStock) Color(0xFFFEE2E2) else Color(0xFFDCFCE7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
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
                    text = "কোড: ${product.code} • ইউনিট: ${product.unit}",
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

            // Quantity selector or Add Button
            if (cartQuantity == 0) {
                Button(
                    onClick = onAddToCart,
                    enabled = product.stockQuantity > 0,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_add_${product.id}")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(14.dp))
                    Text(text = if (product.stockQuantity > 0) "যোগ করুন" else "স্টক নেই", fontSize = 11.sp)
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
                        modifier = Modifier.size(28.dp).testTag("btn_dec_${product.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Remove, contentDescription = "Dec", tint = Color(0xFF0284C7))
                    }

                    Text(
                        text = "$cartQuantity",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        enabled = cartQuantity < product.stockQuantity,
                        modifier = Modifier.size(28.dp).testTag("btn_inc_${product.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Inc", tint = Color(0xFF0284C7))
                    }
                }
            }
        }
    }
}
