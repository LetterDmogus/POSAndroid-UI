package com.example.aplikasikasir

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.OrderHistoryResponse
import com.example.aplikasikasir.databinding.ActivityReportBinding
import com.example.aplikasikasir.ui.OrderAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ReportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportBinding
    private lateinit var adapter: OrderAdapter
    private var token: String = ""

    private var calendarStart = Calendar.getInstance()
    private var calendarEnd = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    // Spinners state
    private val months = listOf(
        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
    )
    private var yearsList = listOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        setupSpinners()
        setupListeners()
        updateFilterDatesUI()

        // Set default daily date indicator to today
        binding.tvDailyDate.text = dateFormat.format(Calendar.getInstance().time)

        // Default query: Harian (today)
        applyDailyFilter()

        binding.btnBackReport.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(emptyList())
        binding.rvReportTransactions.layoutManager = LinearLayoutManager(this)
        binding.rvReportTransactions.adapter = adapter
    }

    private fun setupSpinners() {
        // Months Adapter
        val monthAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, months)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMonth.adapter = monthAdapter
        binding.spinnerMonth.setSelection(Calendar.getInstance().get(Calendar.MONTH))

        // Years Adapter (2020 up to current year)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        yearsList = (2020..currentYear).map { it.toString() }
        
        val yearAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, yearsList)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        
        binding.spinnerMonthYear.adapter = yearAdapter
        binding.spinnerMonthYear.setSelection(yearsList.indexOf(currentYear.toString()))

        binding.spinnerYear.adapter = yearAdapter
        binding.spinnerYear.setSelection(yearsList.indexOf(currentYear.toString()))
    }

    private fun setupListeners() {
        binding.rgReportPeriod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbDaily -> {
                    binding.layoutDailyFilter.visibility = View.VISIBLE
                    binding.layoutMonthlyFilter.visibility = View.GONE
                    binding.layoutYearlyFilter.visibility = View.GONE
                    binding.layoutCustomDates.visibility = View.GONE
                    applyDailyFilter()
                }
                R.id.rbMonthly -> {
                    binding.layoutDailyFilter.visibility = View.GONE
                    binding.layoutMonthlyFilter.visibility = View.VISIBLE
                    binding.layoutYearlyFilter.visibility = View.GONE
                    binding.layoutCustomDates.visibility = View.GONE
                    applyMonthlyFilter()
                }
                R.id.rbYearly -> {
                    binding.layoutDailyFilter.visibility = View.GONE
                    binding.layoutMonthlyFilter.visibility = View.GONE
                    binding.layoutYearlyFilter.visibility = View.VISIBLE
                    binding.layoutCustomDates.visibility = View.GONE
                    applyYearlyFilter()
                }
                R.id.rbCustom -> {
                    binding.layoutDailyFilter.visibility = View.GONE
                    binding.layoutMonthlyFilter.visibility = View.GONE
                    binding.layoutYearlyFilter.visibility = View.GONE
                    binding.layoutCustomDates.visibility = View.VISIBLE
                    fetchFilteredReport()
                }
            }
        }

        // Daily Date selector
        binding.tvDailyDate.setOnClickListener {
            val dailyCal = Calendar.getInstance()
            try {
                dateFormat.parse(binding.tvDailyDate.text.toString())?.let {
                    dailyCal.time = it
                }
            } catch (e: Exception) {}

            showDatePicker(dailyCal) {
                binding.tvDailyDate.text = dateFormat.format(dailyCal.time)
                applyDailyFilter()
            }
        }

        // Custom Date Range selectors
        binding.tvStartDate.setOnClickListener {
            showDatePicker(calendarStart) {
                updateFilterDatesUI()
                fetchFilteredReport()
            }
        }

        binding.tvEndDate.setOnClickListener {
            showDatePicker(calendarEnd) {
                updateFilterDatesUI()
                fetchFilteredReport()
            }
        }

        // Month Spinner changes
        val spinnerListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (binding.rbMonthly.isChecked) {
                    applyMonthlyFilter()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.spinnerMonth.onItemSelectedListener = spinnerListener
        binding.spinnerMonthYear.onItemSelectedListener = spinnerListener

        // Year Spinner changes
        binding.spinnerYear.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (binding.rbYearly.isChecked) {
                    applyYearlyFilter()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateFilterDatesUI() {
        binding.tvStartDate.text = dateFormat.format(calendarStart.time)
        binding.tvEndDate.text = dateFormat.format(calendarEnd.time)
    }

    private fun showDatePicker(calendar: Calendar, onDateSelected: () -> Unit) {
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun applyDailyFilter() {
        val selectedDateStr = binding.tvDailyDate.text.toString()
        val dailyCal = Calendar.getInstance()
        try {
            dateFormat.parse(selectedDateStr)?.let {
                dailyCal.time = it
            }
        } catch (e: Exception) {}

        calendarStart = dailyCal.clone() as Calendar
        calendarEnd = dailyCal.clone() as Calendar
        fetchFilteredReport()
    }

    private fun applyMonthlyFilter() {
        val selectedMonthIdx = binding.spinnerMonth.selectedItemPosition // 0 to 11
        val selectedYearStr = binding.spinnerMonthYear.selectedItem.toString()
        val yearVal = selectedYearStr.toInt()

        calendarStart = Calendar.getInstance()
        calendarStart.set(Calendar.YEAR, yearVal)
        calendarStart.set(Calendar.MONTH, selectedMonthIdx)
        calendarStart.set(Calendar.DAY_OF_MONTH, 1)

        calendarEnd = Calendar.getInstance()
        calendarEnd.set(Calendar.YEAR, yearVal)
        calendarEnd.set(Calendar.MONTH, selectedMonthIdx)
        calendarEnd.set(Calendar.DAY_OF_MONTH, calendarEnd.getActualMaximum(Calendar.DAY_OF_MONTH))

        fetchFilteredReport()
    }

    private fun applyYearlyFilter() {
        val selectedYearStr = binding.spinnerYear.selectedItem.toString()
        val yearVal = selectedYearStr.toInt()

        calendarStart = Calendar.getInstance()
        calendarStart.set(Calendar.YEAR, yearVal)
        calendarStart.set(Calendar.MONTH, Calendar.JANUARY)
        calendarStart.set(Calendar.DAY_OF_MONTH, 1)

        calendarEnd = Calendar.getInstance()
        calendarEnd.set(Calendar.YEAR, yearVal)
        calendarEnd.set(Calendar.MONTH, Calendar.DECEMBER)
        calendarEnd.set(Calendar.DAY_OF_MONTH, 31)

        fetchFilteredReport()
    }

    private fun fetchFilteredReport() {
        val startDateStr = dateFormat.format(calendarStart.time)
        val endDateStr = dateFormat.format(calendarEnd.time)

        RetrofitClient.instance.getOrders(token, startDateStr, endDateStr)
            .enqueue(object : Callback<OrderHistoryResponse> {
                override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        val body = response.body()!!
                        val totalOmzet = body.totalOmzet ?: 0.0
                        val transactionCount = body.count ?: 0
                        val orderList = body.data

                        // Update statistics UI
                        val localeID = Locale("in", "ID")
                        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
                        binding.tvTotalOmzet.text = formatRupiah.format(totalOmzet)
                        binding.tvTotalTransactions.text = "$transactionCount Transaksi"

                        val avg = if (transactionCount > 0) totalOmzet / transactionCount else 0.0
                        binding.tvAverageTransaction.text = formatRupiah.format(avg)

                        adapter.setData(orderList)
                    } else {
                        Toast.makeText(this@ReportActivity, "Gagal mengambil data laporan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                    Toast.makeText(this@ReportActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
