package com.mahmoudrh.roomxml.domain.usecases

import com.mahmoudrh.roomxml.domain.repository.NotesRepository


class GetNoteById(private val repository: NotesRepository) {

    suspend operator fun invoke(id: Int) = repository.getNoteById(id)
}