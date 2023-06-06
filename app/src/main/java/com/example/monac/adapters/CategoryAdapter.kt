package com.example.monac.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.R
import com.example.monac.data.category.TransactionCategory
import com.example.monac.databinding.ItemSearchCategoryResultBinding

class CategoryAdapter(
    val context: Context,
    val onItemClicked: (Int, TransactionCategory) -> Unit,
) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categoryList: ArrayList<TransactionCategory> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            ItemSearchCategoryResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val item = categoryList[position]
        holder.bind(item)
    }

    fun updateList(list: ArrayList<TransactionCategory>) {
        this.categoryList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    inner class CategoryViewHolder(val binding: ItemSearchCategoryResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: TransactionCategory) {
            binding.tvName.text = category.name
            if (category.color != null && category.color != -1) {
                binding.cv.setCardBackgroundColor(category.color)
            } else {
                binding.cv.setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.category_back)))
            }
            binding.cv.setOnClickListener {
                onItemClicked.invoke(
                    absoluteAdapterPosition,
                    category
                )
            }
        }
    }
}