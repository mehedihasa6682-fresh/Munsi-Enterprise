package com.example.ui.model

enum class UserRole(
    val titleBangla: String,
    val titleEnglish: String,
    val description: String
) {
    ADMIN("এ্যাডমিন (Admin)", "Admin", "কোম্পানি ওভারভিউ, ইনভেন্টরি, এসআর ট্র্যাকিং ও রিপোর্ট"),
    SR("এসআর (Sales Rep)", "Sales Rep", "ফিল্ড অর্ডার কাটিং, রুট প্ল্যান ও শপ ভিজিট"),
    DSR("ডিএসআর (Distributor SR)", "Distributor SR", "ডিলার অর্ডারিং, বাল্ক ডেলিভারি ও রিসিভ"),
    DEALER("ডিলার (Dealer)", "Dealer", "প্রাইমারি স্টক অর্ডার ও সেকেন্ডারি রিকুইজিশন"),
    RETAILER("রিটেইলার (দোকানদার)", "Retailer", "সরাসরি সেলফ অর্ডার, ইনভয়েস ও ট্র্যাকিং")
}

enum class PaymentMethod(val displayName: String, val iconRes: String) {
    BKASH("bKash (বিকাশ)", "bkash"),
    NAGAD("Nagad (নগদ)", "nagad"),
    UPAY("Upay (ইউপে)", "upay"),
    CASH("Cash on Delivery (ক্যাশ)", "cash"),
    LEDGER("Ledger / Credit (বাকি)", "ledger")
}

enum class OrderStatus(val banglaText: String) {
    PENDING("পেন্ডিং"),
    CONFIRMED("কনফার্মড"),
    PROCESSING("প্রসেসিং"),
    DELIVERED("ডেলিভার্ড"),
    CANCELLED("বাতিল")
}
