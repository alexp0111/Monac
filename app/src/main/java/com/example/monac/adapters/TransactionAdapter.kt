package com.example.monac.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.databinding.ItemTransactionBinding

class TransactionAdapter(
    val context: Context,
) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactionList: ArrayList<PaymentTransaction> = arrayListOf()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view =
            ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = transactionList[position]
        holder.bind(item)
    }

    fun updateList(list: ArrayList<PaymentTransaction>) {
        this.transactionList = list
        notifyDataSetChanged()
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
            binding.tvName.text = "Подписка на яндекс музыку"
            // set uri
            binding.tvDate.text = transaction.date
            // binding.tvTime.text = transaction.date.hour.toString() + ":" + transaction.date.minute.toString()
            binding.tvValue.text = transaction.value.toString()
        }
    }
}