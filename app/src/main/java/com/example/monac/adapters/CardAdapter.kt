package com.example.monac.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.monac.R
import com.example.monac.data.card.Card
import com.example.monac.data.transaction.PaymentTransaction
import com.example.monac.data.user.User
import com.example.monac.databinding.ItemCardAddBinding
import com.example.monac.databinding.ItemCardBinding
import com.example.monac.util.PaymentInstruments
import com.example.monac.util.TransactionType
import com.example.monac.util.UserType
import com.example.monac.util.getCurrentUser

class CardAdapter(
    private val context: Context,
    private val activity: FragmentActivity,
    val onItemClicked: (Int, Card) -> Unit,
    val onLongItemClicked: (Int, Card) -> Boolean,
    val onItemAddClicked: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var cardList: ArrayList<Card> = arrayListOf()
    private var totalMinus: Double = 0.0
    private var currentValue: Double = 0.0
    private var showValue: Boolean = true

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

    fun updateList(list: ArrayList<Card>, showValue: Boolean) {
        if (list.isEmpty() || list.last().id != -1L) list.add(Card(id = -1L))
        this.cardList = list
        this.showValue = showValue
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return cardList.size
    }

    fun setUpVaue(transactions: ArrayList<PaymentTransaction>, pos: Int) {
        currentValue = 0.0
        this.totalMinus = getTotalMinus(transactions)
        transactions.forEach {
            if (it.type == TransactionType.EXPENSES) {
                currentValue -= it.value
            } else {
                currentValue += it.value
            }
        }
        notifyItemChanged(pos)
    }

    private fun getTotalMinus(transactions: java.util.ArrayList<PaymentTransaction>): Double {
        var minus = 0.0
        transactions.forEach {
            if (it.type == TransactionType.EXPENSES) {
                minus += it.value
            }
        }
        return minus
    }

    inner class CardViewHolder(val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(card: Card) {
            binding.tvName.text = card.name
            // with transactions
            if (showValue) {
                val user = getCurrentUser(activity)
                val limit = getLimit(user, card.marker)

                binding.tvValue.text = buildString {
                    append(currentValue)
                    append(card.marker)
                }


                if (user.type == UserType.CHILD && limit != -1.0) {
                    binding.tvLimit.text = buildString {
                        append(totalMinus)
                        append("/")
                        append(limit)
                        append(card.marker)
                    }

                    binding.tvLimit.visibility = View.VISIBLE
                    if (limit < totalMinus) binding.tvLimit.setTextColor(
                        ColorStateList.valueOf(
                            context.getColor(R.color.red_300)
                        )
                    )
                    else binding.tvLimit.setTextColor(ColorStateList.valueOf(context.getColor(R.color.green_300)))
                } else {
                    binding.tvLimit.visibility = View.GONE
                }
            } else binding.tvValue.text = ""
            binding.tvNumber.text = buildString {
                append("**** **** **** ")
                append(card.number)
            }

            binding.card.setCardBackgroundColor(card.color)

            if (card.paymentInstrument == PaymentInstruments.MASTERCARD)
                binding.iv.setImageResource(R.drawable.master_card)
            else binding.iv.setImageResource(R.drawable.mir_pay)

            binding.card.setOnClickListener { onItemClicked.invoke(absoluteAdapterPosition, card) }
            binding.card.setOnLongClickListener {
                onLongItemClicked.invoke(
                    absoluteAdapterPosition,
                    card
                )
            }
        }
    }

    private fun getLimit(user: User, marker: String): Double {
        return when (marker) {
            "$" -> user.limitUSD ?: -1.0
            "₽" -> user.limitRUB ?: -1.0
            else -> user.limitEUR ?: -1.0
        }
    }

    inner class CardAddViewHolder(val binding: ItemCardAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.clAdd.setOnClickListener { onItemAddClicked.invoke() }
        }
    }
}