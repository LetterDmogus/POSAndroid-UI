package com.example.aplikasikasir.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.DashboardResponse
import com.example.aplikasikasir.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"
        val name = sharedPref.getString("user_name", "User")
        val role = sharedPref.getString("user_role", "kasir")

        binding.tvWelcome.text = "Selamat Datang, $name!"
        binding.tvRole.text = "Anda login sebagai ${role?.replaceFirstChar { it.uppercase() }}"

        if (role == "admin") {
            binding.cardHomeOmzet.setOnClickListener {
                startActivity(android.content.Intent(requireContext(), com.example.aplikasikasir.ReportActivity::class.java))
            }
            binding.cardHomeTransactions.setOnClickListener {
                startActivity(android.content.Intent(requireContext(), com.example.aplikasikasir.ReportActivity::class.java))
            }
        }

        fetchDashboardData()
    }

    private fun fetchDashboardData() {
        RetrofitClient.instance.getDashboard(token).enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(call: Call<DashboardResponse>, response: Response<DashboardResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val data = response.body()!!.data
                    updateUI(data)
                }
            }
            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Gagal memuat dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateUI(data: com.example.aplikasikasir.data.model.DashboardData) {
        if (!isAdded) return

        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        binding.tvHomeOmzet.text = formatRupiah.format(data.today.omzet)
        binding.tvHomeOrders.text = "${data.today.transactions} Transaksi"

        setupLineChart(data.salesTrend)
        setupBarChart(data.topProducts)
        setupPieChart(data.paymentMethods)

        binding.containerLowStock.removeAllViews()
        if (data.lowStock.isEmpty()) {
            val tv = TextView(requireContext())
            tv.text = "Semua stok terpantau aman."
            binding.containerLowStock.addView(tv)
        } else {
            data.lowStock.forEach { barang ->
                val tv = TextView(requireContext())
                tv.text = "⚠️ ${barang.namaBarang} (Stok: ${barang.stok})"
                tv.setTextColor(Color.RED)
                tv.setPadding(0, 8, 0, 8)
                binding.containerLowStock.addView(tv)
            }
        }
    }

    private fun setupLineChart(trend: List<com.example.aplikasikasir.data.model.SalesTrend>) {
        val entries = ArrayList<Entry>()
        trend.forEachIndexed { index, item ->
            entries.add(Entry(index.toFloat(), item.total.toFloat()))
        }

        val dataSet = LineDataSet(entries, "Omzet Harian")
        dataSet.color = Color.parseColor("#0288D1")
        dataSet.setCircleColor(Color.parseColor("#0288D1"))
        dataSet.lineWidth = 2f
        dataSet.circleRadius = 4f
        dataSet.setDrawValues(false)
        dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER

        val lineData = LineData(dataSet)
        binding.lineChart.data = lineData
        
        val xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < trend.size) {
                    trend[index].label
                } else {
                    ""
                }
            }
        }

        binding.lineChart.description.isEnabled = false
        binding.lineChart.animateX(1000)
        binding.lineChart.invalidate()
    }

    private fun setupBarChart(products: List<com.example.aplikasikasir.data.model.TopProduct>) {
        val entries = ArrayList<BarEntry>()
        products.forEachIndexed { index, item ->
            entries.add(BarEntry(index.toFloat(), item.totalQty.toFloat()))
        }

        val dataSet = BarDataSet(entries, "Qty Terjual")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextSize = 10f
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        binding.barChartTopProducts.data = barData

        val xAxis = binding.barChartTopProducts.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f
        xAxis.setDrawGridLines(false)
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                return if (index >= 0 && index < products.size) {
                    products[index].namaBarang
                } else {
                    ""
                }
            }
        }

        binding.barChartTopProducts.extraBottomOffset = 25f
        binding.barChartTopProducts.axisLeft.granularity = 1f
        binding.barChartTopProducts.axisRight.isEnabled = false
        binding.barChartTopProducts.description.isEnabled = false
        binding.barChartTopProducts.animateY(1000)
        binding.barChartTopProducts.invalidate()
    }

    private fun setupPieChart(methods: List<com.example.aplikasikasir.data.model.PaymentMethodStat>) {
        val entries = ArrayList<PieEntry>()
        methods.forEach { item ->
            entries.add(PieEntry(item.count.toFloat(), item.metodePembayaran))
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#0288D1"),
            Color.parseColor("#81C784"),
            Color.parseColor("#FFD54F")
        )
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.WHITE

        val pieData = PieData(dataSet)
        binding.pieChartPaymentMethods.data = pieData
        binding.pieChartPaymentMethods.description.isEnabled = false
        binding.pieChartPaymentMethods.centerText = "Metode"
        binding.pieChartPaymentMethods.animateXY(1000, 1000)
        binding.pieChartPaymentMethods.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}