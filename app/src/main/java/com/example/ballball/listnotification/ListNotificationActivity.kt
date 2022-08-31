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
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ListNotificationActivity : AppCompatActivity() {

    private lateinit var listNotificationBinding: ActivityListNotificationBinding
    private val listNotificationViewModel : ListNotificationViewModel by viewModels()
    private lateinit var listNotificationAdapter: ListNotificationAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private var items : Int? = null

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
                    Log.e("Click", "Clicked")
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
            with(listNotificationBinding) {
                progressBar.visibility = View.GONE
                line2.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is ListNotificationViewModel.LoadListNotificationResult.Loading -> {
                    listNotificationBinding.progressBar.visibility = View.VISIBLE
                }
                is ListNotificationViewModel.LoadListNotificationResult.ResultOk -> {
                    if (result.list.isEmpty()) {
                        listNotificationBinding.recyclerView.visibility = View.GONE
                        listNotificationBinding.imageLayout.visibility = View.VISIBLE
                        listNotificationBinding.progressBar.visibility = View.GONE
                    } else {
                        listNotificationAdapter.addNewData(result.list)
                    }
                }
                is ListNotificationViewModel.LoadListNotificationResult.ResultError -> {}
            }
        }
    }

    private fun initEvents() {
        handleVariables()
        back()
        markRead()
    }

    private fun handleVariables() {
        DatabaseConnection.databaseReference.getReference("listNotifications").child(userUID!!).addValueEventListener(object :
        ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                items = if (snapshot.exists()) {
                    snapshot.childrenCount.toInt()
                } else {
                    0
                }
                if (items == 0) {
                    listNotificationBinding.imageLayout.visibility = View.VISIBLE
                    listNotificationBinding.line2.visibility = View.GONE
                    listNotificationBinding.recyclerView.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                //
            }
        })
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

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }
}