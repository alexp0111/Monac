package com.example.monac.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.R
import com.example.monac.data.category.TransactionCategory
import com.example.monac.databinding.ItemUserAddBinding
import com.example.monac.databinding.ItemUserBinding

class TransactionUserAdapter(
    val context: Context,
    val onItemClicked: (Int, TransactionCategory) -> Unit,
    val onLongItemClicked: (Int, TransactionCategory) -> Boolean,
    val onItemAddClicked: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userList: ArrayList<TransactionCategory> = arrayListOf()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        userList.add(0, TransactionCategory(name = "for adding"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view =
                ItemUserAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return TransactionUserAddViewHolder(view)
        }
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionUserViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == 0) {
            // addition segment
            (holder as TransactionUserAddViewHolder).bind()
        } else {
            val item = userList[position]
            (holder as TransactionUserViewHolder).bind(item)
        }
    }

    fun updateList(list: ArrayList<TransactionCategory>) {
        list.add(0, TransactionCategory(name = "for adding"))
        this.userList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class TransactionUserViewHolder(val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transactionUser: TransactionCategory) {
            binding.tvName.text = transactionUser.name
            binding.tvName.isSelected = true
            if (transactionUser.color != null && transactionUser.color != -1) {
                binding.cvAvatar.setCardBackgroundColor(transactionUser.color)
            } else {
                binding.cvAvatar.setCardBackgroundColor(ColorStateList.valueOf(context.getColor(R.color.white)))
            }

            binding.ivAvatar.setOnClickListener {
                onItemClicked.invoke(absoluteAdapterPosition, transactionUser)
            }
            binding.ivAvatar.setOnLongClickListener {
                onLongItemClicked.invoke(absoluteAdapterPosition, transactionUser)
            }
        }
    }

    inner class TransactionUserAddViewHolder(val binding: ItemUserAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.ivAvatar.setOnClickListener {
                onItemAddClicked.invoke()
            }
        }
    }
}