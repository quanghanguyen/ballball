package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.databinding.ItemsContactBinding
import com.example.ballball.model.UsersModel
import javax.inject.Inject

class ContactAdapter @Inject constructor(private var contactList : ArrayList<UsersModel>)
    : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {

    private lateinit var listerner: OnIconClickListerner

    fun setOnIconClickListerner(listerner: OnIconClickListerner) {
        this.listerner = listerner
    }

    class MyViewHolder(
        private val itemsContactBinding : ItemsContactBinding,
        private val listerner: OnIconClickListerner
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
                }
            }
        }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<UsersModel>) {
        contactList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(contactList[position])
    }

    override fun getItemCount(): Int {
        return contactList.size
    }
}