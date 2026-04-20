package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Category
import com.example.aplikasikasir.databinding.ActivityManageCategoryBinding
import com.example.aplikasikasir.ui.CategoryManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCategoryBinding
    private lateinit var adapter: CategoryManageAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchCategories()

        binding.btnBackManageCat.setOnClickListener { finish() }
        binding.fabAddCategory.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = CategoryManageAdapter(emptyList(), 
            onEditClick = { category -> showEditDialog(category) },
            onDeleteClick = { category -> showDeleteConfirmation(category) }
        )
        binding.rvManageCategories.layoutManager = LinearLayoutManager(this)
        binding.rvManageCategories.adapter = adapter
    }

    private fun fetchCategories() {
        RetrofitClient.instance.getCategories(token).enqueue(object : Callback<ApiResponse<List<Category>>> {
            override fun onResponse(call: Call<ApiResponse<List<Category>>>, response: Response<ApiResponse<List<Category>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Category>>>, t: Throwable) {
                Toast.makeText(this@ManageCategoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddDialog() {
        val input = EditText(this)
        input.hint = "Contoh: Makanan Berat"
        
        AlertDialog.Builder(this)
            .setTitle("Tambah Kategori Baru")
            .setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) createCategory(name)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createCategory(name: String) {
        RetrofitClient.instance.createCategory(token, mapOf("nama_kategori" to name))
            .enqueue(object : Callback<ApiResponse<Category>> {
                override fun onResponse(call: Call<ApiResponse<Category>>, response: Response<ApiResponse<Category>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageCategoryActivity, "Kategori ditambahkan", Toast.LENGTH_SHORT).show()
                        fetchCategories()
                    } else {
                        Toast.makeText(this@ManageCategoryActivity, "Gagal menambah kategori", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Category>>, t: Throwable) {}
            })
    }

    private fun showEditDialog(category: Category) {
        val input = EditText(this)
        input.setText(category.namaKategori)
        
        AlertDialog.Builder(this)
            .setTitle("Edit Kategori")
            .setView(input)
            .setPositiveButton("Update") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) updateCategory(category.id, newName)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateCategory(id: Int, name: String) {
        RetrofitClient.instance.updateCategory(token, id, mapOf("nama_kategori" to name))
            .enqueue(object : Callback<ApiResponse<Category>> {
                override fun onResponse(call: Call<ApiResponse<Category>>, response: Response<ApiResponse<Category>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageCategoryActivity, "Kategori diperbarui", Toast.LENGTH_SHORT).show()
                        fetchCategories()
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Category>>, t: Throwable) {}
            })
    }

    private fun showDeleteConfirmation(category: Category) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori?")
            .setMessage("Yakin ingin menghapus '${category.namaKategori}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteCategory(category.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCategory(id: Int) {
        RetrofitClient.instance.deleteCategory(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageCategoryActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchCategories()
                } else {
                    Toast.makeText(this@ManageCategoryActivity, "Gagal menghapus (Kategori mungkin tidak kosong)", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {}
        })
    }
}