package com.example.ballball.main.match.confirm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfirmViewModel @Inject constructor(private val confirmRepository: ConfirmRepository) : ViewModel() {

    val loadConfirm = MutableLiveData<LoadConfirmResult>()

    sealed class LoadConfirmResult {
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadConfirmResult()
        object ResultError : LoadConfirmResult()
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
}