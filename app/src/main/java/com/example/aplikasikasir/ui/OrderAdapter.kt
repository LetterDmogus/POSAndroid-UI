package com.example.aplikasikasir.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.InvoiceActivity
import com.example.aplikasikasir.data.model.OrderHistory
import com.example.aplikasikasir.databinding.ItemOrderBinding
import java.text.NumberFormat
import java.util.*

class OrderAdapter(private var listOrders: List<OrderHistory>) :
    RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = listOrders[position]
        with(holder.binding) {
            tvHistoryInvoice.text = order.nomorInvoice
            tvHistoryDate.text = order.createdAt
            tvHistoryMethod.text = order.metodePembayaran.uppercase()
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHistoryTotal.text = formatRupiah.format(order.totalHarga)

            root.setOnClickListener {
                val intent = Intent(it.context, InvoiceActivity::class.java)
                intent.putExtra("EXTRA_INVOICE", order.nomorInvoice)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = listOrders.size

    fun setData(newList: List<OrderHistory>) {
        listOrders = newList
        notifyDataSetChanged()
    }
}