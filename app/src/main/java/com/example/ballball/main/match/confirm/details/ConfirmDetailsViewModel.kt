package com.example.ballball.main.match.confirm.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmDetailsViewModel @Inject constructor(private val confirmDetailsRepository: ConfirmDetailsRepository) : ViewModel() {
    val denyConfirmMatch = MutableLiveData<DenyConfirmMatch>()
    val acceptMatch = MutableLiveData<AcceptMatch>()

    sealed class DenyConfirmMatch {
        object ResultOk : DenyConfirmMatch()
        object ResultError : DenyConfirmMatch()
    }

    sealed class AcceptMatch {
//        object DeleteConfirmOk: AcceptMatch()
//        object DeleteConfirmError : AcceptMatch()
        object SaveUpComingOk : AcceptMatch()
        object SaveUpComingError : AcceptMatch()
    }

    fun denyConfirmMatch(
        userUID: String,
        matchId: String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.denyMatch(userUID, matchId, {
                denyConfirmMatch.value = DenyConfirmMatch.ResultOk
            }, {
                denyConfirmMatch.value = DenyConfirmMatch.ResultError
            })
        }
    }

    fun acceptMatch(
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.upComingMatch(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName,
            clientImageUrl, {
                    acceptMatch.value = AcceptMatch.SaveUpComingOk
                }, {
                    acceptMatch.value = AcceptMatch.SaveUpComingError
                })
            }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.denyMatch(userUID, matchID, {
                denyConfirmMatch.value = DenyConfirmMatch.ResultOk
            }, {
                denyConfirmMatch.value = DenyConfirmMatch.ResultError
            })
        }
    }
}