package com.example.ballball.main.match.upcoming

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.ConfirmAdapter
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.adapter.UpComingAdapter
import com.example.ballball.databinding.FragmentUpComingBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.main.match.confirm.details.ConfirmDetailsActivity
import com.example.ballball.main.match.upcoming.details.UpComingDetailsActivity
import com.example.ballball.main.match.wait.WaitViewModel
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpComingFragment : Fragment() {

    private lateinit var upComingBinding : FragmentUpComingBinding
    private lateinit var upComingAdapter : UpComingAdapter
    private val upComingViewModel : UpComingViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            upComingViewModel.loadUpComingList(userUID)
        }
    }

    private fun initObserve() {
        upComingViewModel.loadUpComing.observe(viewLifecycleOwner) {result ->
            when (result) {
                is UpComingViewModel.LoadUpComingResult.ResultOk -> {
                    upComingAdapter.addNewData(result.list)
                }
                is UpComingViewModel.LoadUpComingResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        upComingBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            upComingAdapter = UpComingAdapter(arrayListOf())
            adapter = upComingAdapter

            upComingAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    UpComingDetailsActivity.startDetails(context, requestData)
                    }
                }
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        upComingBinding = FragmentUpComingBinding.inflate(inflater, container, false)
        return upComingBinding.root
    }
}