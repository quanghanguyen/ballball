package com.example.ballball.main.home

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.user.teaminformation.TeamInformationViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val homeRepository: HomeRepository) : ViewModel() {
    val loadAvatar = MutableLiveData<LoadAvatar>()

    sealed class LoadAvatar {
        class ResultOk(val image : Bitmap) : LoadAvatar()
        object ResultError : LoadAvatar()
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
}