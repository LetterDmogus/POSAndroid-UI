package com.example.aplikasikasir.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.aplikasikasir.ManageBarangActivity
import com.example.aplikasikasir.ManageCategoryActivity
import com.example.aplikasikasir.ManageCustomerActivity
import com.example.aplikasikasir.ManageExpenseActivity
import com.example.aplikasikasir.databinding.FragmentMasterDataBinding

class MasterDataFragment : Fragment() {

    private var _binding: FragmentMasterDataBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMasterDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardManageBarang.setOnClickListener {
            startActivity(Intent(requireContext(), ManageBarangActivity::class.java))
        }

        binding.cardManageCategory.setOnClickListener {
            startActivity(Intent(requireContext(), ManageCategoryActivity::class.java))
        }

        binding.cardManageCustomer.setOnClickListener {
            startActivity(Intent(requireContext(), ManageCustomerActivity::class.java))
        }

        binding.cardReport.setOnClickListener {
            startActivity(Intent(requireContext(), com.example.aplikasikasir.ReportActivity::class.java))
        }

        binding.cardManageExpense.setOnClickListener {
            startActivity(Intent(requireContext(), ManageExpenseActivity::class.java))
        }

        binding.cardManageDiscount.setOnClickListener {
            startActivity(Intent(requireContext(), com.example.aplikasikasir.ManageDiscountActivity::class.java))
        }

        binding.cardManageStockEntry.setOnClickListener {
            startActivity(Intent(requireContext(), com.example.aplikasikasir.ManageStockEntryActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}