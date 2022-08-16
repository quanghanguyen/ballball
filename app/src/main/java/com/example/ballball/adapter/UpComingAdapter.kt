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

class UpComingAdapter @Inject constructor(private var upComingList: ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<UpComingAdapter.MyViewHolder>() {

    private lateinit var listerner: OnItemClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<CreateMatchModel>) {
        upComingList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding,
        private val listerner: OnItemClickListerner
    ) : RecyclerView.ViewHolder(matchItemsBinding.root) {
        private val userUID = FirebaseAuth.getInstance().currentUser?.uid
        fun bind(list : CreateMatchModel) {
            with(matchItemsBinding) {
                date.text = list.date
                time.text = list.time
                location.text = list.location
                peopleNumber.text = list.teamPeopleNumber
                address.text = list.locationAddress
                Glide.with(teamImage).load(list.clientImageUrl).centerCrop().into(teamImage)
                newCreate.visibility = View.GONE
                waitConfirm.visibility = View.GONE
                if (userUID == list.userUID) {
                    Glide.with(teamImage).load(list.clientImageUrl).centerCrop().into(teamImage)
                    teamName.text = list.clientTeamName
                } else {
                    Glide.with(teamImage).load(list.teamImageUrl).centerCrop().into(teamImage)
                    teamName.text = list.teamName
                }

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
        holder.bind(upComingList[position])
    }

    override fun getItemCount(): Int {
        return upComingList.size
    }
}