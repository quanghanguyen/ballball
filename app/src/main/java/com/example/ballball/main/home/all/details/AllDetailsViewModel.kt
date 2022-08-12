package com.example.ballball.main.home.all.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import javax.inject.Inject

@HiltViewModel
class AllDetailsViewModel @Inject constructor(private val allDetailsRepository: AllDetailsRepository) : ViewModel()
{
    val catchMatch = MutableLiveData<CatchMatch>()

    sealed class CatchMatch {
        object ResultOk : CatchMatch()
        object ResultError : CatchMatch()
        object WaitMatchOk : CatchMatch()
        object WaitMatchError : CatchMatch()
        object WaitMatchNotificationOk : CatchMatch()
        object WaitMatchNotificationError : CatchMatch()
        object ConfirmMatchOk : CatchMatch()
        object ConfirmMatchError : CatchMatch()
    }

    fun handleCatchMatch(
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int,
        clientTeamName : String, clientUID : String, clientImageUrl: String, teamConfirmUID : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.catchMatch(matchID, userUID, clientUID, click, {
                catchMatch.value = CatchMatch.ResultOk
            }, {
                catchMatch.value = CatchMatch.ResultError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.waitMatch(userUID, matchID, deviceToken, teamName, teamPhone, date, time, location,
                note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName,
                teamConfirmUID, {
                    catchMatch.value = CatchMatch.WaitMatchOk
                }, {
                    catchMatch.value = CatchMatch.WaitMatchError
                })
            }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.waitMatchNotification(userUID, matchID, date, time, clientTeamName, {
                catchMatch.value = CatchMatch.WaitMatchNotificationOk
            }, {
                catchMatch.value = CatchMatch.WaitMatchNotificationError
            })
        }

        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.confirmMatch(teamConfirmUID, matchID, deviceToken, teamName, teamPhone, date,
                time, location, note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click,
                clientTeamName, clientImageUrl, {
                    catchMatch.value = CatchMatch.ConfirmMatchOk
                }, {
                    catchMatch.value = CatchMatch.ConfirmMatchError
                })
            }
        }
    }