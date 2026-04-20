package com.example.aplikasikasir.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aplikasikasir.data.model.Category
import com.example.aplikasikasir.databinding.ItemManageCategoryBinding

class CategoryManageAdapter(
    private var listCategory: List<Category>,
    private val onEditClick: (Category) -> Unit,
    private val onDeleteClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryManageAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(val binding: ItemManageCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemManageCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = listCategory[position]
        with(holder.binding) {
            tvManageCategoryName.text = category.namaKategori
            tvManageCategoryCount.text = "${category.barangsCount ?: 0} Produk"

            root.setOnClickListener { onEditClick(category) }
            btnDeleteCategory.setOnClickListener { onDeleteClick(category) }
        }
    }

    override fun getItemCount(): Int = listCategory.size

    fun setData(newList: List<Category>) {
        listCategory = newList
        notifyDataSetChanged()
    }
}