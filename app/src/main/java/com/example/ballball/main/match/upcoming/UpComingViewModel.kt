package com.example.ballball.main.match.upcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.home.all.AllViewModel
import com.example.ballball.main.match.confirm.ConfirmViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpComingViewModel @Inject constructor(private val upComingRepository: UpComingRepository) : ViewModel() {

    val loadUpComing = MutableLiveData<LoadUpComingResult>()
    val highLight = MutableLiveData<HighLightResult>()

    sealed class LoadUpComingResult {
        object Loading : LoadUpComingResult()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadUpComingResult()
        object ResultError : LoadUpComingResult()
    }

    sealed class HighLightResult {
        object HighLightOk: HighLightResult()
        object HighLightError: HighLightResult()
        object NotHighLightOk: HighLightResult()
        object NotHighLightError: HighLightResult()
    }

    fun loadUpComingList(userUID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            upComingRepository.loadUpComingList(userUID, {
                loadUpComing.value = LoadUpComingResult.ResultOk(it)
            }, {
                loadUpComing.value = LoadUpComingResult.ResultError
            })
        }
    }

    fun handleHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            upComingRepository.highlight(userUID, matchID, {
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
            upComingRepository.notHighLight(userUID, matchID, {
                highLight.value = HighLightResult.NotHighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }
}