package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.QrCode
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

@Composable
fun PaymentDialog(
    totalAmount: Double,
    onConfirmPayment: (paymentMethod: String, paymentStatus: String, discountAmount: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("bKash (বিক্যাশ Gateway)") }
    var discountInput by remember { mutableStateOf("0") }

    val discount = discountInput.toDoubleOrNull() ?: 0.0
    val finalPayable = (totalAmount - discount).coerceAtLeast(0.0)

    val methods = listOf(
        Pair("bKash (বিক্যাশ Gateway)", Icons.Default.QrCode),
        Pair("Nagad (নগদ Gateway)", Icons.Default.AccountBalanceWallet),
        Pair("নগদ ক্যাশ (Cash)", Icons.Default.Money),
        Pair("লেজার বাকি (Ledger Credit)", Icons.Default.CreditCard)
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("payment_dialog_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "অনলাইন পেমেন্ট ও অর্ডার কনফার্মেশন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Summary Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF0F172A))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("মোট পণ্যের মূল্য:", fontSize = 12.sp, color = Color(0xFF94A3B8))
                            Text("৳$totalAmount", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        if (discount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("স্পেশাল ডিসকাউন্ট:", fontSize = 12.sp, color = Color(0xFFFACC15))
                                Text("-৳$discount", fontSize = 12.sp, color = Color(0xFFFACC15), fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("সর্বমোট প্রদেয় বিল:", fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("৳$finalPayable BDT", fontSize = 16.sp, color = Color(0xFF38BDF8), fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("পেমেন্ট মাধ্যম সিলেক্ট করুন:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))

                Spacer(modifier = Modifier.height(6.dp))

                methods.forEach { (method, icon) ->
                    val isSelected = method == selectedMethod
                    val isOnlineGateway = method.contains("Gateway")

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFF0284C7).copy(alpha = 0.1f) else Color(0xFFF8FAF8))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color(0xFF0284C7) else Color(0xFFE2E8F0),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedMethod = method }
                            .padding(10.dp)
                            .testTag("payment_method_${method.take(5)}")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = method,
                                    tint = if (method.contains("bKash")) Color(0xFFE2136E) else if (method.contains("Nagad")) Color(0xFFF16323) else Color(0xFF0284C7),
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = method,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = Color(0xFF0F172A)
                                )
                            }

                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = "Checked", tint = Color(0xFF0284C7), modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = discountInput,
                    onValueChange = { discountInput = it },
                    label = { Text("ছাড় / ট্রেড ডিসকাউন্ট (৳ BDT)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_discount")
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("বাতিল", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    Button(
                        onClick = {
                            val status = if (selectedMethod.contains("Ledger")) "PENDING" else "PAID"
                            onConfirmPayment(selectedMethod, status, discount)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_confirm_order_payment")
                    ) {
                        Text("অর্ডার সাবমিট ও ইনভয়েস", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
