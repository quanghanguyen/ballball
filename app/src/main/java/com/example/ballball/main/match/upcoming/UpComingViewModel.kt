package com.example.ballball.main.match.upcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.confirm.ConfirmViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpComingViewModel @Inject constructor(private val upComingRepository: UpComingRepository) : ViewModel() {

    val loadUpComing = MutableLiveData<LoadUpComingResult>()

    sealed class LoadUpComingResult {
        object Loading : LoadUpComingResult()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadUpComingResult()
        object ResultError : LoadUpComingResult()
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
}