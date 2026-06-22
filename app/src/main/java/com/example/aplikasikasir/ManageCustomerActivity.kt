package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Customer
import com.example.aplikasikasir.databinding.ActivityManageCustomerBinding
import com.example.aplikasikasir.ui.CustomerManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCustomerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCustomerBinding
    private lateinit var adapter: CustomerManageAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCustomerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchCustomers()

        binding.btnBackManageCust.setOnClickListener { finish() }
        binding.fabAddCustomer.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = CustomerManageAdapter(emptyList(),
            onEditClick = { customer -> showEditDialog(customer) },
            onDeleteClick = { customer -> showDeleteConfirmation(customer) }
        )
        binding.rvManageCustomers.layoutManager = LinearLayoutManager(this)
        binding.rvManageCustomers.adapter = adapter
    }

    private fun fetchCustomers() {
        RetrofitClient.instance.getCustomers(token).enqueue(object : Callback<ApiResponse<List<Customer>>> {
            override fun onResponse(call: Call<ApiResponse<List<Customer>>>, response: Response<ApiResponse<List<Customer>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Customer>>>, t: Throwable) {
                Toast.makeText(this@ManageCustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddDialog() {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etName = EditText(context).apply { hint = "Nama Lengkap" }
        val etPhone = EditText(context).apply { hint = "No. Telepon" }
        val etEmail = EditText(context).apply { hint = "Email (Opsional)" }
        val etAddress = EditText(context).apply { hint = "Alamat (Opsional)" }

        layout.addView(etName)
        layout.addView(etPhone)
        layout.addView(etEmail)
        layout.addView(etAddress)

        AlertDialog.Builder(this)
            .setTitle("Tambah Pembeli Baru")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val address = etAddress.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val body = mutableMapOf<String, String>()
                body["name"] = name
                if (phone.isNotEmpty()) body["phone"] = phone
                if (email.isNotEmpty()) body["email"] = email
                if (address.isNotEmpty()) body["address"] = address

                createCustomer(body)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createCustomer(body: Map<String, String>) {
        RetrofitClient.instance.createCustomer(token, body)
            .enqueue(object : Callback<ApiResponse<Customer>> {
                override fun onResponse(call: Call<ApiResponse<Customer>>, response: Response<ApiResponse<Customer>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageCustomerActivity, "Pembeli berhasil ditambahkan", Toast.LENGTH_SHORT).show()
                        fetchCustomers()
                    } else {
                        Toast.makeText(this@ManageCustomerActivity, "Gagal menambah pembeli", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Customer>>, t: Throwable) {
                    Toast.makeText(this@ManageCustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showEditDialog(customer: Customer) {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etName = EditText(context).apply {
            hint = "Nama Lengkap"
            setText(customer.name)
        }
        val etPhone = EditText(context).apply {
            hint = "No. Telepon"
            setText(customer.phone ?: "")
        }
        val etEmail = EditText(context).apply {
            hint = "Email (Opsional)"
            setText(customer.email ?: "")
        }
        val etAddress = EditText(context).apply {
            hint = "Alamat (Opsional)"
            setText(customer.address ?: "")
        }

        layout.addView(etName)
        layout.addView(etPhone)
        layout.addView(etEmail)
        layout.addView(etAddress)

        AlertDialog.Builder(this)
            .setTitle("Edit Pembeli")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val email = etEmail.text.toString().trim()
                val address = etAddress.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Nama tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val body = mutableMapOf<String, String>()
                body["name"] = name
                body["phone"] = phone
                body["email"] = email
                body["address"] = address

                updateCustomer(customer.id, body)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun updateCustomer(id: Int, body: Map<String, String>) {
        RetrofitClient.instance.updateCustomer(token, id, body)
            .enqueue(object : Callback<ApiResponse<Customer>> {
                override fun onResponse(call: Call<ApiResponse<Customer>>, response: Response<ApiResponse<Customer>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageCustomerActivity, "Pembeli berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        fetchCustomers()
                    } else {
                        Toast.makeText(this@ManageCustomerActivity, "Gagal memperbarui pembeli", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ApiResponse<Customer>>, t: Throwable) {
                    Toast.makeText(this@ManageCustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDeleteConfirmation(customer: Customer) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pembeli?")
            .setMessage("Yakin ingin menghapus '${customer.name}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteCustomer(customer.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteCustomer(id: Int) {
        RetrofitClient.instance.deleteCustomer(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageCustomerActivity, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchCustomers()
                } else {
                    Toast.makeText(this@ManageCustomerActivity, "Gagal menghapus pembeli", Toast.LENGTH_LONG).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Toast.makeText(this@ManageCustomerActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
