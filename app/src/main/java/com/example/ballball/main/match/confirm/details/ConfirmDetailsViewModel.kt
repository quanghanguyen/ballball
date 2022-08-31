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
    val denyRequestListNotification = MutableLiveData<DenyRequestListNotification>()
    val confirmRequestListNotification = MutableLiveData<ConfirmRequestListNotification>()

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
        object DeleteMatchOk: AcceptMatch()
        object DeleteMatchError: AcceptMatch()
        object DeleteNewCreateOk: AcceptMatch()
        object DeleteNewCreateError: AcceptMatch()
        object AcceptMatchNotificationOk: AcceptMatch()
        object AcceptMatchNotificationError: AcceptMatch()
        object DenyMatchNotificationOk: AcceptMatch()
        object DenyMatchNotificationError: AcceptMatch()
    }

    sealed class UpComingClientResult {
        object SaveUpComingClientOk: UpComingClientResult()
        object SaveUpComingClientError: UpComingClientResult()
    }

    sealed class DenyRequestListNotification {
        object ResultOk: DenyRequestListNotification()
        object ResultError: DenyRequestListNotification()
    }

    sealed class ConfirmRequestListNotification {
        object ResultOk : ConfirmRequestListNotification()
        object ResultError : ConfirmRequestListNotification()
    }

    fun denyConfirmMatch(
        userUID: String,
        matchId: String,
        confirmUID: String,
        clientUID : String,
        date: String,
        time: String,
        teamName: String
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

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.denyRequestNotification(clientUID, userUID, matchId, date, time, teamName, {
                acceptMatch.value = AcceptMatch.DenyMatchNotificationOk
            }, {
                acceptMatch.value = AcceptMatch.DenyMatchNotificationError
            })
        }
    }

    fun acceptMatch(
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID : String, geoHash : String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.upComingMatch(userUID, matchID, deviceToken, teamName, teamPhone, date,
            time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName,
            clientImageUrl, confirmUID, clientUID, geoHash, {
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
            confirmDetailsRepository.deleteMatch(matchID, {
                acceptMatch.value = AcceptMatch.DeleteMatchOk
            }, {
                acceptMatch.value = AcceptMatch.DeleteMatchError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.deleteNewCreate(userUID, matchID, {
                acceptMatch.value = AcceptMatch.DeleteNewCreateOk
            }, {
                acceptMatch.value = AcceptMatch.DeleteNewCreateError
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
        clientTeamName : String, clientImageUrl : String, confirmUID: String, clientUID : String, geoHash : String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmDetailsRepository.upComingMatchClient(confirmUID, userUID, matchID, deviceToken, teamName, teamPhone, date,
                time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
                clientTeamName, clientImageUrl, clientUID, geoHash, {
                    saveUpComingClient.value = UpComingClientResult.SaveUpComingClientOk
                }, {
                    saveUpComingClient.value = UpComingClientResult.SaveUpComingClientError
                })
            }
        }

    fun denyRequestListNotification(
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
            confirmDetailsRepository.denyRequestListNotifications(clientUID, clientTeamName, clientImageUrl, id, date, time, timeUtils, {
                denyRequestListNotification.value = DenyRequestListNotification.ResultOk
            }, {
                denyRequestListNotification.value = DenyRequestListNotification.ResultError
            })
        }
    }

    fun confirmRequestListNotification(
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
            confirmDetailsRepository.acceptRequestListNotification(clientUID, clientTeamName, clientImageUrl, id, date, time, timeUtils, {
                confirmRequestListNotification.value = ConfirmRequestListNotification.ResultOk
            }, {
                confirmRequestListNotification.value = ConfirmRequestListNotification.ResultError
            })
        }
    }
}