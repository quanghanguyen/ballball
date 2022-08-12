package com.example.ballball.main.match.upcoming

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UpComingViewModel @Inject constructor(private val upComingRepository: UpComingRepository) : ViewModel() {
//    val loadUpComing = MutableLiveData<LoadUpComing>()
//    sealed class LoadUpComing{
//        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadUpComing()
//        object ResultError : LoadUpComing()
//    }
//
//    fun loadUpComing(userUID: String) {
//        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
//            throwable.printStackTrace()
//        }) {
//            upComingRepository.loadUpComing(userUID, {
//                loadUpComing.value = LoadUpComing.ResultOk(it)
//            }, {
//                loadUpComing.value = LoadUpComing.ResultError
//            })
//        }
//    }
}