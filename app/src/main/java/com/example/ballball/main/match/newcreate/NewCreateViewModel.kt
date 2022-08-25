package com.example.ballball.main.match.newcreate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.upcoming.UpComingRepository
import com.example.ballball.main.match.upcoming.UpComingViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCreateViewModel @Inject constructor(private val newCreateRepository: NewCreateRepository) : ViewModel() {

    val loadNewCreate = MutableLiveData<LoadNewCreate>()
    val highLight = MutableLiveData<HighLightResult>()

    sealed class LoadNewCreate{
        object Loading : LoadNewCreate()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadNewCreate()
        object ResultError : LoadNewCreate()
    }

    sealed class HighLightResult {
        object HighLightOk: HighLightResult()
        object HighLightError: HighLightResult()
        object NotHighLightOk: HighLightResult()
        object NotHighLightError: HighLightResult()
    }

    fun loadNewCreate(userUID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            newCreateRepository.loadNewCreate(userUID, {
                loadNewCreate.value = LoadNewCreate.ResultOk(it)
            }, {
                loadNewCreate.value = LoadNewCreate.ResultError
            })
        }
    }

    fun handleHighLight(userUID: String, matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            newCreateRepository.highlight(userUID, matchID, {
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
            newCreateRepository.notHighLight(userUID, matchID, {
                highLight.value = HighLightResult.NotHighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }
}