package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.InvoicePdfDialog
import com.example.ui.components.LowStockAlertBanner
import com.example.ui.components.OfflineSyncBanner
import com.example.ui.components.PaymentDialog
import com.example.ui.components.RoleSelectorBar
import com.example.ui.model.UserRole
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.InventoryManagementScreen
import com.example.ui.screens.LiveTrackingScreen
import com.example.ui.screens.OrderHistoryScreen
import com.example.ui.screens.ReportsAnalyticsScreen
import com.example.ui.screens.RetailerSelfOrderScreen
import com.example.ui.screens.SrOrderBookingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            MyApplicationTheme {
                val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
                val selectedRetailer by viewModel.selectedRetailer.collectAsStateWithLifecycle()
                val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
                val products by viewModel.allProducts.collectAsStateWithLifecycle()
                val lowStockProducts by viewModel.lowStockProducts.collectAsStateWithLifecycle()
                val retailers by viewModel.allRetailers.collectAsStateWithLifecycle()
                val orders by viewModel.allOrders.collectAsStateWithLifecycle()
                val srLocations by viewModel.allSrLocations.collectAsStateWithLifecycle()
                val cart by viewModel.cart.collectAsStateWithLifecycle()
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                val showInvoiceDialog by viewModel.showInvoiceDialog.collectAsStateWithLifecycle()
                val lastPlacedOrder by viewModel.lastPlacedOrder.collectAsStateWithLifecycle()
                val lastGeneratedInvoice by viewModel.lastGeneratedInvoice.collectAsStateWithLifecycle()
                val snackMessage by viewModel.snackMessage.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }
                var currentTab by remember { mutableIntStateOf(0) }
                var showPaymentDialog by remember { mutableStateOf(false) }

                LaunchedEffect(snackMessage) {
                    snackMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearSnack()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = "OrderCut Pro",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "কোম্পানি সেলস অর্ডার ও ইনভেন্টরি অ্যাপ",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F172A)),
                            modifier = Modifier.testTag("main_top_bar")
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp,
                            modifier = Modifier.testTag("bottom_nav_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentTab == 0,
                                onClick = { currentTab = 0 },
                                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                                label = { Text("ড্যাশবোর্ড", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF0284C7)),
                                modifier = Modifier.testTag("nav_dashboard")
                            )
                            NavigationBarItem(
                                selected = currentTab == 1,
                                onClick = { currentTab = 1 },
                                icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = "Order") },
                                label = { Text("অর্ডার কাটিং", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF0284C7)),
                                modifier = Modifier.testTag("nav_order_booking")
                            )
                            NavigationBarItem(
                                selected = currentTab == 2,
                                onClick = { currentTab = 2 },
                                icon = { Icon(Icons.Default.Inventory, contentDescription = "Inventory") },
                                label = { Text("ইনভেন্টরি", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF059669)),
                                modifier = Modifier.testTag("nav_inventory")
                            )
                            NavigationBarItem(
                                selected = currentTab == 3,
                                onClick = { currentTab = 3 },
                                icon = { Icon(Icons.Default.LocationOn, contentDescription = "Tracking") },
                                label = { Text("ট্র্যাকিং", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF7C3AED)),
                                modifier = Modifier.testTag("nav_tracking")
                            )
                            NavigationBarItem(
                                selected = currentTab == 4,
                                onClick = { currentTab = 4 },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Reports") },
                                label = { Text("রিপোর্ট", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFFD97706)),
                                modifier = Modifier.testTag("nav_reports")
                            )
                            NavigationBarItem(
                                selected = currentTab == 5,
                                onClick = { currentTab = 5 },
                                icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Invoices") },
                                label = { Text("ইনভয়েস", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(selectedIconColor = Color(0xFF0F172A)),
                                modifier = Modifier.testTag("nav_invoices")
                            )
                        }
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Role Selector Bar
                        RoleSelectorBar(
                            selectedRole = selectedRole,
                            onRoleSelected = { viewModel.setRole(it) }
                        )

                        // Offline Sync State Banner
                        OfflineSyncBanner(
                            isOnline = isOnline,
                            onToggleOnline = { viewModel.toggleOnlineStatus() },
                            onSyncNow = { viewModel.syncNow() }
                        )

                        // Low Stock Notification Banner
                        LowStockAlertBanner(
                            lowStockItems = lowStockProducts,
                            onClickViewInventory = { currentTab = 2 }
                        )

                        // Screen Router
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            when (currentTab) {
                                0 -> AdminDashboardScreen(
                                    orders = orders,
                                    lowStockItems = lowStockProducts,
                                    srLocations = srLocations,
                                    onNavigateToOrderBooking = { currentTab = 1 },
                                    onNavigateToInventory = { currentTab = 2 },
                                    onNavigateToTracking = { currentTab = 3 },
                                    onNavigateToReports = { currentTab = 4 },
                                    onNavigateToOrders = { currentTab = 5 }
                                )

                                1 -> {
                                    if (selectedRole == UserRole.RETAILER) {
                                        RetailerSelfOrderScreen(
                                            currentShop = selectedRetailer ?: retailers.firstOrNull(),
                                            products = products,
                                            cart = cart,
                                            onAddToCart = { viewModel.addToCart(it, 1) },
                                            onCheckoutClick = { showPaymentDialog = true }
                                        )
                                    } else {
                                        SrOrderBookingScreen(
                                            userRole = selectedRole,
                                            products = products,
                                            retailers = retailers,
                                            selectedRetailer = selectedRetailer,
                                            cart = cart,
                                            searchQuery = searchQuery,
                                            selectedCategory = selectedCategory,
                                            onSelectRetailer = { viewModel.selectRetailer(it) },
                                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                            onCategoryChange = { viewModel.setCategory(it) },
                                            onAddToCart = { viewModel.addToCart(it, 1) },
                                            onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                                            onCheckoutClick = { showPaymentDialog = true }
                                        )
                                    }
                                }

                                2 -> InventoryManagementScreen(
                                    products = products,
                                    lowStockProducts = lowStockProducts,
                                    onAddStock = { id, qty -> viewModel.addStock(id, qty) },
                                    onAddNewProduct = { name, cat, price, stock, unit ->
                                        viewModel.addNewProduct(name, cat, price, stock, unit)
                                    }
                                )

                                3 -> LiveTrackingScreen(
                                    srLocations = srLocations,
                                    onUpdateSrLocation = { id, name, beat, lat, lng, shop ->
                                        viewModel.updateSrLocation(id, name, beat, lat, lng, shop)
                                    }
                                )

                                4 -> ReportsAnalyticsScreen(
                                    orders = orders,
                                    retailers = retailers
                                )

                                5 -> OrderHistoryScreen(
                                    orders = orders
                                )
                            }
                        }
                    }

                    // Payment Dialog Modal
                    if (showPaymentDialog) {
                        val totalCartVal = cart.sumOf { it.product.tradeOfferPrice * it.quantity }
                        PaymentDialog(
                            totalAmount = totalCartVal,
                            onConfirmPayment = { method, status, discount ->
                                showPaymentDialog = false
                                viewModel.placeOrder(method, status, discount)
                            },
                            onDismiss = { showPaymentDialog = false }
                        )
                    }

                    // Invoice PDF Modal
                    if (showInvoiceDialog) {
                        InvoicePdfDialog(
                            order = lastPlacedOrder,
                            pdfFile = lastGeneratedInvoice,
                            onDismiss = { viewModel.dismissInvoiceDialog() }
                        )
                    }
                }
            }
        }
    }
}
