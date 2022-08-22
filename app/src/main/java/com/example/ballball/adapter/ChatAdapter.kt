package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.util.GlideSuppliers
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.databinding.ItemsChatBinding
import com.example.ballball.model.UsersModel
import javax.inject.Inject

class ChatAdapter @Inject constructor(private var chatList : ArrayList<UsersModel>)
    : RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {

    private lateinit var listerner: OnIconClickListerner

    fun setOnIconClickListerner(listerner: OnIconClickListerner) {
        this.listerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<UsersModel>) {
        chatList = list
        notifyDataSetChanged()
    }

    class MyViewHolder(
        private val itemsChatBinding: ItemsChatBinding,
        private val listerner: OnIconClickListerner
    ) : RecyclerView.ViewHolder(itemsChatBinding.root){
        fun bind(list : UsersModel) {
            with(itemsChatBinding) {
                teamName.text = list.teamName
                Glide.with(userAvatar).load(list.avatarUrl).centerCrop().into(userAvatar)

                items.setOnClickListener {
                    listerner.onIconClick(list)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}