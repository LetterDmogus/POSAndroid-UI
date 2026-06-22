package com.example.aplikasikasir.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val barang: Barang,
    var qty: Int
) : Parcelable {
    val discountAmount: Double
        get() {
            val discount = barang.discount
            if (discount == null || !discount.isActive) return 0.0
            return when (discount.type) {
                "percentage" -> {
                    // round(selling_price * value / 100) * qty
                    val perUnit = Math.round(barang.hargaJual * ((discount.value ?: 0.0) / 100.0)).toDouble()
                    perUnit * qty
                }
                "bogo" -> {
                    // floor(qty / 2) * selling_price
                    val freeItems = qty / 2
                    freeItems * barang.hargaJual
                }
                else -> 0.0
            }
        }

    val subtotal: Double
        get() = (barang.hargaJual * qty) - discountAmount
}