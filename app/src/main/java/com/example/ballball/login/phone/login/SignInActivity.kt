package com.example.ballball.login.phone.login

import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import com.example.ballball.R
import com.example.ballball.databinding.ActivitySignInBinding
import com.example.ballball.databinding.LoadingDialogBinding
import com.example.ballball.login.phone.verify.SignInVerifyActivity
import com.example.ballball.main.MainActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.AuthConnection.auth
import com.example.ballball.utils.ClearableEditText.makeClearableEditText
import com.example.ballball.utils.ClearableEditText.onRightDrawableClicked
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {

    private lateinit var signInBinding : ActivitySignInBinding
    lateinit var storedVerificationId: String
    private var userPhoneNumber : String? = null
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(signInBinding.root)
        initEvents()
    }

    private fun initEvents() {
        makeClearableEditText()
        signIn()
    }

    private fun makeClearableEditText() {
        addRightCancelDrawable(signInBinding.phoneNumber)
        signInBinding.phoneNumber.onRightDrawableClicked {
            it.text.clear()
        }
        signInBinding.phoneNumber.makeClearableEditText(null, null)
    }

    private fun addRightCancelDrawable(phoneNumber: TextInputEditText) {
        val cancel = ContextCompat.getDrawable(this, R.drawable.ic_baseline_clear_24)
        cancel?.setBounds(0,0, cancel.intrinsicWidth, cancel.intrinsicHeight)
        phoneNumber.setCompoundDrawables(null, null, cancel, null)
    }

    private fun signIn() {
        val dialog = Dialog(this, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(loadingDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        signInBinding.send.setOnClickListener {
            phoneCheck()
            dialog.show()
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                startActivity(Intent(applicationContext, MainActivity::class.java))
                finish()
                Animation.animateSlideLeft(this@SignInActivity)
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("TAG","onCodeSent:$verificationId")
                storedVerificationId = verificationId
                resendToken = token

                val intent = Intent(applicationContext, SignInVerifyActivity::class.java)
                intent.putExtra("storedVerificationId", storedVerificationId)
                intent.putExtra("userPhoneNumber", userPhoneNumber)
                dialog.dismiss()
                startActivity(intent)
                Animation.animateSlideLeft(this@SignInActivity)
            }
        }
    }

    private fun phoneCheck() {
        val areaCode = signInBinding.phoneCode.text.toString().trim()
        var phoneNumber = signInBinding.phoneNumber.text.toString().toInt().toString().trim()
        userPhoneNumber = "$areaCode $phoneNumber"

        if (phoneNumber.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_LONG).show()
        } else {
            phoneNumber = areaCode + phoneNumber
            sendVertification(phoneNumber)
        }
    }

    private fun sendVertification(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
}