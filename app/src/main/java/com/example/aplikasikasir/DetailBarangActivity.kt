package com.example.aplikasikasir

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.databinding.ActivityDetailBarangBinding
import com.squareup.picasso.Picasso
import java.text.NumberFormat
import java.util.*

class DetailBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBarangBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil data barang dari Intent
        val barang = intent.getParcelableExtra<Barang>("EXTRA_BARANG")

        if (barang != null) {
            displayDetail(barang)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun displayDetail(barang: Barang) {
        with(binding) {
            tvDetailNama.text = barang.namaBarang
            tvDetailSku.text = "SKU: ${barang.sku}"
            tvDetailCategory.text = barang.category?.namaKategori ?: "Tanpa Kategori"
            tvDetailStok.text = barang.stok.toString()
            tvDetailSatuan.text = barang.satuan
            tvDetailDeskripsi.text = barang.deskripsi ?: "Tidak ada deskripsi untuk produk ini."

            // Load Gambar dengan Picasso
            if (!barang.fotoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(barang.fotoUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(ivDetailFoto)
            }

            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            tvDetailHarga.text = formatRupiah.format(barang.hargaJual)
        }
    }
}