package com.example.aplikasikasir.ui

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.DetailBarangActivity
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.databinding.ItemBarangBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class BarangAdapter(
    private var listBarang: List<Barang>,
    private val onAddClick: (Barang) -> Unit // Lambda untuk tambah barang
) : RecyclerView.Adapter<BarangAdapter.BarangViewHolder>() {

    class BarangViewHolder(val binding: ItemBarangBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarangViewHolder {
        val binding = ItemBarangBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BarangViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BarangViewHolder, position: Int) {
        val barang = listBarang[position]
        with(holder.binding) {
            tvNamaBarang.text = barang.namaBarang
            tvSku.text = "SKU: ${barang.sku}"
            tvStok.text = "Stok: ${barang.stok}"
            
            // Load Gambar dengan Picasso (Lebih ringan & stabil)
            if (!barang.fotoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(barang.fotoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.ic_menu_report_image)
                    .into(ivBarang)
            } else {
                ivBarang.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvHarga.text = formatRupiah.format(barang.hargaJual)

            // Setup Promo/Discount Badge
            if (barang.discount != null && barang.discount.isActive) {
                tvPromoBadge.visibility = android.view.View.VISIBLE
                if (barang.discount.type == "percentage") {
                    tvPromoBadge.text = "${(barang.discount.value ?: 0.0).toInt()}% OFF"
                } else if (barang.discount.type == "bogo") {
                    tvPromoBadge.text = "BUY 1 GET 1"
                } else {
                    tvPromoBadge.text = barang.discount.name
                }
            } else {
                tvPromoBadge.visibility = android.view.View.GONE
            }

            // Click Card (Tambah ke Keranjang)
            root.setOnClickListener {
                onAddClick(barang)
            }

            // Click Icon Info (Buka Detail Produk)
            btnAddQuick.setOnClickListener {
                val intent = Intent(it.context, DetailBarangActivity::class.java)
                intent.putExtra("EXTRA_BARANG", barang)
                it.context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = listBarang.size

    fun setData(newList: List<Barang>) {
        listBarang = newList
        notifyDataSetChanged()
    }
}