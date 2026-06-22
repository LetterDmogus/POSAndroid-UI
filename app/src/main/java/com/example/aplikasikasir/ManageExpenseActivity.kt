package com.example.aplikasikasir

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Transaction
import com.example.aplikasikasir.data.model.TransactionResponse
import com.example.aplikasikasir.databinding.ActivityManageExpenseBinding
import com.example.aplikasikasir.ui.ExpenseManageAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ManageExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageExpenseBinding
    private lateinit var adapter: ExpenseManageAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchTransactions()

        binding.btnBackManageExpense.setOnClickListener { finish() }
        binding.fabAddExpense.setOnClickListener { showAddDialog() }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseManageAdapter(emptyList()) { transaction ->
            showDeleteConfirmation(transaction)
        }
        binding.rvManageExpenses.layoutManager = LinearLayoutManager(this)
        binding.rvManageExpenses.adapter = adapter
    }

    private fun fetchTransactions() {
        RetrofitClient.instance.getTransactions(token).enqueue(object : Callback<TransactionResponse> {
            override fun onResponse(call: Call<TransactionResponse>, response: Response<TransactionResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val resBody = response.body()!!
                    val list = resBody.data
                    adapter.setData(list)

                    val localeID = Locale("in", "ID")
                    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                    
                    val totalIncome = resBody.summary?.totalIncome ?: 0.0
                    val totalExpense = resBody.summary?.totalExpense ?: 0.0

                    binding.tvTotalIncome.text = formatRupiah.format(totalIncome)
                    binding.tvTotalExpense.text = formatRupiah.format(totalExpense)
                }
            }

            override fun onFailure(call: Call<TransactionResponse>, t: Throwable) {
                Toast.makeText(this@ManageExpenseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAddDialog() {
        val context = this
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        // Type selection Spinner
        val spinnerType = Spinner(context)
        val types = listOf("Pengeluaran (Expense)", "Pemasukan Lain (Income)")
        val spinnerAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, types)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = spinnerAdapter
        
        val etAmount = EditText(context).apply {
            hint = "Jumlah Nominal (Rupiah)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        val etDescription = EditText(context).apply { hint = "Deskripsi / Keterangan" }
        val etReference = EditText(context).apply { hint = "Referensi (Opsional)" }

        layout.addView(TextView(context).apply { text = "Jenis Catatan" })
        layout.addView(spinnerType)
        layout.addView(View(context).apply { layoutParams = LinearLayout.LayoutParams(1, 20) })
        layout.addView(etAmount)
        layout.addView(etDescription)
        layout.addView(etReference)

        AlertDialog.Builder(this)
            .setTitle("Catat Pengeluaran / Pemasukan")
            .setView(layout)
            .setPositiveButton("Simpan") { _, _ ->
                val amountStr = etAmount.text.toString().trim()
                val description = etDescription.text.toString().trim()
                val reference = etReference.text.toString().trim()

                if (amountStr.isEmpty() || description.isEmpty()) {
                    Toast.makeText(this, "Jumlah nominal dan deskripsi wajib diisi!", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val type = if (spinnerType.selectedItemPosition == 0) "expense" else "income"
                val transactionDateStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())

                val body = mutableMapOf<String, String>()
                body["type"] = type
                body["amount"] = amountStr
                body["description"] = description
                body["transaction_date"] = transactionDateStr
                if (reference.isNotEmpty()) body["reference"] = reference

                createTransaction(body)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun createTransaction(body: Map<String, String>) {
        RetrofitClient.instance.createTransaction(token, body)
            .enqueue(object : Callback<ApiResponse<Transaction>> {
                override fun onResponse(call: Call<ApiResponse<Transaction>>, response: Response<ApiResponse<Transaction>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ManageExpenseActivity, "Transaksi berhasil dicatat", Toast.LENGTH_SHORT).show()
                        fetchTransactions()
                    } else {
                        Toast.makeText(this@ManageExpenseActivity, "Gagal mencatat transaksi", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Transaction>>, t: Throwable) {
                    Toast.makeText(this@ManageExpenseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDeleteConfirmation(transaction: Transaction) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Catatan?")
            .setMessage("Yakin ingin menghapus catatan '${transaction.description}'?")
            .setPositiveButton("Hapus") { _, _ -> deleteTransaction(transaction.id) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteTransaction(id: Int) {
        RetrofitClient.instance.deleteTransaction(token, id).enqueue(object : Callback<ApiResponse<Unit>> {
            override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@ManageExpenseActivity, "Catatan berhasil dihapus", Toast.LENGTH_SHORT).show()
                    fetchTransactions()
                } else {
                    Toast.makeText(this@ManageExpenseActivity, "Gagal menghapus catatan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                Toast.makeText(this@ManageExpenseActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
