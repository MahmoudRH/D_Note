package com.mahmoudrh.roomxml.presentation.screens.note

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mahmoudrh.roomxml.R
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.usecases.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.*
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
open class NoteViewModel @Inject constructor(
    private val noteUseCases: NoteUseCases,
    handle: SavedStateHandle,
) : ViewModel() {
    val noteTitle = mutableStateOf("")
    val noteContent = mutableStateOf("")
    var note: Note? = null
    val isTitleError = mutableStateOf(false)
    val isContentError = mutableStateOf(false)
    val isEventSuccess = mutableStateOf(false)
    val eventName = mutableIntStateOf(R.string.adding_new_note)
    val canDoAction = mutableStateOf<Boolean>(false)
    val isEditModeEnabled = mutableStateOf<Boolean>(true)

    init {
        handle.get<String>("noteId")?.let { noteId ->
            if (noteId != "null") {
                viewModelScope.launch {
                    noteUseCases.getNoteById(noteId.toInt())?.also { fetchedNote ->
                        note = fetchedNote
                        noteTitle.value = fetchedNote.title
                        noteContent.value = fetchedNote.content
                        eventName.intValue = R.string.viewing_note
                        isEditModeEnabled.value = false
                        canDoAction.value = true
                    }
                }
            }
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
                eventName.intValue = if (isEditModeEnabled.value) R.string.editing_note else R.string.viewing_note
            }
        }
    }

    private fun isOriginalNoteModified(ifNoOriginalNote: Boolean) {
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
        note != null // if the object passed isn't null, the user is updating an existing note.
}