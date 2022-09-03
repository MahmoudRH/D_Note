package com.mahmoudrh.roomxml.domain.usecases

import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class SearchNotes constructor(private val repository: NotesRepository) {
    operator fun invoke(searchWord:String): Flow<List<Note>> {
       return repository.searchNotes(searchWord)
    }
}
