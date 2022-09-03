package com.mahmoudrh.roomxml.domain.usecases

data class NoteUseCases(
    val getAllNotes: GetAllNotes,
    val deleteNote: DeleteNote,
    val updateNote: UpdateNote,
    val searchNotes: SearchNotes,
    val deleteAllNotes: DeleteAllNotes,
    val insertNote: InsertNote
)