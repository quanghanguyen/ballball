package com.example.ballball.main.match.wait

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaitViewModel @Inject constructor(private val waitRepository: WaitRepository) : ViewModel() {
    val loadWait = MutableLiveData<LoadWaitResult>()

    sealed class LoadWaitResult {
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadWaitResult()
        object ResultError : LoadWaitResult()
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
}