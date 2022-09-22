package com.example.ballball.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.model.ChatModel
import com.example.ballball.model.UsersModel
import com.google.firebase.auth.FirebaseAuth
import de.hdodenhof.circleimageview.CircleImageView
import javax.inject.Inject

class ChatDetailsAdapter @Inject constructor(
    private var list : ArrayList<ChatModel>,
    private var teamAvatar : String
    )
    : RecyclerView.Adapter<ChatDetailsAdapter.MyViewHolder>() {

    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1

    @SuppressLint("NotifyDataSetChanged")
    fun addNewData(chatList: ArrayList<ChatModel>) {
        list = chatList
        notifyDataSetChanged()
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val message : TextView = view.findViewById(R.id.chat_message)
        val time : TextView = view.findViewById(R.id.chat_time)
        val image : CircleImageView = view.findViewById(R.id.chat_user_avatar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return if (viewType == MESSAGE_TYPE_RIGHT) {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_right_side, parent, false)
            MyViewHolder(inflater)
        } else {
            val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_left_side, parent, false)
            MyViewHolder(inflater)
        }
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val chat = list[position]
        holder.message.text = chat.message
        holder.time.text = chat.time
        Glide.with(holder.image).load(teamAvatar).centerCrop().into(holder.image)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].senderId == userUID ) {
            MESSAGE_TYPE_RIGHT
        } else {
            MESSAGE_TYPE_LEFT
        }
    }
}