package com.example.ballball.main.home.tomorrow

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
import com.example.ballball.databinding.FragmentTomorrowBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.main.home.today.TodayViewModel
import com.example.ballball.model.CreateMatchModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TomorrowFragment : Fragment() {

    private lateinit var tomorrowBinding : FragmentTomorrowBinding
    private lateinit var tomorrowAdapter : HomeAdapter
    private val tomorrowViewModel : TomorrowViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        if (userUID != null) {
            tomorrowViewModel.loadTomorrow(userUID)
        }
    }

    private fun initObserve() {
        tomorrowViewModel.loadTomorrowList.observe(viewLifecycleOwner) {result ->
            with(tomorrowBinding) {
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is TomorrowViewModel.LoadTomorrowList.Loading -> {
                    tomorrowBinding.progressBar.visibility = View.VISIBLE
                }
                is TomorrowViewModel.LoadTomorrowList.ResultOk -> {
                    if (result.list.isEmpty()) {
                        tomorrowBinding.recyclerView.visibility = View.GONE
                        tomorrowBinding.imageLayout.visibility = View.VISIBLE
                        tomorrowBinding.progressBar.visibility = View.GONE
                    } else {
                        tomorrowAdapter.addNewData(result.list)
                    }
                }
                is TomorrowViewModel.LoadTomorrowList.ResultError -> {
                    Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initList() {
        tomorrowBinding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            tomorrowAdapter = HomeAdapter(arrayListOf())
            adapter = tomorrowAdapter

            tomorrowAdapter.setOnItemClickListerner(object :
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
        tomorrowBinding = FragmentTomorrowBinding.inflate(inflater, container, false)
        return tomorrowBinding.root
    }
}