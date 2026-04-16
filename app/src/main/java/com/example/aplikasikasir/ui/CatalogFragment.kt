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
        setupSearch()

        binding.cardCartInfo.setOnClickListener {
            startActivity(Intent(requireContext(), CartActivity::class.java))
        }
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
        val count = CartManager.getItemCount()
        if (count > 0) {
            binding.cardCartInfo.visibility = View.VISIBLE
            binding.tvCartCount.text = "$count Item"
            val formatRupiah = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
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
        // Setiap kali fragment aktif, ambil data terbaru dan update keranjang
        fetchCatalog(binding.etSearch.text.toString())
        updateCartUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}