package com.example.ballball.main.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnIconClickListerner
import com.example.ballball.adapter.ChatAdapter
import com.example.ballball.databinding.FragmentChatBinding
import com.example.ballball.main.chat.details.ChatDetailsActivity
import com.example.ballball.model.UsersModel
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

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
        loadListObserve()
        filterObserve()
    }

    private fun filterObserve() {
        chatViewModel.chatFilter.observe(viewLifecycleOwner) {result ->
            when (result) {
                is ChatViewModel.ChatFilter.ResultOk -> {
                    if (result.list.isEmpty()) {
                        with(chatBinding) {
                            recyclerView.visibility = View.GONE
                            imageLayout.visibility = View.VISIBLE
                        }
                    }
                    chatAdapter.addFilterList(result.list)
                }
                is ChatViewModel.ChatFilter.ResultError -> {}
            }
        }
    }

    private fun loadListObserve() {
        chatViewModel.loadChatList.observe(viewLifecycleOwner) {result ->
            with(chatBinding){
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is ChatViewModel.LoadChatList.Loading -> {
                    chatBinding.progressBar.visibility = View.VISIBLE
                }
                is ChatViewModel.LoadChatList.ResultOk -> {
                    chatAdapter.addNewData(result.list)
                    chatBinding.search.addTextChangedListener(object : TextWatcher {
                        override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            //
                        }

                        override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                            //
                        }

                        override fun afterTextChanged(s: Editable?) {
                            chatViewModel.loadFilterList(userUID!!, s.toString())
                            if (s.toString().isEmpty()) {
                                chatViewModel.loadChatList(userUID)
                                chatBinding.imageLayout.visibility = View.GONE
                            }
                        }
                    })
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
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
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