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
            // Seed Initial FMCG / Consumer Goods Products with Images & Units (KG, Liter, Bag, Pack, Box)
            val initialProducts = listOf(
                ProductEntity(
                    name = "Fresh Fortified Soybean Oil 5L",
                    nameBangla = "ফ্রেশ সয়াবিন তেল ৫ লিটার",
                    category = "খাদ্য ও তেল",
                    price = 820.0,
                    tradeOfferPrice = 790.0,
                    stockQuantity = 120,
                    minStockThreshold = 25,
                    unit = "লিটার (বোতল)",
                    code = "OIL-5L",
                    imageUrl = "https://images.unsplash.com/photo-1620706857370-e1b9770e8bb1?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Pran Premium Miniket Rice 25kg Bag",
                    nameBangla = "প্রাণ প্রিমিয়াম মিনিকেট চাল ২৫ কেজি বস্তা",
                    category = "চাল ও দানা",
                    price = 1750.0,
                    tradeOfferPrice = 1700.0,
                    stockQuantity = 85,
                    minStockThreshold = 20,
                    unit = "কেজি (বস্তা)",
                    code = "RICE-25K",
                    imageUrl = "https://images.unsplash.com/photo-1586201375761-83865001e31c?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Teer Refined Sugar 1kg Pack",
                    nameBangla = "তীর পরিশোধিত চিনি ১ কেজি প্যাক",
                    category = "চিনি ও গুঁড়া",
                    price = 135.0,
                    tradeOfferPrice = 130.0,
                    stockQuantity = 220,
                    minStockThreshold = 40,
                    unit = "কেজি (প্যাক)",
                    code = "SGR-1K",
                    imageUrl = "https://images.unsplash.com/photo-1581600140682-d4e68c8cde32?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Ispahani Mirzapore Premium Tea 500g",
                    nameBangla = "ইস্পাহানি মির্জাপুর চা ৫০০ গ্রাম প্যাক",
                    category = "চা ও পানীয়",
                    price = 240.0,
                    tradeOfferPrice = 225.0,
                    stockQuantity = 18,
                    minStockThreshold = 30,
                    unit = "প্যাক (গ্রাম)",
                    code = "TEA-500G",
                    imageUrl = "https://images.unsplash.com/photo-1576092768241-dec231879fc3?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "ACI Pure Iodine Salt 1kg",
                    nameBangla = "এসিআই পিওর আয়োডিনযুক্ত লবণ ১ কেজি",
                    category = "মশলা ও লবণ",
                    price = 42.0,
                    tradeOfferPrice = 39.0,
                    stockQuantity = 350,
                    minStockThreshold = 50,
                    unit = "কেজি (প্যাক)",
                    code = "SLT-1K",
                    imageUrl = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Ruchi Jhal Chanachur 200g Box",
                    nameBangla = "রুচি ঝাল চানাচুর ২০০ গ্রাম (২৪ পিস কার্টন)",
                    category = "স্ন্যাক্স",
                    price = 50.0,
                    tradeOfferPrice = 45.0,
                    stockQuantity = 12,
                    minStockThreshold = 35,
                    unit = "কার্টন (পিস)",
                    code = "SNC-CTN",
                    imageUrl = "https://images.unsplash.com/photo-1599490659213-e2b9527bd087?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Wheel Washing Powder 1kg Pack",
                    nameBangla = "হুইল ওয়াশিং পাউডার ১ কেজি প্যাক",
                    category = "ডিটারজেন্ট",
                    price = 130.0,
                    tradeOfferPrice = 122.0,
                    stockQuantity = 140,
                    minStockThreshold = 30,
                    unit = "কেজি (প্যাক)",
                    code = "DET-1K",
                    imageUrl = "https://images.unsplash.com/photo-1585842378054-ee2e52f94ba2?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Lux Velvet Glow Soap 100g Bar",
                    nameBangla = "লাক্স সাবান ১০০ গ্রাম (৬ পিস প্যাক)",
                    category = "প্রসাধন",
                    price = 65.0,
                    tradeOfferPrice = 60.0,
                    stockQuantity = 220,
                    minStockThreshold = 40,
                    unit = "পিস",
                    code = "SOP-100",
                    imageUrl = "https://images.unsplash.com/photo-1607006482802-613d52dd2d4e?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Teer Whole Wheat Atta 2kg Bag",
                    nameBangla = "তীর হোল গমের আটা ২ কেজি প্যাক",
                    category = "চাল ও দানা",
                    price = 120.0,
                    tradeOfferPrice = 112.0,
                    stockQuantity = 160,
                    minStockThreshold = 30,
                    unit = "কেজি (প্যাক)",
                    code = "ATTA-2K",
                    imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=500&q=80"
                ),
                ProductEntity(
                    name = "Radhuni Turmeric Powder 200g Pack",
                    nameBangla = "রাঁধুনী গুঁড়া হলুদ ২০০ গ্রাম",
                    category = "মশলা ও লবণ",
                    price = 95.0,
                    tradeOfferPrice = 88.0,
                    stockQuantity = 110,
                    minStockThreshold = 25,
                    unit = "প্যাক (গ্রাম)",
                    code = "SPICE-TRM",
                    imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=500&q=80"
                )
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
