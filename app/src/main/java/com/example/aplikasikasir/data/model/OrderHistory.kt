package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class OrderHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("invoice_number")
    val nomorInvoice: String,
    @SerializedName("total_price")
    val totalHarga: Double,
    @SerializedName("paid_amount")
    val bayar: Double,
    @SerializedName("change_amount")
    val kembali: Double,
    @SerializedName("payment_method")
    val metodePembayaran: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class OrderHistoryResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("total_omzet")
    val totalOmzet: Double?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("data")
    val data: List<OrderHistory>
)