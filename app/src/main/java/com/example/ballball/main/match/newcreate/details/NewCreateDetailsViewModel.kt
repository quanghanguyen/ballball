package com.example.ballball.main.match.newcreate.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCreateDetailsViewModel @Inject constructor(private val newCreateDetailsRepository: NewCreateDetailsRepository) : ViewModel() {

    val deleteNewCreate = MutableLiveData<DeleteNewCreate>()

    sealed class DeleteNewCreate {
        object ResultOk: DeleteNewCreate()
        object ResultError: DeleteNewCreate()
    }

    fun deleteNewCreate(userUID: String, matchID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            newCreateDetailsRepository.deleteNewCreate(userUID, matchID, {
                deleteNewCreate.value = DeleteNewCreate.ResultOk
            }, {
                deleteNewCreate.value = DeleteNewCreate.ResultError
            })
        }
    }
}