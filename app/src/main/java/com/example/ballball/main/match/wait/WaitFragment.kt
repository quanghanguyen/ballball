package com.example.ballball.main.match.wait

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.databinding.FragmentWaitBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.internal.wait

@AndroidEntryPoint
class WaitFragment : Fragment() {

    private lateinit var waitBinding : FragmentWaitBinding
    private lateinit var waitAdapter : HomeAdapter
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
            when (result) {
                is WaitViewModel.LoadWaitResult.ResultOk -> {
                    waitAdapter.addNewData(result.list)
                }
                is WaitViewModel.LoadWaitResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        waitBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            waitAdapter = HomeAdapter(arrayListOf())
            adapter = waitAdapter

            waitAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    AllDetailsActivity.startDetails(context, requestData)
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