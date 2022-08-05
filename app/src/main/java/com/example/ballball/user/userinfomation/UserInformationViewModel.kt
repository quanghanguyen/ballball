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

    val loadUserInfo = MutableLiveData<LoadUserData>()
    val saveAvatar = MutableLiveData<SaveAvatar>()

    sealed class LoadUserData {
        object Loading : LoadUserData()
        class LoadAvatarOk(val image: Bitmap) : LoadUserData()
        object LoadAvatarError : LoadUserData()
        class LoadNameAndPhoneOk(val userName : String, val userPhone : String) : LoadUserData()
        object LoadNameAndPhoneError : LoadUserData()
    }

    sealed class SaveAvatar {
        object ResultOk : SaveAvatar()
        class ResultError(val errorMessage : String) : SaveAvatar()
    }

    fun loadUserInfo(
        userUID: String,
        localFile: File
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            homeRepository.loadAvatar(userUID, localFile, {
                loadUserInfo.value = LoadUserData.LoadAvatarOk(it)
            }, {
                loadUserInfo.value = LoadUserData.LoadAvatarError
            })
        }
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            userInformationRepository.loadNameAndPhone(userUID, {
                if (it.exists()) {
                    val userName = it.child("userName").value.toString()
                    val userPhone = it.child("userPhone").value.toString()
                    loadUserInfo.value = LoadUserData.LoadNameAndPhoneOk(userName, userPhone)
                }
            }, {
                loadUserInfo.value = LoadUserData.LoadNameAndPhoneError
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