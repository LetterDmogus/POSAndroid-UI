package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.Discount
import com.example.aplikasikasir.databinding.ItemManageDiscountBinding

class DiscountManageAdapter(
    private var listDiscount: List<Discount>,
    private val onDeleteClick: (Discount) -> Unit
) : RecyclerView.Adapter<DiscountManageAdapter.DiscountViewHolder>() {

    class DiscountViewHolder(val binding: ItemManageDiscountBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountViewHolder {
        val binding = ItemManageDiscountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscountViewHolder, position: Int) {
        val discount = listDiscount[position]
        with(holder.binding) {
            tvManageDiscountName.text = discount.name
            
            val detailsText = StringBuilder()
            detailsText.append("Tipe: ")
            if (discount.type == "bogo") {
                detailsText.append("Buy 1 Get 1")
            } else if (discount.type == "percentage") {
                detailsText.append("Persentase (${(discount.value ?: 0.0).toInt()}%)")
            } else {
                detailsText.append(discount.type)
            }

            detailsText.append(" | Status: ${if (discount.isActive) "Aktif" else "Non-Aktif"}")
            tvManageDiscountDesc.text = detailsText.toString()

            btnDeleteDiscount.setOnClickListener { onDeleteClick(discount) }
        }
    }

    override fun getItemCount(): Int = listDiscount.size

    fun setData(newList: List<Discount>) {
        listDiscount = newList
        notifyDataSetChanged()
    }
}
