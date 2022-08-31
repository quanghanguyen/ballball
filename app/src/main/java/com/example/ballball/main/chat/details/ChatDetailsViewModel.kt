package com.example.ballball.main.chat.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.ChatModel
import com.example.ballball.model.UsersModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailsViewModel @Inject constructor(private val chatDetailsRepository: ChatDetailsRepository) : ViewModel() {
    val saveChatResult = MutableLiveData<SaveChatResult>()
    val readMessageResult = MutableLiveData<ReadMessageResult>()

    sealed class SaveChatResult {
        object ResultOk : SaveChatResult()
        object ResultError : SaveChatResult()
    }

    sealed class ReadMessageResult {
        object Loading : ReadMessageResult()
        class ResultOk(val list : ArrayList<ChatModel>) : ReadMessageResult()
        object ResultError : ReadMessageResult()
    }

    fun saveChat(
        senderId : String,
        receiverId : String,
        message : String,
        time : String,
        teamAvatar : String,
        teamName : String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            chatDetailsRepository.saveChat(senderId, receiverId, message, time, teamAvatar, teamName, {
                saveChatResult.value = SaveChatResult.ResultOk
            }, {
                saveChatResult.value = SaveChatResult.ResultError
            })
        }
    }

    fun readMessage(
        senderId: String,
        receiverId: String
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            chatDetailsRepository.readMessage(senderId, receiverId, {
                readMessageResult.value = ReadMessageResult.ResultOk(it)
            }, {
                readMessageResult.value = ReadMessageResult.ResultError
            })
        }
    }
}