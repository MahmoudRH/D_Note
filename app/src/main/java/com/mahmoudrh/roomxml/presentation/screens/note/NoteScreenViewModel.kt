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
) : ViewModel()
{
    val noteTitle = mutableStateOf("")
    val noteContent = mutableStateOf("")
    val note = handle.navArgs<NoteNavArgs>().note
    val isTitleError = mutableStateOf(false)
    val isContentError = mutableStateOf(false)
    val isEventSuccess = mutableStateOf(false)
    val eventName = mutableStateOf("Adding New Note")
    val canDoAction = mutableStateOf<Boolean>(false)
    val isEditModeEnabled = mutableStateOf<Boolean>(true)
    init {
        note?.let {
            eventName.value = "Viewing Note"
            noteTitle.value = it.title
            noteContent.value = it.content
            isEditModeEnabled.value = false
            canDoAction.value = true
        }
    }

    fun onTitleChange(newValue: String) {
        noteTitle.value = newValue
        isTitleEmpty()
    }

    fun onContentChange(newValue: String) {
        noteContent.value = newValue
        isContentEmpty()
    }


    fun onEvent(event: NoteEvent) {
        checkEmptyFields()
        when (event) {
            is NoteEvent.InsertNote -> {
                viewModelScope.launch {
                    if (isTitleEmpty() || isContentEmpty()) {
                        return@launch
                    } else {
                        noteUseCases.insertNote(
                            Note(
                                title = noteTitle.value.trim(),
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
                    if (isTitleEmpty() || isContentEmpty()) {
                        return@launch
                    } else {
                        note?.let {
                            noteUseCases.updateNote(
                                it.copy(
                                    title = noteTitle.value.trim(),
                                    content = noteContent.value,
                                )
                            )
                            isEventSuccess.value = true
                        }
                    }
                }
            }
            NoteEvent.ToggleEditMode -> {
                isEditModeEnabled.value = !isEditModeEnabled.value
                eventName.value = if (isEditModeEnabled.value) "Editing Note" else  "Viewing Note"
            }
        }
    }


    private fun isOriginalNoteModified(ifNoOriginalNote:Boolean) {
        canDoAction.value = note?.let {
            noteTitle.value != it.title || noteContent.value != it.content
        } ?: ifNoOriginalNote
    }
    private fun isTitleEmpty(): Boolean {
        val isEmpty = noteTitle.value.trim().isEmpty()
        isTitleError.value = isEmpty
        isOriginalNoteModified(!isEmpty)
        return isEmpty
    }
    private fun isContentEmpty(): Boolean {
        val isEmpty = noteContent.value.trim().isEmpty()
        isContentError.value = isEmpty
        isOriginalNoteModified(!isEmpty)
        return isEmpty
    }
    private fun checkEmptyFields() {
        isTitleError.value = isTitleEmpty()
        isContentError.value = isContentEmpty()
    }

    fun isUpdatingNote() =
        note != null //if the object passed isn't null, the user is updating an existing note.

}