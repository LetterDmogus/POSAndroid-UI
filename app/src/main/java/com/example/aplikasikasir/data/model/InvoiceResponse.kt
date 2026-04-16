package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class InvoiceResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("data")
    val data: InvoiceData?
)

data class InvoiceData(
    @SerializedName("header")
    val header: InvoiceHeader,
    @SerializedName("items")
    val items: List<InvoiceItem>,
    @SerializedName("summary")
    val summary: InvoiceSummary
)

data class InvoiceHeader(
    @SerializedName("invoice_no")
    val invoiceNo: String,
    @SerializedName("tanggal")
    val tanggal: String,
    @SerializedName("kasir")
    val kasir: String,
    @SerializedName("metode")
    val metode: String
)

data class InvoiceItem(
    @SerializedName("nama")
    val nama: String,
    @SerializedName("harga")
    val harga: Int,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("subtotal")
    val subtotal: Int
)

data class InvoiceSummary(
    @SerializedName("total")
    val total: Int,
    @SerializedName("bayar")
    val bayar: Int,
    @SerializedName("kembali")
    val kembali: Int
)