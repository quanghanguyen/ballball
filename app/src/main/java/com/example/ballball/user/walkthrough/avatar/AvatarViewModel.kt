package com.example.ballball.user.walkthrough.avatar

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AvatarViewModel @Inject constructor(private val avatarRepository: AvatarRepository) : ViewModel() {

    val saveAvatar = MutableLiveData<SaveAvatar>()

    sealed class SaveAvatar {
        object ResultOk : SaveAvatar()
        class ResultError(val errorMessage : String) : SaveAvatar()
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