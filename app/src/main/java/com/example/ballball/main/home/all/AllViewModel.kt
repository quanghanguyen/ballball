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

    sealed class LoadAllList {
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadAllList()
        class ResultError(val errorMessage : String) : LoadAllList()
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
}