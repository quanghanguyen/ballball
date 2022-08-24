package com.example.ballball.main.home.nearme

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
import com.example.ballball.adapter.NearMeAdapter
import com.example.ballball.databinding.FragmentNearMeBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearMeFragment : Fragment() {

    private lateinit var nearMeBinding: FragmentNearMeBinding
    private lateinit var nearMeAdapter: NearMeAdapter
    private val nearMeViewModel : NearMeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initObserve()
        nearMeViewModel.loadNearMeList(currentLat!!, currentLong!!)
        Log.e("currentLat", currentLat.toString())
        Log.e("currentLong", currentLong.toString())
    }

    private fun initObserve() {
        nearMeViewModel.loadNearMe.observe(viewLifecycleOwner) {result ->
            with(nearMeBinding) {
                progressBar.visibility = View.GONE
                nearMeRecyclerView.visibility = View.VISIBLE
            }
            when (result) {
                is NearMeViewModel.LoadNearMeResult.Loading -> {
                    nearMeBinding.progressBar.visibility = View.VISIBLE
                }
                is NearMeViewModel.LoadNearMeResult.ResultOk -> {
                    Log.e("SIZE", result.list.size.toString())
                    if (result.list.isEmpty()) {
                        nearMeBinding.nearMeRecyclerView.visibility = View.GONE
                        nearMeBinding.imageLayout.visibility = View.VISIBLE
                        nearMeBinding.progressBar.visibility = View.GONE
                    } else {
                        nearMeAdapter.addNewData(result.list)
                    }
                }
                is NearMeViewModel.LoadNearMeResult.ResultError -> {}
            }
        }
    }

    private fun initList() {
        nearMeBinding.nearMeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            nearMeAdapter = NearMeAdapter(arrayListOf())
            adapter = nearMeAdapter

            nearMeAdapter.setOnItemClickListerner(object :
            OnItemClickListerner{
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
        nearMeBinding = FragmentNearMeBinding.inflate(inflater, container, false)
        return nearMeBinding.root
    }
}