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

    sealed class CancelWaitMatch {
        object ResultOk : CancelWaitMatch()
        object ResultError : CancelWaitMatch()
        object NotificationOk : CancelWaitMatch()
        object NotificationError : CancelWaitMatch()
    }

    fun cancelWaitMatch(userUID: String, matchID: String, date: String, time: String, clientTeamName: String) {
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
    }
}