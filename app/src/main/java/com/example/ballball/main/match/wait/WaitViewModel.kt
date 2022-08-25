package com.example.ballball.main.match.wait

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.upcoming.UpComingViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitViewModel @Inject constructor(private val waitRepository: WaitRepository) : ViewModel() {

    val loadWait = MutableLiveData<LoadWaitResult>()
    val highLight = MutableLiveData<HighLightResult>()

    sealed class LoadWaitResult {
        object Loading : LoadWaitResult()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadWaitResult()
        object ResultError : LoadWaitResult()
    }

    sealed class HighLightResult {
        object HighLightOk: HighLightResult()
        object HighLightError: HighLightResult()
        object NotHighLightOk: HighLightResult()
        object NotHighLightError: HighLightResult()
    }

    fun loadWaitList(userUID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitRepository.loadWaitList(userUID, {
                loadWait.value = LoadWaitResult.ResultOk(it)
            }, {
                loadWait.value = LoadWaitResult.ResultError
            })
        }
    }

    fun handleHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitRepository.highlight(userUID, matchID, {
                highLight.value = HighLightResult.HighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }

    fun handleNotHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            waitRepository.notHighLight(userUID, matchID, {
                highLight.value = HighLightResult.NotHighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }
}