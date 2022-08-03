package com.example.ballball.user.userinfomation

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.home.HomeRepository
import com.example.ballball.user.walkthrough.avatar.AvatarRepository
import com.example.ballball.user.walkthrough.avatar.AvatarViewModel
import com.google.firebase.database.DataSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserInformationViewModel @Inject constructor(
    private val homeRepository : HomeRepository,
    private val userInformationRepository : UserInformationRepository,
    private val avatarRepository: AvatarRepository
    ) : ViewModel() {

    val loadAvatar = MutableLiveData<LoadAvatar>()
    val loadNameAndPhone = MutableLiveData<LoadNameAndPhone>()
    val saveAvatar = MutableLiveData<SaveAvatar>()

    sealed class LoadAvatar {
        object Loading : LoadAvatar()
        class ResultOk(val image : Bitmap) : LoadAvatar()
        object ResultError : LoadAvatar()
    }

    sealed class LoadNameAndPhone {
        class ResultOk(val userName : String, val userPhone : String) : LoadNameAndPhone()
        object ResultError : LoadNameAndPhone()
    }

    sealed class SaveAvatar {
        object ResultOk : SaveAvatar()
        class ResultError(val errorMessage : String) : SaveAvatar()
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

    fun saveAvatar(imgUri : Uri, userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            avatarRepository.saveAvatar(imgUri, userUID, {
                saveAvatar.value = SaveAvatar.ResultOk
            }, {
                saveAvatar.value = SaveAvatar.ResultError(it)
            })
        }
    }
}