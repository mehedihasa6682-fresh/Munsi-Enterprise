package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.OrderDao
import com.example.data.local.dao.ProductDao
import com.example.data.local.dao.RetailerDao
import com.example.data.local.dao.SrLocationDao
import com.example.data.local.entities.OrderEntity
import com.example.data.local.entities.OrderItemEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.RetailerEntity
import com.example.data.local.entities.SrLocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        RetailerEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        SrLocationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun retailerDao(): RetailerDao
    abstract fun orderDao(): OrderDao
    abstract fun srLocationDao(): SrLocationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ordercut_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class AppDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(
                            database.productDao(),
                            database.retailerDao(),
                            database.srLocationDao()
                        )
                    }
                }
            }
        }

        private suspend fun populateInitialData(
            productDao: ProductDao,
            retailerDao: RetailerDao,
            srLocationDao: SrLocationDao
        ) {
            // Seed Initial FMCG / Consumer Goods Products
            val initialProducts = listOf(
                ProductEntity(name = "Fresh Fortified Soybean Oil 5L", nameBangla = "ফ্রেশ সয়াবিন তেল ৫ লিটার", category = "খাদ্য ও তেল", price = 820.0, tradeOfferPrice = 790.0, stockQuantity = 120, minStockThreshold = 25, unit = "বোতল", code = "OIL-5L"),
                ProductEntity(name = "Pran Premium Miniket Rice 25kg", nameBangla = "প্রাণ মিনিকেট চাল ২৫ কেজি", category = "চাল ও দানা", price = 1750.0, tradeOfferPrice = 1700.0, stockQuantity = 80, minStockThreshold = 20, unit = "বস্তা", code = "RICE-25K"),
                ProductEntity(name = "Teer Refined Sugar 1kg Pack", nameBangla = "তীর পরিশোধিত চিনি ১ কেজী", category = "চিনি ও গুঁড়া", price = 135.0, tradeOfferPrice = 130.0, stockQuantity = 200, minStockThreshold = 40, unit = "প্যাক", code = "SGR-1K"),
                ProductEntity(name = "Ispahani Mirzapore Tea Bag 100s", nameBangla = "ইস্পাহানি মির্জাপুর টি ব্যাগ", category = "চা ও পানীয়", price = 240.0, tradeOfferPrice = 225.0, stockQuantity = 18, minStockThreshold = 30, unit = "বক্স", code = "TEA-100"),
                ProductEntity(name = "ACI Pure Salt 1kg", nameBangla = "এসিআই পিওর লবণ ১ কেজি", category = "মশলা ও লবণ", price = 42.0, tradeOfferPrice = 39.0, stockQuantity = 350, minStockThreshold = 50, unit = "প্যাক", code = "SLT-1K"),
                ProductEntity(name = "Ruchi Chanachur Spicy 200g", nameBangla = "রুচি চানাচুর ঝাল ২০০ গ্রাম", category = "স্ন্যাক্স", price = 50.0, tradeOfferPrice = 45.0, stockQuantity = 12, minStockThreshold = 35, unit = "প্যাক", code = "SNC-200"),
                ProductEntity(name = "Wheel Washing Powder 1kg", nameBangla = "হুইল ওয়াশিং পাউডার ১ কেজি", category = "ডিটারজেন্ট", price = 130.0, tradeOfferPrice = 122.0, stockQuantity = 140, minStockThreshold = 30, unit = "প্যাক", code = "DET-1K"),
                ProductEntity(name = "Lux Velvet Glow Soap 100g Pack", nameBangla = "লাক্স সাবান ১০০ গ্রাম", category = "প্রসাধন", price = 65.0, tradeOfferPrice = 60.0, stockQuantity = 220, minStockThreshold = 40, unit = "পিস", code = "SOP-100")
            )
            productDao.insertProducts(initialProducts)

            // Seed Initial Retailers / Shops
            val initialRetailers = listOf(
                RetailerEntity(shopName = "মেসার্স বিসমিল্লাহ স্টোর", ownerName = "হাজী রফিকুল ইসলাম", phone = "01711223344", address = "শপ #৪, মিরপুর ১০ গোলচত্বর", routeBeat = "মিরপুর বিট", dueBalance = 12500.0, totalOrdersCount = 14, latitude = 23.8069, longitude = 90.3687),
                RetailerEntity(shopName = "মা বাবার দোয়া জেনারেল স্টোর", ownerName = "আলমগীর হোসেন", phone = "01812345678", address = "রোড #২, ধানমন্ডি, ঢাকা", routeBeat = "ধানমন্ডি বিট", dueBalance = 4500.0, totalOrdersCount = 8, latitude = 23.7461, longitude = 90.3742),
                RetailerEntity(shopName = "নিউ ঢাকা জেনারেল ট্রেডার্স", ownerName = "মাহাবুব আলম", phone = "01987654321", address = "মতিঝিল শপিং কমপ্লেক্স", routeBeat = "মতিঝিল বিট", dueBalance = 0.0, totalOrdersCount = 22, latitude = 23.7289, longitude = 90.4183),
                RetailerEntity(shopName = "সোনার বাংলা ডিপার্টমেন্টাল স্টোর", ownerName = "কামরুল হাসান", phone = "01655443322", address = "উত্তরা সেক্টর ৭ বাজার", routeBeat = "উত্তরা বিট", dueBalance = 28000.0, totalOrdersCount = 31, latitude = 23.8722, longitude = 90.3989)
            )
            retailerDao.insertRetailers(initialRetailers)

            // Seed Initial SR Location Trackings
            val initialSrLocations = listOf(
                SrLocationEntity(srId = "SR-101", srName = "তানভীর আহমেদ (SR)", beatRoute = "মিরপুর বিট", latitude = 23.8069, longitude = 90.3687, lastVisitedShop = "মেসার্স বিসমিল্লাহ স্টোর", ordersTakenToday = 8, status = "VISITING"),
                SrLocationEntity(srId = "SR-102", srName = "শাহিন রেজা (SR)", beatRoute = "ধানমন্ডি বিট", latitude = 23.7461, longitude = 90.3742, lastVisitedShop = "মা বাবার দোয়া ট্রেডার্স", ordersTakenToday = 12, status = "ACTIVE"),
                SrLocationEntity(srId = "DSR-201", srName = "জাহিদুল ইসলাম (DSR)", beatRoute = "মতিঝিল বিট", latitude = 23.7289, longitude = 90.4183, lastVisitedShop = "নিউ ঢাকা ডিপার্টমেন্টাল", ordersTakenToday = 5, status = "ACTIVE")
            )
            srLocationDao.insertLocations(initialSrLocations)
        }
    }
}
