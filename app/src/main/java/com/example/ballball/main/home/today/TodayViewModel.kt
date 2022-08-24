package com.example.ballball.main.home.today

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.home.all.AllViewModel
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TodayViewModel @Inject constructor(private val todayRepository: TodayRepository) : ViewModel(){
    val loadTodayList = MutableLiveData<LoadTodayList>()

    sealed class LoadTodayList {
        object Loading : LoadTodayList()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadTodayList()
        class ResultError(val errorMessage : String) : LoadTodayList()
    }

    fun loadToday(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            todayRepository.loadMatchList(userUID, {
                loadTodayList.value = LoadTodayList.ResultOk(it)
            }, {
                loadTodayList.value = LoadTodayList.ResultError(it)
            })
        }
    }
}