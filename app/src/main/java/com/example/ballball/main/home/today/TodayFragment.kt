package com.example.ballball.main.home.today

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.HomeAdapter
import com.example.ballball.databinding.FragmentTodayBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodayFragment : Fragment() {

    private lateinit var todayBinding: FragmentTodayBinding
    private val todayViewModel : TodayViewModel by viewModels()
    private lateinit var todayAdapter : HomeAdapter
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            todayViewModel.loadToday(userUID)
        }
    }

    private fun initObserve() {
        todayViewModel.loadTodayList.observe(viewLifecycleOwner) {result ->
            when (result) {
                is TodayViewModel.LoadTodayList.ResultOk -> {
                    todayAdapter.addNewData(result.list)
                }
                is TodayViewModel.LoadTodayList.ResultError -> {
                    Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initList() {
        todayBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            todayAdapter = HomeAdapter(arrayListOf())
            adapter = todayAdapter

            todayAdapter.setOnItemClickListerner(object :
                OnItemClickListerner {
                override fun onItemClick(requestData: CreateMatchModel) {
                    AllDetailsActivity.startDetails(context, requestData)
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        todayBinding = FragmentTodayBinding.inflate(inflater, container, false)
        return todayBinding.root
    }
}