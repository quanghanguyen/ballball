package com.example.ballball.main.home.all

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllViewModel @Inject constructor(private val allRepository: AllRepository) : ViewModel() {
    val loadAllList = MutableLiveData<LoadAllList>()
    val highLight = MutableLiveData<HighLightResult>()

    sealed class LoadAllList {
        object Loading : LoadAllList()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadAllList()
        class ResultError(val errorMessage : String) : LoadAllList()
    }

    sealed class HighLightResult {
        object HighLightOk: HighLightResult()
        object HighLightError: HighLightResult()
        object NotHighLightOk: HighLightResult()
        object NotHighLightError: HighLightResult()
    }

    fun loadAll(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allRepository.loadMatchList(userUID, {
                loadAllList.value = LoadAllList.ResultOk(it)
            }, {
                loadAllList.value = LoadAllList.ResultError(it)
            })
        }
    }

    fun handleHighLight(matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allRepository.highlight(matchID, {
                highLight.value = HighLightResult.HighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }

    fun handleNotHighLight(matchID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            allRepository.notHighLight(matchID, {
                highLight.value = HighLightResult.NotHighLightOk
            }, {
                highLight.value = HighLightResult.HighLightError
            })
        }
    }
}