package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("bayar")
    val bayar: Double,
    @SerializedName("metode_pembayaran")
    val metodePembayaran: String,
    @SerializedName("catatan")
    val catatan: String?,
    @SerializedName("items")
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @SerializedName("barang_id")
    val barangId: Int,
    @SerializedName("qty")
    val qty: Int
)

data class OrderResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String,
    @SerializedName("data")
    val data: OrderData?
)

data class OrderData(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nomor_invoice")
    val nomorInvoice: String,
    @SerializedName("total_harga")
    val totalHarga: Double,
    @SerializedName("kembali")
    val kembali: Double
)