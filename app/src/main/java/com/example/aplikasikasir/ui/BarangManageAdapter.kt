package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.databinding.ItemManageBarangBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class BarangManageAdapter(
    private var listBarang: List<Barang>,
    private val onEditClick: (Barang) -> Unit,
    private val onDeleteClick: (Barang) -> Unit
) : RecyclerView.Adapter<BarangManageAdapter.BarangViewHolder>() {

    class BarangViewHolder(val binding: ItemManageBarangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemManageBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]
        with(holder.binding) {
            tvManageNama.text = barang.namaBarang
            tvManageSku.text = "SKU: ${barang.sku}"
            tvManageStok.text = "Stok: ${barang.stok} ${barang.satuan}"
            
            // Format tanggal created_at / updated_at
            val rawDate = barang.createdAt ?: "-"
            val formattedDate = if (rawDate.length >= 19) {
                // Konversi dari format Laravel (2026-06-21T02:32:33.000000Z) ke format ramah (21-06-2026 02:32)
                try {
                    val datePart = rawDate.substring(0, 10).split("-")
                    val timePart = rawDate.substring(11, 16)
                    "${datePart[2]}-${datePart[1]}-${datePart[0]} $timePart"
                } catch (e: Exception) {
                    rawDate.replace("T", " ").substring(0, 16)
                }
            } else {
                rawDate
            }
            tvManageDate.text = "Dibuat: $formattedDate"

            if (!barang.fotoUrl.isNullOrEmpty()) {
                Picasso.get().load(barang.fotoUrl).into(ivManageBarang)
            } else {
                ivManageBarang.setImageResource(android.R.drawable.ic_menu_gallery)
            }

            btnEditBarang.setOnClickListener { onEditClick(barang) }
            btnDeleteBarang.setOnClickListener { onDeleteClick(barang) }
        }
    }

    override fun getItemCount(): Int = listBarang.size

    fun setData(newList: List<Barang>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}