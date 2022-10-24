package com.mahmoudrh.roomxml.presentation.screens.all_notes

import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.utils.OrderBy

sealed class AllNotesEvent {
    data class Order(val order: OrderBy) : AllNotesEvent()
    data class DeleteNote(val note: Note) : AllNotesEvent()
    data class SelectNote(val note: Note) : AllNotesEvent()
    object DeleteSelectedNotes : AllNotesEvent()
    object DeleteAllNotes : AllNotesEvent()
    object RestoreNote : AllNotesEvent()
    object ToggleOrderSection : AllNotesEvent()
}