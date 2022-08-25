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
import javax.inject.Inject

class NewCreateAdapter @Inject constructor(private var newCreateList : ArrayList<CreateMatchModel>)
    : RecyclerView.Adapter<NewCreateAdapter.MyViewHolder>() {

    private lateinit var listerner: OnItemClickListerner
    private lateinit var highLightListerner : HighLightOnClickListerner
    private lateinit var notHighLightListerner : NotHighLightOnClickListerner

    fun setOnItemClickListerner(listerner: OnItemClickListerner) {
        this.listerner = listerner
    }

    fun setOnHighLightClickListerner(listerner: HighLightOnClickListerner) {
        this.highLightListerner = listerner
    }

    fun setOnNotHighLightClickListerner(listerner: NotHighLightOnClickListerner) {
        this.notHighLightListerner = listerner
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<CreateMatchModel>) {
        newCreateList = list
        notifyDataSetChanged()
    }

    class MyViewHolder (
        private val matchItemsBinding: MatchItemsBinding,
        private val listerner: OnItemClickListerner,
        private val highLightListerner : HighLightOnClickListerner,
        private val notHighLightListerner : NotHighLightOnClickListerner
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
                if (list.highlight == 1) {
                    highlightIcon2.visibility = View.VISIBLE
                }
                if (list.highlight == 0) {
                    highlightIcon2.visibility = View.GONE
                }

                items.setOnClickListener {
                    listerner.onItemClick(list)
                }

                highlightIcon1.setOnClickListener {
                    highLightListerner.onHighLightClickListerner(list)
                }

                highlightIcon2.setOnClickListener {
                    notHighLightListerner.onNotHighLightClickListerner(list)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = MatchItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner, highLightListerner, notHighLightListerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(newCreateList[position])
    }

    override fun getItemCount(): Int {
        return newCreateList.size
    }
}