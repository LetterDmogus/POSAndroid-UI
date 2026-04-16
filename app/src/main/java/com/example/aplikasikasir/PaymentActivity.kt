package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.CartManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.OrderItemRequest
import com.example.aplikasikasir.data.model.OrderRequest
import com.example.aplikasikasir.data.model.OrderResponse
import com.example.aplikasikasir.databinding.ActivityPaymentBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class PaymentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentBinding
    private var totalTagihan = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        totalTagihan = CartManager.getTotalPrice()
        displayCurrency()

        binding.btnBackPayment.setOnClickListener { finish() }

        binding.btnUangPas.setOnClickListener {
            binding.etBayar.setText(totalTagihan.toInt().toString())
        }

        binding.etBayar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateChange()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnFinishOrder.setOnClickListener {
            processCheckout()
        }
    }

    private fun displayCurrency() {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.tvTotalTagihan.text = formatRupiah.format(totalTagihan)
    }

    private fun calculateChange() {
        val bayarStr = binding.etBayar.text.toString()
        val bayar = if (bayarStr.isEmpty()) 0.0 else bayarStr.toDouble()
        val kembali = bayar - totalTagihan

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.tvKembalian.text = if (kembali >= 0) formatRupiah.format(kembali) else "Rp 0"
    }

    private fun processCheckout() {
        val bayarStr = binding.etBayar.text.toString()
        if (bayarStr.isEmpty() || bayarStr.toDouble() < totalTagihan) {
            Toast.makeText(this, "Uang bayar kurang!", Toast.LENGTH_SHORT).show()
            return
        }

        val metode = if (binding.rbTunai.isChecked) "Tunai" else "QRIS"
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val token = "Bearer ${sharedPref.getString("token", "")}"

        // Siapkan daftar item
        val items = CartManager.getItems().map {
            OrderItemRequest(it.barang.id, it.qty)
        }

        val request = OrderRequest(
            bayar = bayarStr.toDouble(),
            metodePembayaran = metode,
            catatan = null,
            items = items
        )

        binding.btnFinishOrder.isEnabled = false
        binding.btnFinishOrder.text = "Sedang Memproses..."

        RetrofitClient.instance.createOrder(token, request).enqueue(object : Callback<OrderResponse> {
            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                binding.btnFinishOrder.isEnabled = true
                binding.btnFinishOrder.text = "SELESAIKAN TRANSAKSI"

                if (response.isSuccessful && response.body()?.success == true) {
                    val invoiceNo = response.body()?.data?.nomorInvoice
                    
                    CartManager.clearCart() // Kosongkan keranjang
                    
                    // Navigasi ke Halaman Sukses
                    val intent = Intent(this@PaymentActivity, SuccessActivity::class.java)
                    intent.putExtra("EXTRA_INVOICE", invoiceNo)
                    startActivity(intent)
                    finish() 
                } else {
                    Toast.makeText(this@PaymentActivity, "Gagal: ${response.body()?.message}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                binding.btnFinishOrder.isEnabled = true
                binding.btnFinishOrder.text = "SELESAIKAN TRANSAKSI"
                Toast.makeText(this@PaymentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}