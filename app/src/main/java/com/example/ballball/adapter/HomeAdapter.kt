package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.`interface`.HighLightOnClickListerner
import com.example.ballball.`interface`.NotHighLightOnClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.databinding.MatchItemsBinding
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel
import javax.inject.Inject

class HomeAdapter @Inject constructor(private var requestList : ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    private lateinit var listerner: OnItemClickListerner
//    private lateinit var highLightListerner : HighLightOnClickListerner
//    private lateinit var notHighLightListerner : NotHighLightOnClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

//    fun setOnHighLightClickListerner(listerner: HighLightOnClickListerner) {
//        this.highLightListerner = listerner
//    }
//
//    fun setOnNotHighLightClickListerner(listerner: NotHighLightOnClickListerner) {
//        this.notHighLightListerner = listerner
//    }

    fun addNewData(list: ArrayList<CreateMatchModel>) {
        requestList = list
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addFilterList(filterList : ArrayList<CreateMatchModel>) {
        requestList = filterList
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding,
        private val listerner: OnItemClickListerner,
//        private val highLightListerner : HighLightOnClickListerner,
//        private val notHighLightListerner : NotHighLightOnClickListerner
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
                        newCreate.visibility = View.GONE
                        waitConfirm.visibility = View.GONE
                        if (list.highlight == 1) {
                            highlightIcon2.visibility = View.VISIBLE
                        }
                        if (list.highlight == 0) {
                            highlightIcon2.visibility = View.GONE
                        }

                        items.setOnClickListener {
                            listerner.onItemClick(list)
                        }

//                        highlightIcon1.setOnClickListener {
//                            highLightListerner.onHighLightClickListerner(list)
//                        }
//
//                        highlightIcon2.setOnClickListener {
//                            notHighLightListerner.onNotHighLightClickListerner(list)
//                        }

                        highlightIcon1.setOnClickListener {
                            highlightIcon1.visibility = View.GONE
                            highlightIcon2.visibility = View.VISIBLE
                        }

                        highlightIcon2.setOnClickListener {
                            highlightIcon2.visibility = View.GONE
                            highlightIcon1.visibility = View.VISIBLE
                        }
                    }
                }
            }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = MatchItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(requestList[position])
    }

    override fun getItemCount(): Int {
        return requestList.size
    }
}