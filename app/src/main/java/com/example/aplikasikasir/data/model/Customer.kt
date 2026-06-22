package com.example.aplikasikasir.data.model

import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("address")
    val address: String?
)
