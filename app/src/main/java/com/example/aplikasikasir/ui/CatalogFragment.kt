package com.example.aplikasikasir.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.aplikasikasir.CartActivity
import com.example.aplikasikasir.data.CartManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.ApiResponse
import com.example.aplikasikasir.data.model.Barang
import com.example.aplikasikasir.databinding.FragmentCatalogBinding
import com.google.zxing.integration.android.IntentIntegrator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.NumberFormat
import java.util.*

class CatalogFragment : Fragment() {

    private var _binding: FragmentCatalogBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BarangAdapter
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCatalogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchCatalog()
        setupSearch()
        updateCartUI()

        binding.cardCartInfo.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }

        // Tombol Scan QR
        binding.fabScanQr.setOnClickListener {
            startQrScanner()
        }
    }

    private fun startQrScanner() {
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan QR Code Barang")
        integrator.setBeepEnabled(true)
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    // Menangani hasil scan
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(requireContext(), "Scan dibatalkan", Toast.LENGTH_SHORT).show()
            } else {
                val sku = result.contents
                fetchBarangBySku(sku)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun fetchBarangBySku(sku: String) {
        Toast.makeText(requireContext(), "Mencari SKU: $sku", Toast.LENGTH_SHORT).show()
        
        RetrofitClient.instance.scanBarang(token, sku).enqueue(object : Callback<ApiResponse<Barang>> {
            override fun onResponse(call: Call<ApiResponse<Barang>>, response: Response<ApiResponse<Barang>>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val barang = response.body()!!.data
                    if (barang != null) {
                        CartManager.addItem(barang)
                        updateCartUI()
                        Toast.makeText(requireContext(), "${barang.namaBarang} ditambahkan!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Produk tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ApiResponse<Barang>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupRecyclerView() {
        adapter = BarangAdapter(emptyList()) { barang ->
            CartManager.addItem(barang)
            updateCartUI()
            Toast.makeText(requireContext(), "${barang.namaBarang} ditambah!", Toast.LENGTH_SHORT).show()
        }
        binding.rvCatalog.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvCatalog.adapter = adapter
    }

    private fun updateCartUI() {
        if (!isAdded) return
        val count = CartManager.getItemCount()
        if (count > 0) {
            binding.cardCartInfo.visibility = View.VISIBLE
            binding.tvCartCount.text = "$count Item"
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            binding.tvCartTotal.text = formatRupiah.format(CartManager.getTotalPrice())
        } else {
            binding.cardCartInfo.visibility = View.GONE
        }
    }

    private fun fetchCatalog(search: String? = null) {
        RetrofitClient.instance.getBarangs(token, search).enqueue(object : Callback<ApiResponse<List<Barang>>> {
            override fun onResponse(call: Call<ApiResponse<List<Barang>>>, response: Response<ApiResponse<List<Barang>>>) {
                if (response.isSuccessful) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                }
            }
            override fun onFailure(call: Call<ApiResponse<List<Barang>>>, t: Throwable) {}
        })
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fetchCatalog(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        updateCartUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}