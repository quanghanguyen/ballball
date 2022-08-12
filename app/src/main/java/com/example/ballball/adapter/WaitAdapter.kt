package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.databinding.MatchItemsBinding
import com.example.ballball.model.CreateMatchModel
import javax.inject.Inject

class WaitAdapter @Inject constructor(private var waitList: ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<WaitAdapter.MyViewHolder>() {
    private lateinit var listerner: OnItemClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<CreateMatchModel>) {
        waitList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding,
        private val listerner: OnItemClickListerner
    ) : RecyclerView.ViewHolder(matchItemsBinding.root) {
        fun bind(list : CreateMatchModel) {
            with(matchItemsBinding) {
                teamName.text = list.teamName
                date.text = list.date
                time.text = list.time
                location.text = list.location
                peopleNumber.text = list.teamPeopleNumber
                address.text = list.locationAddress
                Glide.with(teamImage).load(list.teamImageUrl).centerCrop().into(teamImage)
                waitConfirm.visibility = View.GONE
                newCreate.visibility = View.GONE

                items.setOnClickListener {
                    listerner.onItemClick(list)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = MatchItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(waitList[position])
    }

    override fun getItemCount(): Int {
        return waitList.size
    }
}