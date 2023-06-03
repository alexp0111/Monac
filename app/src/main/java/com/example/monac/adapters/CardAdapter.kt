package com.example.monac.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.R
import com.example.monac.data.card.Card
import com.example.monac.databinding.ItemCardAddBinding
import com.example.monac.databinding.ItemCardBinding
import com.example.monac.util.PaymentInstruments

class CardAdapter(
    private val context: Context,
    val onItemClicked: (Int, Card) -> Unit,
    val onItemAddClicked: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cardList: ArrayList<Card> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Log.d("ADAPTER1", cardList.size.toString())
        if (viewType == cardList.size - 1) {
            val view =
                ItemCardAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CardAddViewHolder(view)
        }
        val view = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Log.d("ADAPTER2", cardList[position].toString())
        Log.d("ADAPTER3", cardList.size.toString())
        if (holder.itemViewType == cardList.size - 1) {
            (holder as CardAddViewHolder).bind()
        } else {
            val item = cardList[position]
            (holder as CardViewHolder).bind(item)
        }
    }

    fun updateList(list: ArrayList<Card>) {
        if (list.isEmpty() || list.last().id != -1L) list.add(Card(id = -1L))
        this.cardList = list
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    inner class CardViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            binding.tvName.text = card.name
            binding.tvValue.text = buildString {
                append(card.marker)
                append(card.value.toString())
            }
            binding.tvNumber.text = buildString {
                append("**** **** **** ")
                append(card.number)
            }

            binding.card.setCardBackgroundColor(card.color)

            if (card.paymentInstrument == PaymentInstruments.MASTERCARD)
                binding.iv.setImageResource(R.drawable.master_card)
            else binding.iv.setImageResource(R.drawable.mir_pay)

            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, card) }
        }
    }

    inner class CardAddViewHolder(val binding: ItemCardAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.clAdd.setOnClickListener { onItemAddClicked.invoke() }
        }
    }
}