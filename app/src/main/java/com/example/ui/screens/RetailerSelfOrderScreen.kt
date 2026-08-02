package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RetailerEntity
import com.example.data.repository.CartItem

@Composable
fun RetailerSelfOrderScreen(
    currentShop: RetailerEntity?,
    products: List<ProductEntity>,
    cart: List<CartItem>,
    onAddToCart: (ProductEntity) -> Unit,
    onCheckoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
    ) {
        // Shop Welcome Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .testTag("retailer_welcome_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Store,
                        contentDescription = "Shop",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = currentShop?.shopName ?: "মেসার্স বিসমিল্লাহ স্টোর",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "প্রোপ্রাইটর: ${currentShop?.ownerName ?: "হাজী রফিকুল ইসলাম"} • ${currentShop?.address ?: "মিরপুর ১০, ঢাকা"}",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ট্রেড অফার ও ডিরেক্ট কারখানা ডিসকাউন্ট চলোমান",
                        fontSize = 10.sp,
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "বকেয়া লেজার: ৳${currentShop?.dueBalance ?: 0.0}",
                        fontSize = 11.sp,
                        color = Color(0xFFFACC15),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = "সরাসরি কারখানা ক্যাটালগ (Self-Order Store):",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )

        // Products Grid with E-Commerce Photos
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(products) { product ->
                val inCartQty = cart.find { it.product.id == product.id }?.quantity ?: 0

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("self_order_item_${product.id}")
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Product Photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.2f)
                                .background(Color(0xFFF1F5F9)),
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
                                Icon(Icons.Default.Image, contentDescription = "No Image", tint = Color.Gray)
                            }

                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopStart)
                                    .padding(6.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFDC2626))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text("অফার প্রাইস", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }

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
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "${product.unit} • ${product.code}",
                                fontSize = 10.sp,
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
                                        text = "৳${product.tradeOfferPrice}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF0284C7)
                                    )
                                    Text(
                                        text = "৳${product.price}",
                                        fontSize = 9.sp,
                                        color = Color.Gray
                                    )
                                }

                                Button(
                                    onClick = { onAddToCart(product) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (inCartQty > 0) Color(0xFF059669) else Color(0xFF0F172A)
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("btn_self_add_${product.id}")
                                ) {
                                    Text(if (inCartQty > 0) "$inCartQty টি" else "+ যোগ", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Self Order Bottom Bar
        val totalQty = cart.sumOf { it.quantity }
        val totalAmount = cart.sumOf { it.product.tradeOfferPrice * it.quantity }

        if (totalQty > 0) {
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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Cart", tint = Color(0xFF38BDF8))
                        Column {
                            Text("$totalQty টি পণ্য কার্টে আছে", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("৳$totalAmount BDT", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Button(
                        onClick = onCheckoutClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2136E)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_self_checkout")
                    ) {
                        Text("অর্ডার প্লেস করুন >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
