package com.example.ballball.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.user.walkthrough.team.TeamViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository : MainRepository) : ViewModel() {
    val updateUsers = MutableLiveData<UpdateUsers>()

    sealed class UpdateUsers {
        object ResultOk : UpdateUsers()
        object ResultError : UpdateUsers()
    }

    fun updateUser(
        userUID : String,
        avatarUrl : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            mainRepository.updateUser(userUID, avatarUrl, {
                updateUsers.value = UpdateUsers.ResultOk
            }, {
                updateUsers.value = UpdateUsers.ResultError
            })
        }
    }
}