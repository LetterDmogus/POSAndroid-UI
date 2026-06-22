package com.example.aplikasikasir

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
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

        binding.btnSavePng.setOnClickListener {
            saveInvoiceAsPng()
        }

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

            val subtotalVal = data.summary.subtotal ?: (data.summary.total - (data.summary.pajak ?: 0))
            val discountVal = data.summary.diskon ?: 0
            val taxVal = data.summary.pajak ?: 0

            tvInvSubtotal.text = formatRupiah.format(subtotalVal)
            tvInvDiskon.text = "- " + formatRupiah.format(discountVal)
            tvInvTax.text = formatRupiah.format(taxVal)
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
                
                val itemText = StringBuilder()
                itemText.append("${item.qty} x ${formatRupiah.format(item.harga)}")
                if (item.diskon > 0) {
                    itemText.append(" (Diskon: -${formatRupiah.format(item.diskon)})")
                }
                itemText.append(" = ${formatRupiah.format(item.subtotal)}")
                
                text2.text = itemText.toString()
                text2.textSize = 12f // Perbaikan di sini (12f artinya 12sp)
                
                containerItems.addView(itemView)
            }
        }
    }

    private fun saveInvoiceAsPng() {
        val view = binding.cardInvoiceReceipt
        
        // Measure and layout the view in case it hasn't been drawn yet
        if (view.width == 0 || view.height == 0) {
            Toast.makeText(this, "Struk belum siap diekspor", Toast.LENGTH_SHORT).show()
            return
        }

        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)

        val filename = "Struk_${System.currentTimeMillis()}.png"
        var fos: java.io.OutputStream? = null

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val resolver = contentResolver
                val contentValues = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/POS_Receipts")
                }
                val imageUri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES).toString()
                val dir = java.io.File(imagesDir, "POS_Receipts")
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val file = java.io.File(dir, filename)
                fos = java.io.FileOutputStream(file)
            }

            if (fos != null) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
                fos.flush()
                fos.close()
                Toast.makeText(this, "Struk berhasil diexport sebagai PNG ke Galeri!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Gagal mengekspor struk", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}