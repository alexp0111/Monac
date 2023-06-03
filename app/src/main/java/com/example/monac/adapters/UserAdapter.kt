package com.example.monac.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monac.data.user.User
import com.example.monac.databinding.ItemUserAddBinding
import com.example.monac.databinding.ItemUserBinding

class UserAdapter(
    val context: Context,
    val onItemClicked: (Int, User) -> Unit,
    val onItemAddClicked: () -> Unit
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var userList: ArrayList<User> = arrayListOf()

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

    fun updateList(list: ArrayList<User>) {
        if (list.isEmpty() || list.first().id != -1L) list.add(0, User(id = -1L))
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
        fun bind(user: User) {
            binding.tvName.text = user.name
            binding.tvName.isSelected = true
            user.imageUri.toUri().let {
                Log.d("USERADAPTER", it.toString())
                Glide.with(context)
                    .load(it)
                    .into(binding.ivAvatar)
            }

            binding.ivAvatar.setOnClickListener {
                onItemClicked.invoke(absoluteAdapterPosition, user)
            }
        }
    }

    inner class TransactionUserAddViewHolder(val binding: ItemUserAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(){
            binding.ivAvatar.setOnClickListener {
                onItemAddClicked.invoke()
            }
        }
    }
}