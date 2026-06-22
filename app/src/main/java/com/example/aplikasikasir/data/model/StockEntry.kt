package com.example.aplikasikasir.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StockEntry(
    @SerializedName("id")
    val id: Int,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("purchase_price")
    val purchasePrice: Double,
    @SerializedName("total_cost")
    val totalCost: Double,
    @SerializedName("supplier_name")
    val supplierName: String?,
    @SerializedName("entry_date")
    val entryDate: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("item")
    val item: Barang? = null
) : Parcelable
