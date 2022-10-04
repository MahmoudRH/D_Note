package com.mahmoudrh.roomxml.presentation.screens.note

sealed class NoteEvent {
    object InsertNote : NoteEvent()
    object UpdateNote : NoteEvent()
    object ToggleEditMode : NoteEvent()
}