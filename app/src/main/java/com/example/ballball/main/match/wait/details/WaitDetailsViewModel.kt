package com.example.ballball.main.match.wait.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.newcreate.NewCreateViewModel
import com.example.ballball.main.match.newcreate.details.NewCreateDetailsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class WaitDetailsViewModel @Inject constructor(private val waitDetailsRepository: WaitDetailsRepository) : ViewModel() {

    val cancelWaitMatch = MutableLiveData<CancelWaitMatch>()
    val cancelWaitMatchListNotification = MutableLiveData<CancelWaitMatchListNotification>()

    sealed class CancelWaitMatch {
        object ResultOk : CancelWaitMatch()
        object ResultError : CancelWaitMatch()
        object DeleteConfirmOk : CancelWaitMatch()
        object DeleteConfirmError : CancelWaitMatch()
        object UpdateClickOk : CancelWaitMatch()
        object UpdateClickError : CancelWaitMatch()
        object NotificationOk : CancelWaitMatch()
        object NotificationError : CancelWaitMatch()
    }

    sealed class CancelWaitMatchListNotification {
        object ResultOk : CancelWaitMatchListNotification()
        object ResultError : CancelWaitMatchListNotification()
    }

    fun cancelWaitMatch(
        userUID: String,
        matchID: String,
        date: String,
        time: String,
        clientTeamName: String,
        clientUID: String,
        click : Int
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitDetailsRepository.cancelRequest(userUID, matchID, {
                cancelWaitMatch.value = CancelWaitMatch.ResultOk
            }, {
                cancelWaitMatch.value = CancelWaitMatch.ResultError
            })

            waitDetailsRepository.cancelRequestNotification(userUID, matchID, date, time, clientTeamName,{
                cancelWaitMatch.value = CancelWaitMatch.NotificationOk
            }, {
                cancelWaitMatch.value = CancelWaitMatch.NotificationError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitDetailsRepository.deleteConfirm(clientUID, matchID, {
                cancelWaitMatch.value = CancelWaitMatch.DeleteConfirmOk
            }, {
                cancelWaitMatch.value = CancelWaitMatch.DeleteConfirmError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitDetailsRepository.deleteClick(click, matchID, {
                cancelWaitMatch.value = CancelWaitMatch.UpdateClickOk
            }, {
                cancelWaitMatch.value = CancelWaitMatch.UpdateClickError
            })
        }
    }

    fun cancelWaitMatchListNotification(
        clientUID : String,
        clientTeamName: String,
        clientImageUrl: String,
        id : String,
        date : String,
        time: String,
        timeUtils : Long
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitDetailsRepository.cancelWaitMatchListNotification(clientUID, clientTeamName, clientImageUrl, id, date, time, timeUtils, {
                cancelWaitMatchListNotification.value = CancelWaitMatchListNotification.ResultOk
            }, {
                cancelWaitMatchListNotification.value = CancelWaitMatchListNotification.ResultError
            })
        }
    }
}