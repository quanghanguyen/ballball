package com.example.ballball.main.match.wait

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.adapter.WaitAdapter
import com.example.ballball.databinding.FragmentWaitBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.main.match.wait.details.WaitDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.wait

@AndroidEntryPoint
class WaitFragment : Fragment() {

    private lateinit var waitBinding : FragmentWaitBinding
    private lateinit var waitAdapter : WaitAdapter
    private val waitViewModel : WaitViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            waitViewModel.loadWaitList(userUID)
        }
    }

    private fun initObserve() {
        loadWaitObserve()
    }

    private fun loadWaitObserve() {
        waitViewModel.loadWait.observe(viewLifecycleOwner) {result ->
            with(waitBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is WaitViewModel.LoadWaitResult.Loading -> {
                    waitBinding.progressBar.visibility = View.VISIBLE
                }
                is WaitViewModel.LoadWaitResult.ResultOk -> {
                    if (result.list.isEmpty()) {
                        waitBinding.imageLayout.visibility = View.VISIBLE
                        waitBinding.recyclerView.visibility = View.GONE
                        waitBinding.progressBar.visibility = View.GONE
                    } else {
                        waitAdapter.addNewData(result.list)
                    }
                }
                is WaitViewModel.LoadWaitResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        waitBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            waitAdapter = WaitAdapter(arrayListOf())
            adapter = waitAdapter

            waitAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    WaitDetailsActivity.startDetails(context, requestData)
                    }
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        waitBinding = FragmentWaitBinding.inflate(inflater, container, false)
        return waitBinding.root
    }
}