package com.example.ballball.main.match.confirm

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
class ConfirmViewModel @Inject constructor(private val confirmRepository: ConfirmRepository) : ViewModel() {

    val loadConfirm = MutableLiveData<LoadConfirmResult>()
    val highLight = MutableLiveData<HighLightResult>()

    sealed class LoadConfirmResult {
        object Loading : LoadConfirmResult()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadConfirmResult()
        object ResultError : LoadConfirmResult()
    }

    sealed class HighLightResult {
        object HighLightOk: HighLightResult()
        object HighLightError: HighLightResult()
        object NotHighLightOk: HighLightResult()
        object NotHighLightError: HighLightResult()
    }

    fun loadConfirmList(userUID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmRepository.loadConfirmList(userUID, {
                loadConfirm.value = LoadConfirmResult.ResultOk(it)
            }, {
                loadConfirm.value = LoadConfirmResult.ResultError
            })
        }
    }

    fun handleHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            confirmRepository.highlight(userUID, matchID, {
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
            confirmRepository.notHighLight(userUID, matchID, {
                highLight.value = HighLightResult.NotHighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }
}