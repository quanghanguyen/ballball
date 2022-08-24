package com.example.ballball.main.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.adapter.ChatAdapter
import com.example.ballball.databinding.FragmentChatBinding
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.model.UsersModel
import com.google.firebase.auth.FirebaseAuth
import com.sendbird.uikit.fragments.ChannelListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var chatBinding: FragmentChatBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatViewModel : ChatViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            chatViewModel.loadChatList(userUID)
        }
    }

    private fun initObserve() {
        chatViewModel.loadChatList.observe(viewLifecycleOwner) {result ->
            with(chatBinding){
                progressBar.visibility = View.GONE
                mainLayout.visibility = View.VISIBLE
            }
            when (result) {
                is ChatViewModel.LoadChatList.Loading -> {
                    chatBinding.progressBar.visibility = View.VISIBLE
                }
                is ChatViewModel.LoadChatList.ResultOk -> {
                    chatAdapter.addNewData(result.list)
                }
                is ChatViewModel.LoadChatList.ResultError -> {}
            }
        }
    }

    private fun initList() {
        chatBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            chatAdapter = ChatAdapter(arrayListOf())
            adapter = chatAdapter

            chatAdapter.setOnIconClickListerner(object :
            OnIconClickListerner {
                override fun onIconClick(requestData: UsersModel) {
                    ChatDetailsActivity.startDetails(context, requestData)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatBinding = FragmentChatBinding.inflate(inflater, container, false)
        return chatBinding.root
    }
}