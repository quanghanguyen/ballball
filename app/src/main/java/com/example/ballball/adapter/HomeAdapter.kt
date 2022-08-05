package com.example.ballball.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ballball.databinding.MatchItemsBinding
import com.example.ballball.model.CreateMatchModel
import javax.inject.Inject

class HomeAdapter @Inject constructor(private var requestList : ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    fun addNewData(list: ArrayList<CreateMatchModel>) {
        requestList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding
            ) : RecyclerView.ViewHolder(matchItemsBinding.root) {
                fun bind(list : CreateMatchModel) {
                    with(matchItemsBinding) {
                        teamName.text = list.teamName
                        date.text = list.date
                        time.text = list.time
                        location.text = list.location
                        peopleNumber.text = list.teamPeopleNumber
                        address.text = "FJJ2+M47, Vỹ Dạ, Thành phố Huế, Thừa Thiên Huế, Việt Nam "
                    }
                }
            }git bran

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = MatchItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int {
        return requestList.size
    }
}