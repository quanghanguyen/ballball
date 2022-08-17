package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.`interface`.OnNewContactClickListerner
import com.example.ballball.databinding.ItemsAddContactBinding
import com.example.ballball.model.NewContactModel
import com.example.ballball.model.UsersModel
import javax.inject.Inject

class NewContactAdapter @Inject constructor(private var newContactList : ArrayList<NewContactModel>)
    : RecyclerView.Adapter<NewContactAdapter.MyViewHolder>() {

    private lateinit var listerner: OnNewContactClickListerner

    fun setOnNewContactClickListerner(listerner: OnNewContactClickListerner) {
        this.listerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<NewContactModel>) {
        newContactList = list
        notifyDataSetChanged()
    }

    class MyViewHolder(
        private val itemsAddContactBinding: ItemsAddContactBinding,
        private val listerner: OnNewContactClickListerner
    ) : RecyclerView.ViewHolder(itemsAddContactBinding.root){
        fun bind(list : NewContactModel) {
            with(itemsAddContactBinding) {
                userName.text = list.name
                userPhone.text = list.phoneNumber

                call.setOnClickListener {
                    listerner.onNewContactClick(list)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsAddContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(newContactList[position])
    }

    override fun getItemCount(): Int {
        return newContactList.size
    }
}