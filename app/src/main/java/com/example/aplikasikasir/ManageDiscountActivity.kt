package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.data.model.Discount
import com.example.aplikasikasir.databinding.ActivityManageDiscountBinding
import com.example.aplikasikasir.databinding.DialogAddDiscountBinding
import com.example.aplikasikasir.ui.DiscountManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageDiscountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageDiscountBinding
    private lateinit var adapter: DiscountManageAdapter
    private var token: String = ""
    private var barangList = listOf<Barang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageDiscountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchDiscounts()
        fetchBarangList()

        binding.btnBackManageDisc.setOnClickListener { finish() }
        binding.fabAddDiscount.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = DiscountManageAdapter(emptyList(),
            onDeleteClick = { discount -> showDeleteConfirmation(discount) }
        )
        binding.rvManageDiscounts.layoutManager = LinearLayoutManager(this)
        binding.rvManageDiscounts.adapter = adapter
    }

    private fun fetchDiscounts() {
        RetrofitClient.instance.getDiscounts(token).enqueue(object : Callback<ApiResponse<List<Discount>>> {
            override fun onResponse(call: Call<ApiResponse<List<Discount>>>, response: Response<ApiResponse<List<Discount>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Discount>>>, t: Throwable) {
                Toast.makeText(this@ManageDiscountActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
        val dialogBinding = DialogAddDiscountBinding.inflate(LayoutInflater.from(this))
        
        // Setup Spinner Barang
        val barangNames = barangList.map { it.namaBarang }
        val barangAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, barangNames)
        barangAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerBarang.adapter = barangAdapter

        // Setup Spinner Type
        val types = listOf("Persentase (%)", "BOGO (Buy 1 Get 1)")
        val typeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogBinding.spinnerType.adapter = typeAdapter

        dialogBinding.spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 1) { // BOGO
                    dialogBinding.etValue.visibility = View.GONE
                } else { // Percentage
                    dialogBinding.etValue.visibility = View.VISIBLE
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        AlertDialog.Builder(this)
            .setTitle("Tambah Diskon Baru")
            .setView(dialogBinding.root)
            .setPositiveButton("Simpan") { _, _ ->
                val name = dialogBinding.etName.text.toString().trim()
                val selectedBarangIndex = dialogBinding.spinnerBarang.selectedItemPosition
                val selectedTypeIndex = dialogBinding.spinnerType.selectedItemPosition

                if (name.isEmpty() || selectedBarangIndex == AdapterView.INVALID_POSITION) {
                    Toast.makeText(this, "Data tidak lengkap!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val barangId = barangList[selectedBarangIndex].id
                val type = if (selectedTypeIndex == 0) "percentage" else "bogo"
                val valueStr = dialogBinding.etValue.text.toString().trim()
                val value = if (type == "percentage") {
                    if (valueStr.isEmpty()) 0.0 else valueStr.toDouble()
                } else {
                    0.0
                }

                createDiscount(name, type, value, barangId)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createDiscount(name: String, type: String, value: Double, itemId: Int) {
        val body = mapOf(
            "name" to name,
            "type" to type,
            "value" to value.toString(),
            "item_id" to itemId.toString(),
            "is_active" to "1"
        )

        RetrofitClient.instance.createDiscount(token, body).enqueue(object : Callback<ApiResponse<Discount>> {
            override fun onResponse(call: Call<ApiResponse<Discount>>, response: Response<ApiResponse<Discount>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageDiscountActivity, "Diskon ditambahkan", Toast.LENGTH_SHORT).show()
                    fetchDiscounts()
                } else {
                    Toast.makeText(this@ManageDiscountActivity, "Gagal menambahkan diskon", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Discount>>, t: Throwable) {
                Toast.makeText(this@ManageDiscountActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmation(discount: Discount) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Diskon?")
            .setMessage("Yakin ingin menghapus promo '${discount.name}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteDiscount(discount.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteDiscount(id: Int) {
        RetrofitClient.instance.deleteDiscount(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageDiscountActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchDiscounts()
                } else {
                    Toast.makeText(this@ManageDiscountActivity, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {}
        })
    }
}
