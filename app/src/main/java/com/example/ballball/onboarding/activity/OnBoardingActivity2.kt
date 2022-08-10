package com.example.ballball.onboarding.activity

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.ballball.databinding.ActivityOnBoarding2Binding
import com.example.ballball.login.phone.login.SignInActivity
import com.example.ballball.main.MainActivity
import com.example.ballball.onboarding.adapter.OnBoardingAdapter
import com.example.ballball.onboarding.transitions.DepthPageTransformer
import com.example.ballball.utils.Animation
import com.example.ballball.utils.DatabaseConnection
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth


class OnBoardingActivity2 : AppCompatActivity() {

    private lateinit var onBoarding2Binding: ActivityOnBoarding2Binding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBoarding2Binding = ActivityOnBoarding2Binding.inflate(layoutInflater)
        setContentView(onBoarding2Binding.root)

        if (Build.VERSION.SDK_INT in 19..20) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }
        if (Build.VERSION.SDK_INT >= 19) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }

        onBoarding2Binding.viewPager.setPageTransformer(DepthPageTransformer())
        initViewPager()
        initEvents()
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    private fun initEvents() {
        skip()
        next()
    }

    private fun next() {
        onBoarding2Binding.next.setOnClickListener {
            if (getItem() > onBoarding2Binding.viewPager.childCount) {
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                Animation.animateSlideLeft(this)
            } else {
                onBoarding2Binding.viewPager.setCurrentItem(getItem() + 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return onBoarding2Binding.viewPager.currentItem
    }

    private fun skip() {
        onBoarding2Binding.skip.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun initViewPager() {
        onBoarding2Binding.viewPager.adapter = OnBoardingAdapter(this, this)
        onBoarding2Binding.pageIndicator.let {
            TabLayoutMediator(it, onBoarding2Binding.viewPager) { _, _ -> }.attach()
        }
    }
}