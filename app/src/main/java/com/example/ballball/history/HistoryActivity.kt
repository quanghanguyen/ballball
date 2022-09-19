package com.example.ballball.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.`interface`.HighLightOnClickListerner
import com.example.ballball.`interface`.NotHighLightOnClickListerner
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.UpComingAdapter
import com.example.ballball.databinding.ActivityHistoryBinding
import com.example.ballball.history.details.HistoryDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var historyBinding: ActivityHistoryBinding
    private lateinit var upComingAdapter: UpComingAdapter
    private val historyViewModel : HistoryViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyBinding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(historyBinding.root)
        initList()
        initEvents()
        initObserve()
        if (userUID != null) {
            historyViewModel.loadHistoryMatch(userUID)
        }
    }

    private fun initEvents() {
        back()
    }

    private fun back() {
        historyBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun initObserve() {
        loadListObserve()
        highLightObserve()
    }

    private fun highLightObserve() {
        historyViewModel.highlight.observe(this) {result ->
            when (result) {
                is HistoryViewModel.HighLight.HighLightOk -> {}
                is HistoryViewModel.HighLight.HighLightError -> {}
                is HistoryViewModel.HighLight.NotHighLightOk -> {}
                is HistoryViewModel.HighLight.NotHighLightError -> {}
            }
        }
    }

    private fun loadListObserve() {
        historyViewModel.loadHistoryMatch.observe(this) {result ->
            with(historyBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is HistoryViewModel.LoadHistoryMatch.Loading -> {
                    historyBinding.progressBar.visibility = View.VISIBLE
                }
                is HistoryViewModel.LoadHistoryMatch.ResultOk -> {
                    if (result.historyList.isEmpty()) {
                        historyBinding.imageLayout.visibility = View.VISIBLE
                        historyBinding.recyclerView.visibility = View.GONE
                        historyBinding.progressBar.visibility = View.GONE
                    } else {
                        upComingAdapter.addNewData(result.historyList)
                    }
                }
                is HistoryViewModel.LoadHistoryMatch.ResultError -> {}
            }
        }
    }

    private fun initList() {
        historyBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            upComingAdapter = UpComingAdapter(arrayListOf())
            adapter = upComingAdapter

            upComingAdapter.setOnItemClickListerner(object :
            OnItemClickListerner{
                override fun onItemClick(requestData: CreateMatchModel) {
                    HistoryDetailsActivity.startDetails(context, requestData)
                    Animation.animateSlideLeft(this@HistoryActivity)
                }
            })

            upComingAdapter.setOnHighLightClickListerner(object :
                HighLightOnClickListerner {
                override fun onHighLightClickListerner(requestData: CreateMatchModel) {
                    historyViewModel.highLight(userUID!!, requestData.matchID)
                }
            })

            upComingAdapter.setOnNotHighLightClickListerner(object :
                NotHighLightOnClickListerner {
                override fun onNotHighLightClickListerner(requestData: CreateMatchModel) {
                    historyViewModel.notHighLight(userUID!!, requestData.matchID)
                }
            })
        }
    }
}