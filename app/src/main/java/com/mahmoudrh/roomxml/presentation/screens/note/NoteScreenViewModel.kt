package com.mahmoudrh.roomxml.presentation.screens.note

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.usecases.NoteUseCases
import com.mahmoudrh.roomxml.presentation.screens.navArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    handle: SavedStateHandle,
) : ViewModel() {
    val noteTitle = mutableStateOf("")
    val noteContent = mutableStateOf("")
    val note = handle.navArgs<NoteNavArgs>().note
    val isTitleError = mutableStateOf(false)
    val isContentError = mutableStateOf(false)
    val isEventSuccess = mutableStateOf(false)
    val eventName = mutableStateOf("Adding New Note")

    init {
        note?.let {
            eventName.value = "Editing Note"
            noteTitle.value = it.title
            noteContent.value = it.content
        }
    }

    fun onEvent(event: NoteEvent) {
        when (event) {
            is NoteEvent.InsertNote -> {
                viewModelScope.launch {
                    isTitleError.value = isTitleEmpty()
                    isContentError.value = isContentEmpty()
                    if (isTitleEmpty() || isContentEmpty()) {
                        return@launch
                    } else {
                        noteUseCases.insertNote(
                            Note(
                                title = noteTitle.value,
                                content = noteContent.value,
                                date = Date().time.toString(),
                            )
                        )
                        isEventSuccess.value = true
                    }

                }
            }
            is NoteEvent.UpdateNote -> {
                viewModelScope.launch {
                    isTitleError.value = isTitleEmpty()
                    isContentError.value = isContentEmpty()
                    if (isTitleEmpty() || isContentEmpty()) {
                        return@launch
                    } else {
                        note?.let {
                            noteUseCases.updateNote(
                                it.copy(
                                    title = noteTitle.value,
                                    content = noteContent.value,
                                )
                            )
                            isEventSuccess.value = true
                        }
                    }
                }
            }
        }

    }

    private fun isTitleEmpty() = noteTitle.value.isEmpty()
    private fun isContentEmpty() = noteContent.value.isEmpty()
    fun isUpdatingNote() =
        note != null //if the object passed isn't null, the user is updating an existing note.

}