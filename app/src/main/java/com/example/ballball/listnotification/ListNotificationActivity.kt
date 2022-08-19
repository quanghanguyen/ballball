package com.example.ballball.listnotification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
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
    }

    private fun initList() {
        listNotificationBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            listNotificationAdapter = ListNotificationAdapter(arrayListOf())
            adapter = listNotificationAdapter

            listNotificationAdapter.setNotificationOnClickListerner(object :
            NotificationOnClickListerner{
                override fun OnClick(data: ListNotificationModel) {
                    listNotificationBinding.recyclerView.setBackgroundColor(resources.getColor(R.color.white))
                }
            })
        }
    }

    private fun initObserve() {
        listNotificationViewModel.loadListNotification.observe(this) {result ->
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
    }

    private fun back() {
        listNotificationBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }
}