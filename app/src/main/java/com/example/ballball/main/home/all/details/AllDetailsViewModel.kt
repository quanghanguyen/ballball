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
    val waitMatch = MutableLiveData<WaitMatch>()

    sealed class CatchMatch {
        object ResultOk : CatchMatch()
        object ResultError : CatchMatch()
    }

    sealed class WaitMatch {
        object ResultOk : WaitMatch()
        object ResultError : WaitMatch()
    }

    fun handleCatchMatch(
        matchId : String,
        uid : String,
        clientUid : String,
        click : Int
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.catchMatch(matchId, uid, clientUid, click, {
                catchMatch.value = CatchMatch.ResultOk
            }, {
                catchMatch.value = CatchMatch.ResultError
            })
        }
    }

    fun saveWaitMatch (
        userUID : String, matchID : String, deviceToken : String, teamName: String, teamPhone: String,
        date : String, time : String, location : String, note : String, teamPeopleNumber: String,
        teamImageUrl : String, locationAddress : String, lat : Double, long : Double, click : Int, clientTeamName : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allDetailsRepository.waitMatch(userUID, matchID, deviceToken, teamName, teamPhone, date, time, location,
                note, teamPeopleNumber, teamImageUrl, locationAddress, lat, long, click, clientTeamName, {
                waitMatch.value = WaitMatch.ResultOk
            }, {
                waitMatch.value = WaitMatch.ResultError
            })
        }
    }
}