package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.data.model.StockEntry
import com.example.aplikasikasir.databinding.ActivityManageStockEntryBinding
import com.example.aplikasikasir.databinding.DialogAddStockEntryBinding
import com.example.aplikasikasir.ui.StockEntryManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ManageStockEntryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStockEntryBinding
    private lateinit var adapter: StockEntryManageAdapter
    private var token: String = ""
    private var barangList = listOf<Barang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStockEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchStockEntries()
        fetchBarangList()

        binding.btnBackManageStock.setOnClickListener { finish() }
        binding.fabAddStockEntry.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = StockEntryManageAdapter(emptyList(),
            onDeleteClick = { entry -> showDeleteConfirmation(entry) }
        )
        binding.rvManageStockEntries.layoutManager = LinearLayoutManager(this)
        binding.rvManageStockEntries.adapter = adapter
    }

    private fun fetchStockEntries() {
        RetrofitClient.instance.getStockEntries(token).enqueue(object : Callback<ApiResponse<List<StockEntry>>> {
            override fun onResponse(call: Call<ApiResponse<List<StockEntry>>>, response: Response<ApiResponse<List<StockEntry>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<StockEntry>>>, t: Throwable) {
                Toast.makeText(this@ManageStockEntryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchBarangList() {
        RetrofitClient.instance.getBarangs(token).enqueue(object : Callback<ApiResponse<List<Barang>>> {
            override fun onResponse(call: Call<ApiResponse<List<Barang>>>, response: Response<ApiResponse<List<Barang>>>) {
                if (response.isSuccessful) {
                    barangList = response.body()?.data ?: emptyList()
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {}
        })
    }

    private fun showAddDialog() {
        val dialogBinding = DialogAddStockEntryBinding.inflate(LayoutInflater.from(this))
        
        val barangNames = barangList.map { "${it.namaBarang} (Stok: ${it.stok})" }
        val barangAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, barangNames)
        barangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerBarang.adapter = barangAdapter

        AlertDialog.Builder(this)
            .setTitle("Tambah Barang Masuk")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val qtyStr = dialogBinding.etQty.text.toString().trim()
                val priceStr = dialogBinding.etPurchasePrice.text.toString().trim()
                val supplier = dialogBinding.etSupplier.text.toString().trim()
                val selectedIndex = dialogBinding.spinnerBarang.selectedItemPosition

                if (qtyStr.isEmpty() || priceStr.isEmpty() || selectedIndex == android.widget.AdapterView.INVALID_POSITION) {
                    Toast.makeText(this, "Data tidak lengkap!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val itemId = barangList[selectedIndex].id
                val qty = qtyStr.toInt()
                val totalCostInput = priceStr.toDouble()
                val unitPrice = if (qty > 0) totalCostInput / qty else 0.0
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

                createStockEntry(itemId, qty, unitPrice, supplier, date)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createStockEntry(itemId: Int, qty: Int, price: Double, supplier: String, date: String) {
        val body = mapOf(
            "item_id" to itemId.toString(),
            "qty" to qty.toString(),
            "purchase_price" to String.format(Locale.US, "%.2f", price),
            "supplier_name" to supplier,
            "entry_date" to date
        )

        RetrofitClient.instance.createStockEntry(token, body).enqueue(object : Callback<ApiResponse<StockEntry>> {
            override fun onResponse(call: Call<ApiResponse<StockEntry>>, response: Response<ApiResponse<StockEntry>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageStockEntryActivity, "Stok masuk dicatat", Toast.LENGTH_SHORT).show()
                    fetchStockEntries()
                } else {
                    Toast.makeText(this@ManageStockEntryActivity, "Gagal mencatat stok masuk", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<StockEntry>>, t: Throwable) {
                Toast.makeText(this@ManageStockEntryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmation(entry: StockEntry) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Penerimaan Barang?")
            .setMessage("Yakin ingin menghapus catatan stok ini? Stok barang akan dikurangi kembali.")
            .setPositiveButton("Hapus") { _, _ -> deleteStockEntry(entry.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteStockEntry(id: Int) {
        RetrofitClient.instance.deleteStockEntry(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageStockEntryActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchStockEntries()
                } else {
                    Toast.makeText(this@ManageStockEntryActivity, "Gagal membatalkan stok masuk", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {}
        })
    }
}
