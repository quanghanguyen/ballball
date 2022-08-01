package com.example.ballball.login.phone.verify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.ballball.R
import com.example.ballball.databinding.ActivitySignInVerifyBinding
import com.example.ballball.main.MainActivity
import com.example.ballball.user.walkthrough.name.NameActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.AuthConnection.auth
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class SignInVerifyActivity : AppCompatActivity() {

    private lateinit var signInVerifyBinding: ActivitySignInVerifyBinding
    private var phoneNumber : String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInVerifyBinding = ActivitySignInVerifyBinding.inflate(layoutInflater)
        setContentView(signInVerifyBinding.root)

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //
            }
            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(applicationContext, "Đã xảy ra lỗi", Toast.LENGTH_LONG).show()
            }
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Toast.makeText(applicationContext, "Đã gửi lại mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
        initEvents()
    }

    private fun initEvents() {
        binding()
        verify()
        back()
        signInVerifyBinding.resend.setOnClickListener {
            phoneNumber?.let {
                resend(it)
            }
        }
        tryOtherPhoneNumber()
    }

    private fun resend(phoneNumber : String) {
            val options = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        }

    private fun binding() {
        val userPhoneNumber = intent.getStringExtra("userPhoneNumber")
        signInVerifyBinding.userPhoneNumber.text = userPhoneNumber
        phoneNumber = userPhoneNumber?.trim()
    }

    private fun tryOtherPhoneNumber() {
        signInVerifyBinding.tryOtherNumber.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun back() {
        signInVerifyBinding.back.setOnClickListener {
            finish()
            Animation.animateSlideRight(this)
        }
    }

    private fun verify() {
        val storedVerificationId = intent.getStringExtra("storedVerificationId")
        signInVerifyBinding.verify.setOnClickListener {
            val otp = signInVerifyBinding.pin.text.toString().trim()
            if(otp.isNotEmpty()){
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)
                signInWithPhoneAuthCredential(credential)
            }else {
                Toast.makeText(this,"Hãy nhập mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent(applicationContext, NameActivity::class.java)
                intent.putExtra("phoneNumber", phoneNumber)
                startActivity(intent)
                finish()
                Animation.animateSlideLeft(this)
            } else {
                if (task.exception is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this,"Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}