package com.example.ballball.listnotification

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.ListNotificationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListNotificationViewModel @Inject constructor(private val listNotificationRepository: ListNotificationRepository) : ViewModel() {

    val loadListNotification = MutableLiveData<LoadListNotificationResult>()

    sealed class LoadListNotificationResult {
        class ResultOk(val list : ArrayList<ListNotificationModel>) : LoadListNotificationResult()
        object ResultError : LoadListNotificationResult()
    }

    fun loadListNotification(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            listNotificationRepository.loadNotificationList(userUID, {
                loadListNotification.value = LoadListNotificationResult.ResultOk(it)
            }, {
                loadListNotification.value = LoadListNotificationResult.ResultError
            })
        }
    }
}