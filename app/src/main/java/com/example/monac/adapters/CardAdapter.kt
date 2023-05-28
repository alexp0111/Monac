package com.example.monac.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.monac.data.Card
import com.example.monac.databinding.ItemCardBinding

class CardAdapter(private val cardList: ArrayList<Card>, private val viewPager2: ViewPager2) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = cardList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    class CardViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            binding.tvName.text = card.name
            binding.tvValue.text = buildString {
                append(card.marker)
                append(card.value.toString())
            }
            binding.tvNumber.text = card.number

            binding.card.setCardBackgroundColor(card.color)

            // TODO: payment instrument
        }
    }
}