package com.example.ballball.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.databinding.ActivitySearchBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Animation
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {
    private lateinit var searchBinding: ActivitySearchBinding
    private lateinit var homeAdapter: HomeAdapter
    private val searchViewModel : SearchViewModel by viewModels()
    val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(searchBinding.root)
        initList()
        initEvents()
        initObserve()
    }

    private fun initEvents() {
        initSearch()
        back()
    }

    private fun back() {
        searchBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun initSearch() {
        searchBinding.search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                //
            }

            override fun afterTextChanged(s: Editable?) {
                if (userUID != null) {
                    searchViewModel.loadFilterList(userUID, s.toString())
                }
            }
        })
    }

    private fun initObserve() {
        searchViewModel.searchFilter.observe(this) {result ->
            when (result) {
                is SearchViewModel.SearchFilterResult.ResultOk -> {
                    if (searchBinding.search.text.isNullOrEmpty()) {
                        searchBinding.recyclerView.visibility = View.GONE
                        searchBinding.imageLayout.visibility = View.GONE
                    }
                    if (result.list.isEmpty()) {
                        with(searchBinding) {
                            recyclerView.visibility = View.GONE
                            imageLayout.visibility = View.VISIBLE
                        }
                    } else {
                        homeAdapter.addFilterList(result.list)
                    }
                }
                is SearchViewModel.SearchFilterResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        searchBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            homeAdapter = HomeAdapter(arrayListOf())
            adapter = homeAdapter

            homeAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    AllDetailsActivity.startDetails(context, requestData)
                    Animation.animateSlideLeft(this@SearchActivity)
                    }
                }
            )
        }
    }
}