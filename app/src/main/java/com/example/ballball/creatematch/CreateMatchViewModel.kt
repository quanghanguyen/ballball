package com.example.ballball.creatematch

import android.graphics.Bitmap
import android.service.autofill.SaveRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.user.teaminformation.TeamInformationRepository
import com.example.ballball.user.walkthrough.team.TeamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CreateMatchViewModel @Inject constructor(
    private val teamInformationRepository: TeamInformationRepository,
    private val createMatchRepository: CreateMatchRepository
) : ViewModel() {

    val loadTeamInfo = MutableLiveData<LoadTeamInfo>()
    val saveRequest = MutableLiveData<SaveRequest>()

    sealed class LoadTeamInfo {
        object Loading : LoadTeamInfo()
        class LoadImageOk(val image : Bitmap) : LoadTeamInfo()
        object LoadImageError : LoadTeamInfo()
        class LoadInfoOk(val teamLocation : String, val teamPeopleNumber : String) : LoadTeamInfo()
        object LoadInfoError : LoadTeamInfo()
    }
    sealed class SaveRequest {
        object Loading : SaveRequest()
        object ResultOk : SaveRequest()
        class ResultError(val errorMessage : String) : SaveRequest()
    }

    fun loadTeamInfo (
        userUID : String,
        localFile : File
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamInformationRepository.loadTeamImage(userUID, localFile, {
                loadTeamInfo.value = LoadTeamInfo.LoadImageOk(it)
            }, {
                loadTeamInfo.value = LoadTeamInfo.LoadImageError
            })
            teamInformationRepository.loadTeamInfo(userUID, {
                if (it.exists()) {
                    val teamLocation = it.child("teamLocation").value.toString()
                    val teamPeopleNumber = it.child("teamPeopleNumber").value.toString()
                    loadTeamInfo.value = LoadTeamInfo.LoadInfoOk(teamLocation, teamPeopleNumber)
                }
            }, {
                loadTeamInfo.value = LoadTeamInfo.LoadInfoError
            })
        }
    }

    fun saveRequest(
        userUID : String,
        matchID : String,
        deviceToken : String,
        teamName : String,
        teamPhone : String,
        date : String,
        time : String,
        location : String,
        note : String,
        teamPeopleNumber: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.sendRequest(userUID, matchID, deviceToken, teamName, teamPhone, date, time, location, note, teamPeopleNumber, {
                saveRequest.value = SaveRequest.ResultOk
            }, {
                saveRequest.value = SaveRequest.ResultError(it)
            })
        }
    }
    }