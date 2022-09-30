package com.mahmoudrh.roomxml.presentation.screens.all_notes

import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.usecases.NoteUseCases
import com.mahmoudrh.roomxml.domain.utils.OrderBy
import com.mahmoudrh.roomxml.domain.utils.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    private val application: Application
) : ViewModel() {
    private val _notesState = mutableStateOf<NotesState>(NotesState())
    val notesState: State<NotesState> = _notesState
    private var recentlyDeletedNote: Note? = null
    private var getNotesJob: Job? = null
    private val preferences = application.getSharedPreferences("OrderPreferences",Context.MODE_PRIVATE)
    val selectedNotes : MutableList<Note> = mutableListOf()
    val isSelectionModeEnabled = mutableStateOf<Boolean>(false)
    init {
        val ob = preferences.getInt("OrderBy",OrderBy.DATE)
        val ot = preferences.getInt("OrderType",OrderType.DESCENDING)
        val orderPreference = OrderBy.getOrderBy(orderType = ot, orderBy = ob)
        loadAllNotes(orderPreference)
    }
    private fun loadAllNotes(orderBy: OrderBy) {
        getNotesJob?.cancel() // cancel if already running
        getNotesJob = noteUseCases.getAllNotes(orderBy).onEach {
            _notesState.value = notesState.value.copy(
                notes = it,
                order = orderBy,
                isListLoading = false,
                isListEmpty = it.isEmpty()
            )
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: AllNotesEvent) {
        when (event) {
            is AllNotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    noteUseCases.deleteNote(event.note)
                    recentlyDeletedNote = event.note
                }
            }
            is AllNotesEvent.Order -> {
                if (notesState.value.order::class == event.order::class &&
                    notesState.value.order.orderType == event.order.orderType) { // same order and same order type
                    return
                }else {
                    loadAllNotes(event.order)
                    saveIntoSharedPreferences(event.order)
                }
            }
            AllNotesEvent.RestoreNote -> {
                recentlyDeletedNote?.let {
                    viewModelScope.launch {
                        noteUseCases.insertNote(it)
                        recentlyDeletedNote = null
                    }
                }
            }
            AllNotesEvent.ToggleOrderSection -> {
                _notesState.value = notesState.value.copy(
                    isOrderSectionVisible = !notesState.value.isOrderSectionVisible
                )
            }
            is AllNotesEvent.SelectNote -> {
                if (selectedNotes.contains(event.note)){
                    selectedNotes.remove(event.note)
                }else{
                    selectedNotes.add(event.note)
                }

               isSelectionModeEnabled.value = selectedNotes.isNotEmpty()
            }
        }
    }

    private fun saveIntoSharedPreferences(order: OrderBy) {
        val ot = OrderBy.getOrderTypeInt(order)
        val ob = OrderBy.getOrderByInt(order)
        preferences.edit().apply {
            putInt("OrderType",ot)
            putInt("OrderBy",ob)
            apply()
        }
    }
}