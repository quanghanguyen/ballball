package com.example.ballball.main.match.upcoming.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.confirm.details.ConfirmDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpComingDetailsViewModel @Inject constructor(private val upComingDetailsRepository: UpComingDetailsRepository) : ViewModel() {

    val cancelUpComing = MutableLiveData<CancelUpComing>()
    val cancelUpComingListNotification = MutableLiveData<CancelUpComingListNotification>()

    sealed class CancelUpComing {
        object ResultOk : CancelUpComing()
        object ResultError : CancelUpComing()
        object CancelNotificationOk : CancelUpComing()
        object CancelNotificationError : CancelUpComing()
    }

    sealed class CancelUpComingListNotification {
        object ResultOk : CancelUpComingListNotification()
        object ResultError : CancelUpComingListNotification()
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

    fun cancelUpComingListNotification(
        clientUID : String,
        clientTeamName: String,
        clientImageUrl: String,
        id : String,
        date : String,
        time: String,
        reason: String,
        timeUtils : Long
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            upComingDetailsRepository.cancelUpComingListNotification(clientUID, clientTeamName, clientImageUrl, id, date, time, reason, timeUtils, {
                cancelUpComingListNotification.value = CancelUpComingListNotification.ResultOk
            }, {
                cancelUpComingListNotification.value = CancelUpComingListNotification.ResultError
            })
        }
    }
}