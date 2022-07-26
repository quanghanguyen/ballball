package com.example.ballball.onboarding.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ballball.R
import com.example.ballball.databinding.ActivityOnBoardingBinding
import com.example.ballball.main.MainActivity
import com.example.ballball.onboarding.adapter.OnBoardingAdapter
import com.example.ballball.utils.Animation
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

class OnBoardingActivity : AppCompatActivity() {
    private lateinit var onBoardingBinding: ActivityOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBoardingBinding = ActivityOnBoardingBinding.inflate(layoutInflater)
        setContentView(onBoardingBinding.root)
        initViewPager()
        initEvents()
    }

    private fun initViewPager() {
        onBoardingBinding.viewPager.adapter = OnBoardingAdapter(this, this)
        onBoardingBinding.pageIndicator?.let {
            TabLayoutMediator(it, onBoardingBinding.viewPager) { _, _ -> }.attach()
        }
    }

    private fun initEvents() {
        skip()
        next()
    }

    private fun next() {
        onBoardingBinding.next?.setOnClickListener {
            if (getItem() > onBoardingBinding.viewPager.childCount) {
                finish()
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
            } else {
                onBoardingBinding.viewPager.setCurrentItem(getItem() + 1, true)
            }
        }
    }

    private fun skip() {
        onBoardingBinding.next?.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            Animation.animateSlideLeft(this)
        }
    }

    private fun getItem(): Int {
        return onBoardingBinding.viewPager.currentItem
    }
}