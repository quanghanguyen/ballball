package com.example.ballball.main.match.upcoming.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpComingDetailsViewModel @Inject constructor(private val upComingDetailsRepository: UpComingDetailsRepository) : ViewModel() {

    val cancelUpComing = MutableLiveData<CancelUpComing>()

    sealed class CancelUpComing {
        object ResultOk : CancelUpComing()
        object ResultError : CancelUpComing()
        object CancelNotificationOk : CancelUpComing()
        object CancelNotificationError : CancelUpComing()
    }

    fun cancelUpComingMatch(
        clientUID: String,
        userUID: String,
        matchID: String,
        date: String,
        time: String,
        teamName: String,
        reason : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            upComingDetailsRepository.cancelMatch(userUID, clientUID, matchID, {
                cancelUpComing.value = CancelUpComing.ResultOk
            }, {
                cancelUpComing.value = CancelUpComing.ResultError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            upComingDetailsRepository.cancelMatchNotification(clientUID, userUID, matchID, date, time, teamName, reason, {
                cancelUpComing.value = CancelUpComing.CancelNotificationOk
            }, {
                cancelUpComing.value = CancelUpComing.CancelNotificationError
            })
        }
    }
}