package com.example.ballball.main.home.nearme

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.NearMeAdapter
import com.example.ballball.databinding.FragmentNearMeBinding
import com.example.ballball.databinding.LocationAccessDialogBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Model.currentLat
import com.example.ballball.utils.Model.currentLong
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NearMeFragment : Fragment() {

    private lateinit var nearMeBinding: FragmentNearMeBinding
    private lateinit var nearMeAdapter: NearMeAdapter
    private val nearMeViewModel : NearMeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initList()
        initEvents()
        initObserve()
        nearMeViewModel.loadNearMeList(currentLat!!, currentLong!!)
        Log.e("currentLat", currentLat.toString())
        Log.e("currentLong", currentLong.toString())
    }

    private fun initEvents() {
        //
    }

    private fun initObserve() {
        nearMeViewModel.loadNearMe.observe(viewLifecycleOwner) {result ->
            when (result) {
                is NearMeViewModel.LoadNearMeResult.ResultOk -> {
                    nearMeAdapter.addNewData(result.list)
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