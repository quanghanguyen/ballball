package com.example.ballball.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballball.main.home.all.AllFragment
import com.example.ballball.main.home.nearme.NearMeFragment
import com.example.ballball.main.home.today.TodayFragment
import com.example.ballball.main.home.tomorrow.TomorrowFragment
import javax.inject.Inject

class HomePagerAdapter @Inject constructor(fm : FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllFragment()
            }
            1 -> {
                TodayFragment()
            }
            2 -> {
                TomorrowFragment()
            }
            3 -> {
                NearMeFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}