package com.example.ballball.onboarding.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.ballball.R
import com.example.ballball.onboarding.fragment.OnBoardingFragment
import javax.inject.Inject

class OnBoardingAdapter
    (fragmentActivity: FragmentActivity,
     private val context: Context) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.description_onboarding_1),
                R.drawable.onboarding_one
            )
            1 -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_1),
                R.drawable.onboarding_two
            )
            else -> OnBoardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_3),
                context.resources.getString(R.string.description_onboarding_1),
                R.drawable.onboarding_three
            )
        }
    }
}