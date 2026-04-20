package com.example.aplikasikasir

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.databinding.ActivityManageBarangBinding
import com.example.aplikasikasir.ui.BarangManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageBarangActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageBarangBinding
    private lateinit var adapter: BarangManageAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchBarang()

        binding.btnBackManageBarang.setOnClickListener { finish() }
        binding.fabAddBarang.setOnClickListener {
            val intent = Intent(this, FormBarangActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        adapter = BarangManageAdapter(emptyList(),
            onEditClick = { barang ->
                val intent = Intent(this, FormBarangActivity::class.java)
                intent.putExtra("EXTRA_BARANG", barang)
                startActivity(intent)
            },
            onDeleteClick = { barang -> showDeleteConfirmation(barang) }
        )
        binding.rvManageBarang.layoutManager = LinearLayoutManager(this)
        binding.rvManageBarang.adapter = adapter
    }

    private fun fetchBarang() {
        RetrofitClient.instance.getBarangs(token).enqueue(object : Callback<ApiResponse<List<Barang>>> {
            override fun onResponse(call: Call<ApiResponse<List<Barang>>>, response: Response<ApiResponse<List<Barang>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {
                Toast.makeText(this@ManageBarangActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDeleteConfirmation(barang: Barang) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Barang?")
            .setMessage("Yakin ingin menghapus '${barang.namaBarang}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteBarang(barang.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteBarang(id: Int) {
        RetrofitClient.instance.deleteBarang(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageBarangActivity, "Barang dihapus", Toast.LENGTH_SHORT).show()
                    fetchBarang()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {}
        })
    }

    override fun onResume() {
        super.onResume()
        fetchBarang()
    }
}