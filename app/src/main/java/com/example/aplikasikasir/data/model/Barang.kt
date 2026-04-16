package com.example.aplikasikasir.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    @SerializedName("id")
    val id: Int,
    @SerializedName("nama_kategori")
    val namaKategori: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("barangs_count")
    val barangsCount: Int? = null
) : Parcelable

@Parcelize
data class Barang(
    @SerializedName("id")
    val id: Int,
    @SerializedName("sku")
    val sku: String,
    @SerializedName("nama_barang")
    val namaBarang: String,
    @SerializedName("deskripsi")
    val deskripsi: String?,
    @SerializedName("harga_beli")
    val hargaBeli: Double,
    @SerializedName("harga_jual")
    val hargaJual: Double,
    @SerializedName("stok")
    val stok: Int,
    @SerializedName("satuan")
    val satuan: String,
    @SerializedName("category_id")
    val categoryId: Int,
    @SerializedName("foto")
    val foto: String?,
    @SerializedName("foto_url")
    val fotoUrl: String?,
    @SerializedName("is_active")
    val isActive: Boolean,
    @SerializedName("category")
    val category: Category? = null
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