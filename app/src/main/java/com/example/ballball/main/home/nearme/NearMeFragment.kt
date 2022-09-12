package com.example.ballball.main.home.nearme

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ballball.R
import com.example.ballball.`interface`.OnItemClickListerner
import com.example.ballball.adapter.NearMeAdapter
import com.example.ballball.databinding.FragmentNearMeBinding
import com.example.ballball.main.home.all.details.AllDetailsActivity
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.utils.Model
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NearMeFragment : Fragment() {

    private lateinit var nearMeBinding: FragmentNearMeBinding
    private lateinit var nearMeAdapter: NearMeAdapter
    private val nearMeViewModel : NearMeViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getLocation()
        initList()
        initObserve()
    }

    private fun getLocation() {
        fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it.applicationContext) }!!

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
            if (location != null) {
                val currentLat = location.latitude
                val currentLong = location.longitude

                Log.e("currentLat", currentLat.toString())
                Log.e("currentLong", currentLong.toString())

                if (userUID != null) {
                    nearMeViewModel.loadNearMeList(userUID, currentLat, currentLong)
                }
            }
        }
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
                    activity?.overridePendingTransition(R.anim.animate_slide_left_enter, R.anim.animate_slide_left_exit)
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