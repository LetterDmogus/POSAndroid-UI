package com.example.aplikasikasir.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Discount(
    @SerializedName("id")
    val id: Int,
    @SerializedName("item_id")
    val itemId: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String, // "percentage" or "bogo"
    @SerializedName("value")
    val value: Double?,
    @SerializedName("is_active")
    val isActive: Boolean
) : Parcelable
