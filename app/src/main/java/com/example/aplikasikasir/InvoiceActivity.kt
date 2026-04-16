package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.InvoiceResponse
import com.example.aplikasikasir.databinding.ActivityInvoiceBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class InvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val invoiceNo = intent.getStringExtra("EXTRA_INVOICE") ?: ""
        fetchInvoice(invoiceNo)

        binding.btnDoneInvoice.setOnClickListener {
            finish()
        }
    }

    private fun fetchInvoice(invoiceNo: String) {
        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val token = "Bearer ${sharedPref.getString("token", "")}"

        RetrofitClient.instance.getInvoice(token, invoiceNo).enqueue(object : Callback<InvoiceResponse> {
            override fun onResponse(call: Call<InvoiceResponse>, response: Response<InvoiceResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    displayInvoice(response.body()!!)
                } else {
                    Toast.makeText(this@InvoiceActivity, "Gagal mengambil data struk", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<InvoiceResponse>, t: Throwable) {
                Toast.makeText(this@InvoiceActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayInvoice(response: InvoiceResponse) {
        val data = response.data ?: return
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        with(binding) {
            tvInvNo.text = "INV: ${data.header.invoiceNo}"
            tvInvDate.text = "Tanggal: ${data.header.tanggal}"
            tvInvCashier.text = "Kasir: ${data.header.kasir}"
            tvInvMethod.text = "Metode Pembayaran: ${data.header.metode}"

            tvInvTotal.text = formatRupiah.format(data.summary.total)
            tvInvBayar.text = formatRupiah.format(data.summary.bayar)
            tvInvKembali.text = formatRupiah.format(data.summary.kembali)

            // Populate Items
            containerItems.removeAllViews()
            val inflater = LayoutInflater.from(this@InvoiceActivity)
            
            for (item in data.items) {
                val itemView = inflater.inflate(android.R.layout.simple_list_item_2, null)
                val text1 = itemView.findViewById<TextView>(android.R.id.text1)
                val text2 = itemView.findViewById<TextView>(android.R.id.text2)
                
                text1.text = item.nama
                text1.textSize = 14f // Perbaikan di sini (14f artinya 14sp)
                text2.text = "${item.qty} x ${formatRupiah.format(item.harga)} = ${formatRupiah.format(item.subtotal)}"
                text2.textSize = 12f // Perbaikan di sini (12f artinya 12sp)
                
                containerItems.addView(itemView)
            }
        }
    }
}