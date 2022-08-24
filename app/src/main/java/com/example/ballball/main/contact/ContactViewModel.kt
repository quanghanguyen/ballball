package com.example.ballball.main.contact

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import com.example.ballball.model.NewContactModel
import com.example.ballball.model.UsersModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel @Inject constructor(private val contactRepository: ContactRepository) : ViewModel() {
    val loadContactList = MutableLiveData<LoadContactList>()
    val saveNewContact = MutableLiveData<SaveNewContact>()
    val loadNewContactList = MutableLiveData<LoadNewContactList>()

    sealed class LoadContactList {
        object Loading : LoadContactList()
        class ResultOk(val list : ArrayList<UsersModel>) : LoadContactList()
        object ResultError : LoadContactList()
    }

    sealed class SaveNewContact {
        object ResultOk : SaveNewContact()
        object ResultError : SaveNewContact()
    }

    sealed class LoadNewContactList {
        class ResultOk(val list : ArrayList<NewContactModel>) : LoadNewContactList()
        object ResultError : LoadNewContactList()
    }

    fun loadContactList(userUID : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            contactRepository.loadContactList(userUID, {
                loadContactList.value = LoadContactList.ResultOk(it)
            }, {
                loadContactList.value = LoadContactList.ResultError
            })
        }
    }

    fun saveNewContact(userUID: String, name: String, phoneNumber : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            contactRepository.saveNewContact(userUID, name, phoneNumber, {
                saveNewContact.value = SaveNewContact.ResultOk
            }, {
                saveNewContact.value = SaveNewContact.ResultError
            })
        }
    }

    fun loadNewContactList(userUID: String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            contactRepository.loadNewContactList(userUID, {
                loadNewContactList.value = LoadNewContactList.ResultOk(it)
            }, {
                loadNewContactList.value = LoadNewContactList.ResultError
            })
        }
    }
}