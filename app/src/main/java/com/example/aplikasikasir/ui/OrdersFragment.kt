package com.example.aplikasikasir.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aplikasikasir.data.api.RetrofitClient
import com.example.aplikasikasir.data.model.OrderHistoryResponse
import com.example.aplikasikasir.databinding.FragmentOrdersBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrdersFragment : Fragment() {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: OrderAdapter
    private var token: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = requireActivity().getSharedPreferences("pos_pref", Context.MODE_PRIVATE)
        token = "Bearer ${sharedPref.getString("token", "")}"

        setupRecyclerView()
        fetchOrders()
    }

    private fun setupRecyclerView() {
        adapter = OrderAdapter(emptyList())
        binding.rvOrders.layoutManager = LinearLayoutManager(requireContext())
        binding.rvOrders.adapter = adapter
    }

    private fun fetchOrders() {
        RetrofitClient.instance.getOrders(token).enqueue(object : Callback<OrderHistoryResponse> {
            override fun onResponse(call: Call<OrderHistoryResponse>, response: Response<OrderHistoryResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.data ?: emptyList()
                    adapter.setData(list)
                } else {
                    Toast.makeText(requireContext(), "Gagal mengambil riwayat", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<OrderHistoryResponse>, t: Throwable) {
                if (isAdded) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}