package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.Customer
import com.example.aplikasikasir.databinding.ItemManageCustomerBinding

class CustomerManageAdapter(
    private var listCustomer: List<Customer>,
    private val onEditClick: (Customer) -> Unit,
    private val onDeleteClick: (Customer) -> Unit
) : RecyclerView.Adapter<CustomerManageAdapter.CustomerViewHolder>() {

    class CustomerViewHolder(val binding: ItemManageCustomerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val binding = ItemManageCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {
        val customer = listCustomer[position]
        with(holder.binding) {
            tvManageCustomerName.text = customer.name
            
            if (customer.phone.isNullOrEmpty()) {
                tvManageCustomerPhone.text = "No. Telepon: -"
            } else {
                tvManageCustomerPhone.text = "No. Telepon: ${customer.phone}"
            }

            if (customer.address.isNullOrEmpty()) {
                tvManageCustomerAddress.visibility = View.GONE
            } else {
                tvManageCustomerAddress.visibility = View.VISIBLE
                tvManageCustomerAddress.text = "Alamat: ${customer.address}"
            }

            root.setOnClickListener { onEditClick(customer) }
            btnDeleteCustomer.setOnClickListener { onDeleteClick(customer) }
        }
    }

    override fun getItemCount(): Int = listCustomer.size

    fun setData(newList: List<Customer>) {
        listCustomer = newList
        notifyDataSetChanged()
    }
}
