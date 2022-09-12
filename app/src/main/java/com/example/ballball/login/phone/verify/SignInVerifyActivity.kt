package com.example.ballball.login.phone.verify

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.Toast
import com.example.ballball.R
import com.example.ballball.databinding.ActivitySignInVerifyBinding
import com.example.ballball.databinding.LoadingDialogBinding
import com.example.ballball.main.MainActivity
import com.example.ballball.user.walkthrough.name.NameActivity
import com.example.ballball.utils.Animation
import com.example.ballball.utils.AuthConnection.auth
import com.example.ballball.utils.DatabaseConnection
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class SignInVerifyActivity : AppCompatActivity() {

    private lateinit var signInVerifyBinding: ActivitySignInVerifyBinding
    private var phoneNumber : String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    private lateinit var loadingDialogBinding: LoadingDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInVerifyBinding = ActivitySignInVerifyBinding.inflate(layoutInflater)
        setContentView(signInVerifyBinding.root)
        initEvents()
    }

    private fun initEvents() {
        callback()
        binding()
        verify()
        back()
        resendCode()
        tryOtherPhoneNumber()
    }

    private fun callback() {
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
    }

    private fun resendCode() {
        signInVerifyBinding.resend.setOnClickListener {
            phoneNumber?.let {
                resend(it)
            }
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        Animation.animateSlideRight(this)
    }

    private fun verify() {
        val dialog = Dialog(this, R.style.MyAlertDialogTheme)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        loadingDialogBinding = LoadingDialogBinding.inflate(layoutInflater)
        dialog.setContentView(loadingDialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val storedVerificationId = intent.getStringExtra("storedVerificationId")

        signInVerifyBinding.verify.setOnClickListener {
            val otp = signInVerifyBinding.pin.text.toString().trim()
            if(otp.isNotEmpty()){
                dialog.show()
                val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(
                    storedVerificationId.toString(), otp)

                auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        val userUID = FirebaseAuth.getInstance().currentUser?.uid
                        DatabaseConnection.databaseReference.getReference("Teams").child(userUID!!).get()
                            .addOnSuccessListener {
                                if (it.exists()) {
                                    dialog.dismiss()
                                    startActivity(Intent(this, MainActivity::class.java))
                                    Animation.animateSlideLeft(this)
                                } else {
                                    dialog.dismiss()
                                    val intent = Intent(applicationContext, NameActivity::class.java)
                                    intent.putExtra("phoneNumber", phoneNumber)
                                    startActivity(intent)
                                    finish()
                                    Animation.animateSlideLeft(this)
                                }
                            }
                    } else {
                        if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            dialog.dismiss()
                            Toast.makeText(this,"Mã OTP không hợp lệ", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                dialog.dismiss()
                Toast.makeText(this,"Hãy nhập mã OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}