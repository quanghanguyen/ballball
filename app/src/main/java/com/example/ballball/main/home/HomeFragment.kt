package com.example.ballball.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.ballball.R
import com.example.ballball.adapter.HomePagerAdapter
import com.example.ballball.databinding.FragmentHomeBinding
import com.example.ballball.listnotification.ListNotificationActivity
import com.example.ballball.user.userinfomation.UserInformationActivity
import com.example.ballball.utils.Animation
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var homeBinding: FragmentHomeBinding
    private val homeViewModel : HomeViewModel by viewModels()
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid
    private val localFile = File.createTempFile("tempImage", "jpg")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
        initEvents()
        initObserve()
        if (userUID != null) {
            homeViewModel.loadAvatar(userUID, localFile)
        }
    }

    private fun initEvents() {
        goUserInfo()
        goListNotification()
    }

    private fun goListNotification() {
        homeBinding.notification.setOnClickListener {
            startActivity(Intent(context, ListNotificationActivity::class.java))
        }
    }

    private fun goUserInfo() {
        homeBinding.userAvatar.setOnClickListener {
            startActivity(Intent(context, UserInformationActivity::class.java))
        }
    }

    private fun initObserve() {
        homeViewModel.loadAvatar.observe(viewLifecycleOwner) {result ->
            when (result) {
                is HomeViewModel.LoadAvatar.ResultOk -> {
                    homeBinding.userAvatar.setImageBitmap(result.image)
                }
                is HomeViewModel.LoadAvatar.ResultError -> {}
                else -> {}
            }
        }
    }

    private fun initViewPager() {
        val matchPagerAdapter = HomePagerAdapter(childFragmentManager, lifecycle)
        homeBinding.viewPager.adapter = matchPagerAdapter

        TabLayoutMediator(homeBinding.tabLayout, homeBinding.viewPager) {tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Tất cả"
                }
                1 -> {
                    tab.text = "Hôm nay"
                }
                2 -> {
                    tab.text = "Ngày mai"
                }
                3 -> {
                    tab.text = "Gần tôi"
                }
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }
}