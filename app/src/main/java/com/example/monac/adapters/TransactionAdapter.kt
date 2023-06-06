package com.example.monac.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monac.data.card.Card
import com.example.monac.data.category.TransactionCategory
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.databinding.ItemTransactionBinding
import com.example.monac.util.PaymentType
import com.example.monac.util.TransactionType

class TransactionAdapter(
    val context: Context,
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactionList: ArrayList<PaymentTransaction> = arrayListOf()
    private var categoryList: ArrayList<TransactionCategory> = arrayListOf()
    private var card = Card()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactionList[position]
        holder.bind(item)
    }

    fun updateList(list: ArrayList<PaymentTransaction>, catList: ArrayList<TransactionCategory>, card: Card) {
        this.transactionList = list
        this.categoryList = catList
        this.card = card
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        transactionList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    inner class TransactionViewHolder(val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: PaymentTransaction) {
            var category = TransactionCategory()
            categoryList.forEach { if (it.id == transaction.typeID) category = it }

            binding.tvName.text = category.name
            category.color?.let { binding.cvAvatar.setCardBackgroundColor(it) }
            if (category.type == PaymentType.CATEGORY) {
                binding.ivAvatar.visibility = View.GONE
            } else {
                binding.ivAvatar.visibility = View.VISIBLE
            }

            binding.tvDate.text = transaction.date
            binding.tvTime.text = transaction.time

            if (transaction.type == TransactionType.EARNINGS) {
                binding.tvValue.text = buildString {
                    append("+")
                    append(transaction.value.toString())
                }
            } else {
                binding.tvValue.text = buildString {
                    append("-")
                    append(transaction.value.toString())
                }
            }
            binding.tvValue.append(card.marker)
        }
    }
}