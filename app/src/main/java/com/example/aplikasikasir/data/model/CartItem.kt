package com.example.aplikasikasir.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val barang: Barang,
    var qty: Int
) : Parcelable {
    val subtotal: Double
        get() = barang.hargaJual * qty
}