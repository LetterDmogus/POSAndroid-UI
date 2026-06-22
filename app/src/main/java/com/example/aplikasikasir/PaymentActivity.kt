package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikasir.data.CartManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Customer
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
    private var rawSubtotal = 0.0
    private var discountAmount = 0.0
    private var subtotal = 0.0 // Net subtotal
    private var taxAmount = 0.0
    private var totalTagihan = 0.0
    private var customerList = listOf<Customer>()
    private var selectedCustomerId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rawSubtotal = CartManager.getOriginalSubtotal()
        discountAmount = CartManager.getTotalDiscount()
        subtotal = CartManager.getTotalPrice()
        taxAmount = Math.round(subtotal * 0.11).toDouble()
        totalTagihan = subtotal + taxAmount
        displayCurrency()
        populateItemsPreview()

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        val token = "Bearer ${sharedPref.getString("token", "")}"
        loadCustomers(token)

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

    private fun loadCustomers(token: String) {
        RetrofitClient.instance.getCustomers(token).enqueue(object : Callback<ApiResponse<List<Customer>>> {
            override fun onResponse(call: Call<ApiResponse<List<Customer>>>, response: Response<ApiResponse<List<Customer>>>) {
                if (response.isSuccessful) {
                    customerList = response.body()?.data ?: emptyList()
                    setupCustomerSpinner()
                } else {
                    Toast.makeText(this@PaymentActivity, "Gagal mengambil data pembeli", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<Customer>>>, t: Throwable) {
                Toast.makeText(this@PaymentActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupCustomerSpinner() {
        val names = mutableListOf<String>()
        names.add("Umum (Non-Member)")
        for (customer in customerList) {
            names.add(customer.name + if (customer.phone.isNullOrEmpty()) "" else " (${customer.phone})")
        }

        val adapter = android.widget.ArrayAdapter(this, android.R.layout.simple_spinner_item, names)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCustomer.adapter = adapter

        binding.spinnerCustomer.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                selectedCustomerId = if (position == 0) {
                    null
                } else {
                    customerList[position - 1].id
                }
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                selectedCustomerId = null
            }
        }
    }

    private fun populateItemsPreview() {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.containerPaymentItems.removeAllViews()
        val inflater = LayoutInflater.from(this)

        for (cartItem in CartManager.getItems()) {
            val itemView = inflater.inflate(android.R.layout.simple_list_item_2, null)
            val text1 = itemView.findViewById<android.widget.TextView>(android.R.id.text1)
            val text2 = itemView.findViewById<android.widget.TextView>(android.R.id.text2)

            text1.text = cartItem.barang.namaBarang
            text1.textSize = 14f
            text1.setTextColor(resources.getColor(R.color.text_primary))

            val detailsText = StringBuilder()
            detailsText.append("${cartItem.qty} x ${formatRupiah.format(cartItem.barang.hargaJual)}")
            
            // Tandai promo Buy 1 Get 1
            val promo = cartItem.barang.discount
            if (promo != null && promo.isActive) {
                if (promo.type == "bogo") {
                    detailsText.append(" (Promo: BUY 1 GET 1)")
                } else if (promo.type == "percentage") {
                    detailsText.append(" (Promo: ${(promo.value ?: 0.0).toInt()}% OFF)")
                }
            }

            if (cartItem.discountAmount > 0) {
                detailsText.append("\nDiskon: -${formatRupiah.format(cartItem.discountAmount)}")
            }
            detailsText.append(" = ${formatRupiah.format(cartItem.subtotal)}")

            text2.text = detailsText.toString()
            text2.textSize = 12f
            text2.setTextColor(resources.getColor(R.color.text_secondary))

            binding.containerPaymentItems.addView(itemView)
        }
    }

    private fun displayCurrency() {
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.tvSubtotalPrice.text = formatRupiah.format(rawSubtotal)
        binding.tvDiscountPrice.text = "- " + formatRupiah.format(discountAmount)
        binding.tvTaxPrice.text = formatRupiah.format(taxAmount)
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
            customerId = selectedCustomerId,
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