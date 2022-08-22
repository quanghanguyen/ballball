package com.example.ballball.listnotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.NotificationOnClickListerner
import com.example.ballball.adapter.ListNotificationAdapter
import com.example.ballball.databinding.ActivityListNotificationBinding
import com.example.ballball.model.ListNotificationModel
import com.example.ballball.utils.Animation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ListNotificationActivity : AppCompatActivity() {

    private lateinit var listNotificationBinding: ActivityListNotificationBinding
    private val listNotificationViewModel : ListNotificationViewModel by viewModels()
    private lateinit var listNotificationAdapter: ListNotificationAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listNotificationBinding = ActivityListNotificationBinding.inflate(layoutInflater)
        setContentView(listNotificationBinding.root)
        initList()
        initEvents()
        initObserve()
        if (userUID != null) {
            listNotificationViewModel.loadListNotification(userUID)
        }

//        if (listNotificationAdapter.itemCount == 0) {
//            listNotificationBinding.line2.visibility = View.GONE
//            listNotificationBinding.recyclerView.visibility = View.GONE
//            listNotificationBinding.imageLayout.visibility = View.VISIBLE
//        }
//
//        val itemCount = listNotificationAdapter.itemCount
//        Log.e("Item Count", itemCount.toString())
    }

    private fun initList() {
        listNotificationBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            listNotificationAdapter = ListNotificationAdapter(arrayListOf())
            adapter = listNotificationAdapter

            listNotificationAdapter.setNotificationOnClickListerner(object :
            NotificationOnClickListerner{
                override fun OnClick(data: ListNotificationModel) {
                    ////
                }
            })
        }
    }

    private fun initObserve() {
        loadListObserve()
        markReadObserve()
    }

    private fun markReadObserve() {
        listNotificationViewModel.markRead.observe(this) { result ->
            when (result) {
                is ListNotificationViewModel.MarkReadResult.ResultOk -> {}
                is ListNotificationViewModel.MarkReadResult.ResultError -> {}
            }
        }
    }

    private fun loadListObserve() {
        listNotificationViewModel.loadListNotification.observe(this) { result ->
            when (result) {
                is ListNotificationViewModel.LoadListNotificationResult.ResultOk -> {
                    listNotificationAdapter.addNewData(result.list)
                }
                is ListNotificationViewModel.LoadListNotificationResult.ResultError -> {}
            }
        }
    }

    private fun initEvents() {
        back()
        markRead()
    }

    private fun markRead() {
        listNotificationBinding.markRead.setOnClickListener {
            if (userUID != null) {
                listNotificationViewModel.markRead(userUID)
            }
        }
    }

    private fun back() {
        listNotificationBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
}