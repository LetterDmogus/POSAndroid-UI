package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class DashboardResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: DashboardData
)

data class DashboardData(
    @SerializedName("today")
    val today: TodaySummary,
    @SerializedName("low_stock")
    val lowStock: List<Barang>,
    @SerializedName("sales_trend")
    val salesTrend: List<SalesTrend>,
    @SerializedName("top_products")
    val topProducts: List<TopProduct>,
    @SerializedName("payment_methods")
    val paymentMethods: List<PaymentMethodStat>
)

data class TodaySummary(
    @SerializedName("today")
    val today: TodaySummary?, // legacy safety
    @SerializedName("omzet")
    val omzet: Int,
    @SerializedName("transactions")
    val transactions: Int
)

data class SalesTrend(
    @SerializedName("label")
    val label: String,
    @SerializedName("total")
    val total: Int
)

data class TopProduct(
    @SerializedName("item_name_backup")
    val namaBarang: String,
    @SerializedName("total_qty")
    val totalQty: Int
)

data class PaymentMethodStat(
    @SerializedName("payment_method")
    val metodePembayaran: String,
    @SerializedName("count")
    val count: Int
)