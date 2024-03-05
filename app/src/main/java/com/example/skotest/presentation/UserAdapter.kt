package com.example.skotest.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.skotest.R
import com.example.skotest.databinding.ItemLayoutBinding
import com.example.skotest.domain.User

class UserAdapter(private val context: Context, private var userList: List<User>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.item_layout, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    fun updateUsers(users: List<User>) {
        userList = users
        notifyDataSetChanged()
    }

    inner class UserViewHolder(private val binding: ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.user = user
            binding.context = context
            binding.executePendingBindings()

            Glide.with(context)
                .load(user.avatar)
                .placeholder(R.drawable.ic_launcher_background)
                .into(binding.avatarImageView)
        }
    }
}
