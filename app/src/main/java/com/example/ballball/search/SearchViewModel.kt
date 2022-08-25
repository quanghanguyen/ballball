package com.example.ballball.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballball.model.CreateMatchModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val searchRepository: SearchRepository) : ViewModel() {

    val searchFilter = MutableLiveData<SearchFilterResult>()

    sealed class SearchFilterResult {
        class ResultOk(val list : ArrayList<CreateMatchModel>) : SearchFilterResult()
        object ResultError : SearchFilterResult()
    }

    fun loadFilterList(userUID : String, text : String) {
        viewModelScope.launch(CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }) {
            searchRepository.filter(userUID, text, {
                searchFilter.value = SearchFilterResult.ResultOk(it)
            }, {
                searchFilter.value = SearchFilterResult.ResultError
            })
        }
    }
}