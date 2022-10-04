package com.mahmoudrh.roomxml.domain.usecases

import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.repository.NotesRepository

class DeleteNote(private val repository: NotesRepository) {
    suspend operator fun invoke(note: Note) {
        repository.delete(note)
    }
}