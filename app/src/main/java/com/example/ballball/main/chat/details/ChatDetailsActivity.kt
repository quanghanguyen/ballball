package com.example.ballball.main.chat.details

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.adapter.ChatDetailsAdapter
import com.example.ballball.databinding.ActivityChatDetailsBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.UsersModel
import com.example.ballball.utils.Animation
import com.example.ballball.utils.Model
import com.example.ballball.utils.Model.receiverId
import com.example.ballball.utils.Model.teamImageUrl
import com.example.ballball.utils.Model.teamName
import com.example.ballball.utils.StorageConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class ChatDetailsActivity : AppCompatActivity() {

    private lateinit var chatDetailsBinding: ActivityChatDetailsBinding
    private val chatDetailsViewModel : ChatDetailsViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var chatDetailsAdapter: ChatDetailsAdapter
    private var intentReceiverId : String? = null
    var thisTeamName : String? = null

    companion object {
        private const val KEY_DATA = "request_data"
        fun startDetails(context: Context, data : UsersModel?)
        {
            context.startActivity(Intent(context, ChatDetailsActivity::class.java).apply {
                putExtra(KEY_DATA, data)
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        chatDetailsBinding = ActivityChatDetailsBinding.inflate(layoutInflater)
        setContentView(chatDetailsBinding.root)
        initEvents()
        initObserves()
        if (userUID != null) {
            if (receiverId.isNullOrEmpty()) {
                chatDetailsViewModel.readMessage(userUID, intentReceiverId!!)
            } else {
                chatDetailsViewModel.readMessage(userUID, receiverId!!)
            }
        }
    }

    private fun initObserves() {
        saveChatObserve()
        readMessageObserve()
    }

    private fun readMessageObserve() {
        chatDetailsViewModel.readMessageResult.observe(this) {result ->
            when (result) {
                is ChatDetailsViewModel.ReadMessageResult.ResultOk -> {
                    chatDetailsAdapter.addNewData(result.list)
                }
                is ChatDetailsViewModel.ReadMessageResult.ResultError -> {}
            }
        }
    }

    private fun saveChatObserve() {
        chatDetailsViewModel.saveChatResult.observe(this) { result ->
            when (result) {
                is ChatDetailsViewModel.SaveChatResult.ResultOk -> {}
                is ChatDetailsViewModel.SaveChatResult.ResultError -> {}
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initEvents() {
        binding()
        intentBinding()
        handleVariable()
        initListMessage()
        sendChat()
        back()
    }

    private fun handleVariable() {
        if (userUID != null) {
            FirebaseDatabase.getInstance().getReference("Teams").child(userUID).get()
                .addOnSuccessListener {
                    teamName = it.child("teamName").value.toString()
                }
            }

        if (userUID != null) {
            StorageConnection.storageReference.getReference("Users").child(userUID).downloadUrl
                .addOnSuccessListener {
                    teamImageUrl = it.toString()
                }
                .addOnFailureListener {
                    Log.e("Error", it.toString())
                }
            }
        }

    private fun initListMessage() {
        chatDetailsBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            chatDetailsAdapter = ChatDetailsAdapter(arrayListOf())
            adapter = chatDetailsAdapter
        }
    }

    private fun back() {
        chatDetailsBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun binding() {
        intent?.let { bundle ->
            val data = bundle.getParcelableExtra<UsersModel>(KEY_DATA)
            with(chatDetailsBinding) {
                thisTeamName = data?.teamName
                teamName.text = data?.teamName
                receiverId = data?.userUid
            }
        }
    }

    private fun intentBinding() {
        val name = intent?.getStringExtra("teamName")
        val userUid = intent?.getStringExtra("userUid")
        if (name.isNullOrEmpty()) {
            chatDetailsBinding.teamName.text = thisTeamName
        } else {
            chatDetailsBinding.teamName.text = name
            intentReceiverId = userUid
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendChat() {
        chatDetailsBinding.send.setOnClickListener {
            if (chatDetailsBinding.message.text.isNotEmpty()) {
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")
                val formatted = current.format(formatter).toString()
                if (userUID != null) {
                    if (receiverId == null) {
                        chatDetailsViewModel.saveChat(userUID, intentReceiverId!!, chatDetailsBinding.message.text.toString(), formatted,
                            teamImageUrl!!, teamName!!)
                    }
                    else {
                        chatDetailsViewModel.saveChat(userUID, receiverId!!, chatDetailsBinding.message.text.toString(), formatted,
                            teamImageUrl!!, teamName!!)
                    }
                }
                chatDetailsBinding.message.text.clear()
            } else {
                Toast.makeText(this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show()
            }
        }
    }
}