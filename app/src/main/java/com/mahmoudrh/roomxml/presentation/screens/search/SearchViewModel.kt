package com.mahmoudrh.roomxml.presentation.screens.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.usecases.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
       private val noteUseCases: NoteUseCases
       ) : ViewModel() {
    val searchWord = mutableStateOf("")
    var resultsList : List<Note> = emptyList()
    val isLoading = mutableStateOf<Boolean>(false)
    val isResultsListEmpty = mutableStateOf<Boolean>(false)

    private var searchJob:Job? = null
    fun search(){
        isLoading.value = true
        searchJob?.cancel()
        searchJob = noteUseCases.searchNotes(searchWord.value.trim()).onEach {
            resultsList = it
            isLoading.value = false
            isResultsListEmpty.value = it.isEmpty()
        }.launchIn(viewModelScope)
    }
}