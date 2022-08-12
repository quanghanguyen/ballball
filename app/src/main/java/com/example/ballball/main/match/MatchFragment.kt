package com.example.ballball.main.match

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.ballball.adapter.MatchPagerAdapter
import com.example.ballball.databinding.FragmentMatchBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchFragment : Fragment() {

    private lateinit var matchBinding : FragmentMatchBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewPager()
    }

    private fun initViewPager() {
        val matchPagerAdapter = MatchPagerAdapter(childFragmentManager, lifecycle)
        matchBinding.viewPager.adapter = matchPagerAdapter

        TabLayoutMediator(matchBinding.tabLayout, matchBinding.viewPager) {tab, position ->
            when (position) {
                0 -> {
                    tab.text = "Sắp đá"
                }
                1 -> {
                    tab.text = "Mới tạo"
                }
                2 -> {
                    tab.text = "Đã yêu cầu"
                }
                3 -> {
                    tab.text = "Xác nhận"
                }
                4 -> {
                    tab.text = "Lịch sử"
                }
            }
        }.attach()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        matchBinding = FragmentMatchBinding.inflate(inflater, container, false)
        return matchBinding.root
    }
}