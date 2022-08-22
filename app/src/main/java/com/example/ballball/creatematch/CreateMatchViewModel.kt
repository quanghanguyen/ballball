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
    val notification = MutableLiveData<NotificationResult>()
    val saveNewCreate = MutableLiveData<NewCreateResult>()

    sealed class LoadTeamInfo {
        object Loading : LoadTeamInfo()
        class LoadImageOk(val teamImageUrl : String) : LoadTeamInfo()
        object LoadImageError : LoadTeamInfo()
        class LoadInfoOk(val teamLocation : String, val teamPeopleNumber : String) : LoadTeamInfo()
        object LoadInfoError : LoadTeamInfo()
    }
    sealed class SaveRequest {
        object Loading : SaveRequest()
        object ResultOk : SaveRequest()
        class ResultError(val errorMessage : String) : SaveRequest()
    }
    sealed class NotificationResult {
        object ResultOk : NotificationResult()
        object ResultError : NotificationResult()
    }
    sealed class NewCreateResult {
        object ResultOk : NewCreateResult()
        object ResultError : NewCreateResult()
    }

    fun loadTeamInfo (
        userUID : String,
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            teamInformationRepository.loadTeamInfo(userUID, {
                if (it.exists()) {
                    val teamImageUrl = it.child("teamImageUrl").value.toString()
                    loadTeamInfo.value = LoadTeamInfo.LoadImageOk(teamImageUrl)
                }
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
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double,
        geoHash: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.sendRequest(userUID, matchID, deviceToken, teamName, teamPhone,
                date, time, location, note, teamPeopleNumber, teamImageUrl,locationAddress, lat, long, geoHash, {
                saveRequest.value = SaveRequest.ResultOk
            }, {
                saveRequest.value = SaveRequest.ResultError(it)
            })
        }
    }

    fun notification(
        matchID: String,
        teamName: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.notification(matchID, teamName,{
                    saveRequest.value = SaveRequest.ResultOk
                }, {
                    saveRequest.value = SaveRequest.ResultError(it)
                })
            }
        }

    fun saveNewCreate(
        userUID : String,
        matchID : String,
        deviceToken : String,
        teamName : String,
        teamPhone : String,
        date : String,
        time : String,
        location : String,
        note : String,
        teamPeopleNumber: String,
        teamImageUrl: String,
        locationAddress: String,
        lat: Double,
        long: Double
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            createMatchRepository.saveNewCreate(userUID, matchID, deviceToken, teamName, teamPhone,
                date, time, location, note, teamPeopleNumber, teamImageUrl,locationAddress, lat, long,{
                    saveNewCreate.value = NewCreateResult.ResultOk
                }, {
                    saveNewCreate.value = NewCreateResult.ResultError
                })
            }
        }
    }