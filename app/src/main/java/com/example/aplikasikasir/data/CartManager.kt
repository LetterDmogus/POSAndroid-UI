package com.example.aplikasikasir.data

import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.data.model.CartItem

object CartManager {
    private val items = mutableListOf<CartItem>()

    fun addItem(barang: Barang) {
        val existingItem = items.find { it.barang.id == barang.id }
        if (existingItem != null) {
            existingItem.qty++
        } else {
            items.add(CartItem(barang, 1))
        }
    }

    fun getItems(): List<CartItem> = items

    fun removeItem(barangId: Int) {
        items.removeAll { it.barang.id == barangId }
    }

    fun updateQty(barangId: Int, newQty: Int) {
        val item = items.find { it.barang.id == barangId }
        if (item != null) {
            if (newQty > 0) {
                item.qty = newQty
            } else {
                removeItem(barangId)
            }
        }
    }

    fun clearCart() {
        items.clear()
    }

    fun getTotalPrice(): Double {
        return items.sumOf { it.subtotal }
    }

    fun getItemCount(): Int {
        return items.sumOf { it.qty }
    }
}