package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class OrderHistory(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nomor_invoice")
    val nomorInvoice: String,
    @SerializedName("total_harga")
    val totalHarga: Double,
    @SerializedName("bayar")
    val bayar: Double,
    @SerializedName("kembali")
    val kembali: Double,
    @SerializedName("metode_pembayaran")
    val metodePembayaran: String,
    @SerializedName("created_at")
    val createdAt: String
)

data class OrderHistoryResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: List<OrderHistory>
)