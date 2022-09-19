package com.example.ballball.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val historyRepository: HistoryRepository) : ViewModel() {
    val loadHistoryMatch = MutableLiveData<LoadHistoryMatch>()
    val highlight = MutableLiveData<HighLight>()

    sealed class LoadHistoryMatch {
        object Loading : LoadHistoryMatch()
        class ResultOk (val historyList : ArrayList<CreateMatchModel>) : LoadHistoryMatch()
        object ResultError : LoadHistoryMatch()
    }

    sealed class HighLight {
        object HighLightOk: HighLight()
        object HighLightError: HighLight()
        object NotHighLightOk: HighLight()
        object NotHighLightError: HighLight()
    }

    fun loadHistoryMatch(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            historyRepository.loadHistoryMatch(userUID, {
                loadHistoryMatch.value = LoadHistoryMatch.ResultOk(it)
            }, {
                loadHistoryMatch.value = LoadHistoryMatch.ResultError
            })
        }
    }

    fun highLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            historyRepository.highlight(userUID, matchID, {
                highlight.value = HighLight.HighLightOk
            }, {
                highlight.value = HighLight.HighLightError
            })
        }
    }

    fun notHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            historyRepository.notHighLight(userUID, matchID, {
                highlight.value = HighLight.NotHighLightOk
            }, {
                highlight.value = HighLight.NotHighLightError
            })
        }
    }
}