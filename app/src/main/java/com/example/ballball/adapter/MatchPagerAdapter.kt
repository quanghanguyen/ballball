package com.example.ballball.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballball.main.match.history.HistoryFragment
import com.example.ballball.main.match.upcoming.UpComingFragment
import com.example.ballball.main.match.wait.WaitFragment
import javax.inject.Inject

class MatchPagerAdapter @Inject constructor(fm: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fm, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                UpComingFragment()
            }
            1 -> {
                WaitFragment()
            }
            2 -> {
                HistoryFragment()
            }
            else -> {
                Fragment()
            }
        }
    }
}