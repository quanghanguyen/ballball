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
    val saveUpComingClient = MutableLiveData<UpComingClientResult>()

    sealed class DenyConfirmMatch {
        object ResultOk : DenyConfirmMatch()
        object ResultError : DenyConfirmMatch()
    }

    sealed class AcceptMatch {
        object DeleteConfirmOk: AcceptMatch()
        object DeleteConfirmError : AcceptMatch()
        object SaveUpComingOk : AcceptMatch()
        object SaveUpComingError : AcceptMatch()
        object DeleteWaitOk: AcceptMatch()
        object DeleteWaitError: AcceptMatch()
        object AcceptMatchNotificationOk: AcceptMatch()
        object AcceptMatchNotificationError: AcceptMatch()
    }

    sealed class UpComingClientResult {
        object SaveUpComingClientOk: UpComingClientResult()
        object SaveUpComingClientError: UpComingClientResult()
    }

    fun denyConfirmMatch(
        userUID: String,
        matchId: String,
        confirmUID: String
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

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.deleteWaitMatch(confirmUID, matchId, {
                acceptMatch.value = AcceptMatch.DeleteWaitOk
            }, {
                acceptMatch.value = AcceptMatch.DeleteWaitError
            })
        }
    }

    fun acceptMatch(
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.upComingMatch(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName,
            clientImageUrl, confirmUID, clientUID, {
                    acceptMatch.value = AcceptMatch.SaveUpComingOk
                }, {
                    acceptMatch.value = AcceptMatch.SaveUpComingError
                })
            }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.deleteConfirmMatch(userUID, matchID, {
                acceptMatch.value = AcceptMatch.DeleteConfirmOk
            }, {
                acceptMatch.value = AcceptMatch.DeleteConfirmError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.deleteWaitMatch(confirmUID, matchID, {
                acceptMatch.value = AcceptMatch.DeleteWaitOk
            }, {
                acceptMatch.value = AcceptMatch.DeleteWaitError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.acceptRequestNotification(confirmUID, userUID, matchID, date, time, teamName, {
                acceptMatch.value = AcceptMatch.AcceptMatchNotificationOk
            }, {
                acceptMatch.value = AcceptMatch.AcceptMatchNotificationError
            })
        }
    }

    fun saveUpComingClient(
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.upComingMatchClient(confirmUID, userUID, matchID, deviceToken, teamName, teamPhone, date,
                time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
                clientTeamName, clientImageUrl, clientUID, {
                    saveUpComingClient.value = UpComingClientResult.SaveUpComingClientOk
                }, {
                    saveUpComingClient.value = UpComingClientResult.SaveUpComingClientError
                })
            }
        }
    }