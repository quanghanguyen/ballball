package com.example.ballball.main.match.newcreate

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.main.match.upcoming.UpComingRepository
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCreateViewModel @Inject constructor(private val newCreateRepository: NewCreateRepository) : ViewModel() {

    val loadNewCreate = MutableLiveData<LoadNewCreate>()

    sealed class LoadNewCreate{
        object Loading : LoadNewCreate()
        class ResultOk(val list : ArrayList<CreateMatchModel>) : LoadNewCreate()
        object ResultError : LoadNewCreate()
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
}