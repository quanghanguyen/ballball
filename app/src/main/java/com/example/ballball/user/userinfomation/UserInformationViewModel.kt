package com.example.ballball.user.userinfomation

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.home.HomeRepository
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserInformationViewModel @Inject constructor(
    private val homeRepository : HomeRepository,
    private val userInformationRepository : UserInformationRepository
    ) : ViewModel() {

    val loadAvatar = MutableLiveData<LoadAvatar>()
    val loadNameAndPhone = MutableLiveData<LoadNameAndPhone>()

    sealed class LoadAvatar {
        class ResultOk(val image : Bitmap) : LoadAvatar()
        object ResultError : LoadAvatar()
    }

    sealed class LoadNameAndPhone {
        class ResultOk(val userName : String, val userPhone : String) : LoadNameAndPhone()
        object ResultError : LoadNameAndPhone()
    }

    fun loadAvatar(
        userUID : String,
        localFile : File
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            homeRepository.loadAvatar(userUID, localFile, {
                loadAvatar.value = LoadAvatar.ResultOk(it)
            }, {
                loadAvatar.value = LoadAvatar.ResultError
            })
        }
    }

    fun loadNameAndPhone(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            userInformationRepository.loadNameAndPhone(userUID, {
                if (it.exists()) {
                    val userName = it.child("userName").value.toString()
                    val userPhone = it.child("userPhone").value.toString()
                    loadNameAndPhone.value = LoadNameAndPhone.ResultOk(userName, userPhone)
                }
            }, {
                loadNameAndPhone.value = LoadNameAndPhone.ResultError
            })
        }
    }
}