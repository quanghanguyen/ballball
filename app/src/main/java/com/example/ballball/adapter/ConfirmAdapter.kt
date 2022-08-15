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
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class ConfirmAdapter @Inject constructor(private var confirmList: ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<ConfirmAdapter.MyViewHolder>() {

    private lateinit var listerner: OnItemClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<CreateMatchModel>) {
        confirmList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding,
        private val listerner: OnItemClickListerner,
    ) : RecyclerView.ViewHolder(matchItemsBinding.root) {
        fun bind(list : CreateMatchModel) {
            with(matchItemsBinding) {
                teamName.text = list.clientTeamName
                date.text = list.date
                time.text = list.time
                location.text = list.location
                peopleNumber.text = list.teamPeopleNumber
                address.text = list.locationAddress
                Glide.with(teamImage).load(list.clientImageUrl).centerCrop().into(teamImage)
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
        holder.bind(confirmList[position])
    }

    override fun getItemCount(): Int {
        return confirmList.size
    }
}