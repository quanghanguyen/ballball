package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.`interface`.OnChatClickListerner
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.databinding.ItemsContactBinding
import com.example.ballball.model.UsersModel
import javax.inject.Inject

class ContactAdapter @Inject constructor(private var contactList : ArrayList<UsersModel>)
    : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    private lateinit var listerner: OnIconClickListerner
    private lateinit var chatListerner : OnChatClickListerner

    fun setOnIconClickListerner(listerner: OnIconClickListerner) {
        this.listerner = listerner
    }

    fun setOnChatClickListerner(listerner: OnChatClickListerner) {
        this.chatListerner = listerner
    }

    class MyViewHolder(
        private val itemsContactBinding : ItemsContactBinding,
        private val listerner: OnIconClickListerner,
        private val chatListerner: OnChatClickListerner
        )
        : RecyclerView.ViewHolder(itemsContactBinding.root) {
            fun bind(list : UsersModel) {
                with(itemsContactBinding) {
                    teamName.text = list.teamName
                    userName.text = list.userName
                    Glide.with(userAvatar).load(list.avatarUrl).centerCrop().into(userAvatar)

                    call.setOnClickListener {
                        listerner.onIconClick(list)
                    }
                    chat.setOnClickListener {
                        chatListerner.onChatClick(list)
                    }
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<UsersModel>) {
        contactList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFilterList(filterList : ArrayList<UsersModel>) {
        contactList = filterList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner, chatListerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}