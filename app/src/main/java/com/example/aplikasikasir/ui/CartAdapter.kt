package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.CartManager
import com.example.aplikasikasir.data.model.CartItem
import com.example.aplikasikasir.databinding.ItemCartBinding
import java.text.NumberFormat
import java.util.*

class CartAdapter(
    private var listCart: List<CartItem>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = listCart[position]
        with(holder.binding) {
            tvCartNama.text = item.barang.namaBarang
            tvCartQty.text = item.qty.toString()
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvCartHarga.text = formatRupiah.format(item.subtotal)

            btnPlus.setOnClickListener {
                item.qty++
                notifyItemChanged(position)
                onUpdate()
            }

            btnMinus.setOnClickListener {
                if (item.qty > 1) {
                    item.qty--
                    notifyItemChanged(position)
                    onUpdate()
                }
            }

            btnDelete.setOnClickListener {
                CartManager.removeItem(item.barang.id)
                listCart = CartManager.getItems()
                notifyDataSetChanged()
                onUpdate()
            }
        }
    }

    override fun getItemCount(): Int = listCart.size

    fun setData(newList: List<CartItem>) {
        listCart = newList
        notifyDataSetChanged()
    }
}