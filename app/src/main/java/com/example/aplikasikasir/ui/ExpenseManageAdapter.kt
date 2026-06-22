package com.example.aplikasikasir.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.Transaction
import com.example.aplikasikasir.databinding.ItemManageExpenseBinding
import java.text.NumberFormat
import java.util.*

class ExpenseManageAdapter(
    private var listExpenses: List<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<ExpenseManageAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(val binding: ItemManageExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val binding = ItemManageExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExpenseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = listExpenses[position]
        with(holder.binding) {
            val localeID = Locale("in", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            
            tvExpenseAmount.text = formatRupiah.format(expense.amount)
            tvExpenseDescription.text = expense.description
            tvExpenseDate.text = expense.transactionDate

            if (expense.type == "expense") {
                viewTypeIndicator.setBackgroundColor(Color.parseColor("#FF5252")) // Red
                tvExpenseAmount.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                viewTypeIndicator.setBackgroundColor(Color.parseColor("#4CAF50")) // Green
                tvExpenseAmount.setTextColor(Color.parseColor("#388E3C"))
            }

            btnDeleteExpense.setOnClickListener { onDeleteClick(expense) }
        }
    }

    override fun getItemCount(): Int = listExpenses.size

    fun setData(newList: List<Transaction>) {
        listExpenses = newList
        notifyDataSetChanged()
    }
}
