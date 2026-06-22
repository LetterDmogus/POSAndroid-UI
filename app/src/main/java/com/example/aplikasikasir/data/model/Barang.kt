package com.example.aplikasikasir.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val namaKategori: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("items_count")
    val barangsCount: Int? = null
) : Parcelable

@Parcelize
data class Barang(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("name")
    val namaBarang: String,
    @SerializedName("description")
    val deskripsi: String?,
    @SerializedName("purchase_price")
    val hargaBeli: Double,
    @SerializedName("selling_price")
    val hargaJual: Double,
    @SerializedName("stock")
    val stok: Int,
    @SerializedName("unit")
    val satuan: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("image")
    val foto: String?,
    @SerializedName("image_url")
    val fotoUrl: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("category")
    val category: Category? = null,
    @SerializedName("discount")
    val discount: Discount? = null
) : Parcelable

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("count")
    val count: Int? = null,
    @SerializedName("total_omzet")
    val totalOmzet: Int? = null
)