package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class OrderRequest(
    @SerializedName("paid_amount")
    val bayar: Double,
    @SerializedName("payment_method")
    val metodePembayaran: String,
    @SerializedName("notes")
    val catatan: String?,
    @SerializedName("customer_id")
    val customerId: Int?,
    @SerializedName("items")
    val items: List<OrderItemRequest>
)

data class OrderItemRequest(
    @SerializedName("item_id")
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
    @SerializedName("invoice_number")
    val nomorInvoice: String,
    @SerializedName("total_price")
    val totalHarga: Double,
    @SerializedName("change_amount")
    val kembali: Double
)