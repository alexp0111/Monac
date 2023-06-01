package com.example.monac.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.data.Card
import com.example.monac.databinding.ItemCardAddBinding
import com.example.monac.databinding.ItemCardBinding

class CardAdapter(
    private val cardList: ArrayList<Card>,
    val onItemClicked: (Int, Card) -> Unit,
    val onItemAddClicked: () -> Unit
    ) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        cardList.add(Card(name = "for adding"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == cardList.size - 1) {
            val view =
                ItemCardAddBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return CardAddViewHolder(view)
        }
        val view = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == cardList.size - 1) {
            (holder as CardAddViewHolder).bind()
        } else {
            val item = cardList[position]
            (holder as CardViewHolder).bind(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    inner class CardViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            binding.tvName.text = card.name
            binding.tvValue.text = buildString {
                append(card.marker)
                append(card.value.toString())
            }
            binding.tvNumber.text = card.number

            binding.card.setCardBackgroundColor(card.color)

            // TODO: payment instrument

            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, card) }
        }
    }

    inner class CardAddViewHolder(val binding: ItemCardAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind(){
                binding.clAdd.setOnClickListener { onItemAddClicked.invoke() }
            }
    }
}