package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.StockEntry
import com.example.aplikasikasir.databinding.ItemManageStockEntryBinding
import java.text.NumberFormat
import java.util.*

class StockEntryManageAdapter(
    private var listEntry: List<StockEntry>,
    private val onDeleteClick: (StockEntry) -> Unit
) : RecyclerView.Adapter<StockEntryManageAdapter.StockEntryViewHolder>() {

    class StockEntryViewHolder(val binding: ItemManageStockEntryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockEntryViewHolder {
        val binding = ItemManageStockEntryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockEntryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StockEntryViewHolder, position: Int) {
        val entry = listEntry[position]
        with(holder.binding) {
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

            tvStockItemName.text = entry.item?.namaBarang ?: "Produk Terhapus"
            tvStockQtyAndPrice.text = "Qty: ${entry.qty} | Harga Beli: ${formatRupiah.format(entry.purchasePrice)}"
            tvStockTotalCost.text = "Total Belanja: ${formatRupiah.format(entry.totalCost)}"

            val rawDate = entry.entryDate
            val formattedDate = try {
                if (rawDate.length >= 10) {
                    val datePart = rawDate.substring(0, 10).split("-")
                    "${datePart[2]}-${datePart[1]}-${datePart[0]}"
                } else {
                    rawDate
                }
            } catch (e: Exception) {
                rawDate
            }

            val supplier = entry.supplierName ?: "-"
            tvStockSupplierAndDate.text = "Supplier: $supplier | Tanggal: $formattedDate"

            btnDeleteStockEntry.setOnClickListener { onDeleteClick(entry) }
        }
    }

    override fun getItemCount(): Int = listEntry.size

    fun setData(newList: List<StockEntry>) {
        listEntry = newList
        notifyDataSetChanged()
    }
}
