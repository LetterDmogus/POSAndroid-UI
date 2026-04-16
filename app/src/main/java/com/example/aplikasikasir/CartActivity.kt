package com.example.aplikasikasir

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.CartManager
import com.example.aplikasikasir.databinding.ActivityCartBinding
import com.example.aplikasikasir.ui.CartAdapter
import java.text.NumberFormat
import java.util.*

class CartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCartBinding
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotal()

        binding.btnBackCart.setOnClickListener {
            finish()
        }

        binding.btnProceedPayment.setOnClickListener {
            if (CartManager.getItems().isEmpty()) {
                Toast.makeText(this, "Keranjang masih kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Navigasi ke PaymentActivity
            startActivity(Intent(this, PaymentActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        adapter = CartAdapter(CartManager.getItems()) {
            updateTotal()
        }
        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter
    }

    private fun updateTotal() {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.tvTotalBayar.text = formatRupiah.format(CartManager.getTotalPrice())
        
        // Tutup halaman jika keranjang kosong setelah penghapusan
        if (CartManager.getItems().isEmpty()) {
            Toast.makeText(this, "Keranjang kosong", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}