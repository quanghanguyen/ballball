package com.example.ballball.user.walkthrough.name

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.ballball.R
import com.example.ballball.databinding.ActivityNameBinding
import com.example.ballball.user.walkthrough.avatar.AvatarActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.AuthConnection.auth
import com.example.ballball.utils.AuthConnection.authUser
import com.example.ballball.utils.AuthConnection.uid
import com.example.ballball.utils.ClearableEditText.makeClearableEditText
import com.example.ballball.utils.ClearableEditText.onRightDrawableClicked
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NameActivity : AppCompatActivity() {

    private lateinit var nameBinding: ActivityNameBinding
    private val nameViewModel : NameViewModel by viewModels()
    private var phoneNumber : String? = null
    private val userUID = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nameBinding = ActivityNameBinding.inflate(layoutInflater)
        setContentView(nameBinding.root)
        initEvents()
        initObserve()
    }


    private fun initEvents() {
        makeClearableEditText()
        next()
    }

    private fun initObserve() {
        nameViewModel.saveUsers.observe(this) {result ->
            when (result) {
                is NameViewModel.SaveUsers.ResultOk -> {
                    startActivity(Intent(this, AvatarActivity::class.java))
                    Animation.animateSlideLeft(this)
                }
                is NameViewModel.SaveUsers.ResultError -> {
                    Toast.makeText(this, result.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun next() {
        phoneNumber = intent.getStringExtra("phoneNumber")
        nameBinding.next.setOnClickListener {
            if (nameBinding.name.text.isNullOrEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên của bạn", Toast.LENGTH_SHORT).show()
            } else {
                    phoneNumber?.let { phoneNumber ->
                        if (userUID != null) {
                            nameViewModel.saveUsers(
                                userUid = userUID,
                                userName = nameBinding.name.text.toString(),
                                userPhone = phoneNumber
                            )
                        }
                    }
                }
            }
         }

    private fun makeClearableEditText() {
        addRightCancelDrawable(nameBinding.name)
        nameBinding.name.onRightDrawableClicked {
            it.text.clear()
        }
        nameBinding.name.makeClearableEditText(null, null)
    }

    private fun addRightCancelDrawable(name: TextInputEditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.ic_baseline_clear_24)
        cancel?.setBounds(0,0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        name.setCompoundDrawables(null, null, cancel, null)
    }
}