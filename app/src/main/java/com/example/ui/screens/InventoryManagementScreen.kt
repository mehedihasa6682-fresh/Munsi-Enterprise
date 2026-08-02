package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ProductEntity

@Composable
fun InventoryManagementScreen(
    products: List<ProductEntity>,
    lowStockProducts: List<ProductEntity>,
    onAddStock: (productId: Long, qty: Int) -> Unit,
    onAddNewProduct: (nameBangla: String, category: String, price: Double, stock: Int, unit: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showRestockDialog by remember { mutableStateOf(false) }
    var selectedProductForRestock by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddProductDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAF8))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Inventory,
                        contentDescription = "Inventory",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        text = "লাইভ ইনভেন্টরি ও প্রোডাক্ট স্টক",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Button(
                    onClick = { showAddProductDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_add_new_product_dialog")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Text("নতুন পণ্য", fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Low Stock Warning Box
            if (lowStockProducts.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("low_stock_summary_card")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Warning, contentDescription = "Alert", tint = Color(0xFFDC2626))
                            Text(
                                text = "স্বয়ংক্রিয় স্টক সতর্কতা (${lowStockProducts.size}টি পণ্যের স্টক কম!):",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        lowStockProducts.forEach { item ->
                            Text(
                                text = "• ${item.nameBangla} - অবশিষ্ট স্টক: ${item.stockQuantity} ${item.unit} (থ্রেশহোল্ড: ${item.minStockThreshold})",
                                fontSize = 11.sp,
                                color = Color(0xFF991B1B)
                            )
                        }
                    }
                }
            }

            // Products Table / List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(products) { product ->
                    val isLow = product.stockQuantity <= product.minStockThreshold

                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inventory_item_${product.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.nameBangla,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "ক্যাটাগরি: ${product.category} • কোড: ${product.code}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF64748B)
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "বর্তমান স্টক: ${product.stockQuantity} ${product.unit}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isLow) Color(0xFFDC2626) else Color(0xFF059669)
                                    )
                                    Text(
                                        text = "মূল্য: ৳${product.tradeOfferPrice}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF0284C7)
                                    )
                                }
                            }

                            Button(
                                onClick = {
                                    selectedProductForRestock = product
                                    showRestockDialog = true
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("btn_restock_${product.id}")
                            ) {
                                Icon(imageVector = Icons.Default.AddBox, contentDescription = "Restock", modifier = Modifier.size(14.dp))
                                Text("মজুদ যোগ", fontSize = 11.sp, modifier = Modifier.padding(start = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Restock Dialog
    if (showRestockDialog && selectedProductForRestock != null) {
        var restockQtyInput by remember { mutableStateOf("50") }
        val item = selectedProductForRestock!!

        Dialog(onDismissRequest = { showRestockDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "মজুদ রিফিল করুন (Stock Entry)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = item.nameBangla, fontSize = 12.sp, color = Color.Gray)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = restockQtyInput,
                        onValueChange = { restockQtyInput = it },
                        label = { Text("নতুন যোগ করার পরিমাণ (${item.unit})") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_restock_qty")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showRestockDialog = false }) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val qty = restockQtyInput.toIntOrNull() ?: 0
                                if (qty > 0) {
                                    onAddStock(item.id, qty)
                                    showRestockDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                        ) {
                            Text("মজুদ সেভ করুন")
                        }
                    }
                }
            }
        }
    }

    // Add Product Dialog
    if (showAddProductDialog) {
        var nameInput by remember { mutableStateOf("") }
        var categoryInput by remember { mutableStateOf("খাদ্য ও তেল") }
        var priceInput by remember { mutableStateOf("") }
        var stockInput by remember { mutableStateOf("100") }
        var unitInput by remember { mutableStateOf("প্যাক") }

        Dialog(onDismissRequest = { showAddProductDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "নতুন পণ্য এন্ট্রি (Add Product)", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("পণ্যের বাংলা নাম") },
                        modifier = Modifier.fillMaxWidth().testTag("input_product_name")
                    )
                    OutlinedTextField(
                        value = categoryInput,
                        onValueChange = { categoryInput = it },
                        label = { Text("ক্যাটাগরি (চাল/তেল/মশলা)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_product_cat")
                    )
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { priceInput = it },
                        label = { Text("একক মূল্য (৳ BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_product_price")
                    )
                    OutlinedTextField(
                        value = stockInput,
                        onValueChange = { stockInput = it },
                        label = { Text("প্রাথমিক মজুদ পরিমাণ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth().testTag("input_product_stock")
                    )
                    OutlinedTextField(
                        value = unitInput,
                        onValueChange = { unitInput = it },
                        label = { Text("একক (কার্টন/প্যাক/বোতল)") },
                        modifier = Modifier.fillMaxWidth().testTag("input_product_unit")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddProductDialog = false }) {
                            Text("বাতিল")
                        }
                        Button(
                            onClick = {
                                val prc = priceInput.toDoubleOrNull() ?: 0.0
                                val stk = stockInput.toIntOrNull() ?: 0
                                if (nameInput.isNotBlank() && prc > 0) {
                                    onAddNewProduct(nameInput, categoryInput, prc, stk, unitInput)
                                    showAddProductDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Text("পণ্য যোগ করুন")
                        }
                    }
                }
            }
        }
    }
}
