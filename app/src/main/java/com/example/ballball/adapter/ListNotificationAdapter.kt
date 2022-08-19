package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.`interface`.NotificationOnClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.databinding.ItemsNotificationBinding
import com.example.ballball.model.ListNotificationModel
import javax.inject.Inject

class ListNotificationAdapter @Inject constructor(private var notificationList : ArrayList<ListNotificationModel>)
    : RecyclerView.Adapter<ListNotificationAdapter.MyViewHolder>() {

    private lateinit var listerner: NotificationOnClickListerner

    fun setNotificationOnClickListerner(listerner: NotificationOnClickListerner) {
        this.listerner = listerner
    }

    class MyViewHolder(
        private val itemsNotificationBinding: ItemsNotificationBinding,
        private val listerner: NotificationOnClickListerner
    ) : RecyclerView.ViewHolder(itemsNotificationBinding.root) {

        fun bind(list : ListNotificationModel) {
            with(itemsNotificationBinding) {
                teamName.text = list.clientTeamName
                val now = System.currentTimeMillis()
                val timeUtils = list.timeUtils.toLong()
                val relativeTimeStr : CharSequence = DateUtils.getRelativeTimeSpanString(timeUtils, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE)
                time.text = relativeTimeStr
                Glide.with(userAvatar).load(list.clientImageUrl).centerCrop().into(userAvatar)
                //
                if (list.id == "waitMatch") {
                    notificationContent.text = "Muốn đá cùng bạn vào lúc ${list.time} (${list.date})"
                }
                if (list.id == "cancelWaitMatch") {
                    notificationContent.text = "Hủy yêu cầu đá cùng bạn vào lúc ${list.time} (${list.date})"
                }
                if (list.id == "denyRequest") {
                    notificationContent.text = "Từ chối đá cùng bạn vào lúc ${list.time} (${list.date})"
                }
                if (list.id == "acceptRequest") {
                    notificationContent.text = "Đồng ý đá cùng bạn vào lúc ${list.time} (${list.date})"
                }
                if (list.id == "h") {
                    notificationContent.text = "Hủy trận đấu với bạn vào lúc ${list.time} (${list.date}) vì lí do ${list.reason}"
                }

                items.setOnClickListener {
                    listerner.OnClick(list)
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(list: ArrayList<ListNotificationModel>) {
        notificationList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val items = ItemsNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(items, listerner)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(notificationList[position])
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }
}