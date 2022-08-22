package com.example.ballball.main.home.nearme

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NearMeViewModel @Inject constructor(private val nearMeRepository: NearMeRepository) : ViewModel() {

    val loadNearMe = MutableLiveData<LoadNearMeResult>()

    sealed class LoadNearMeResult {
        class ResultOk(val list: ArrayList<CreateMatchModel>) : LoadNearMeResult()
        object ResultError : LoadNearMeResult()
    }

    fun loadNearMeList(
        currentLat : Double,
        currentLong : Double
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            nearMeRepository.loadNearMe(currentLat, currentLong, {
                loadNearMe.value = LoadNearMeResult.ResultOk(it)
            }, {
                loadNearMe.value = LoadNearMeResult.ResultError
            })
        }
    }
}