package com.mahmoudrh.roomxml.domain.usecases

import com.mahmoudrh.roomxml.domain.repository.NotesRepository

class DeleteAllNotes constructor(private val repository: NotesRepository) {
    suspend operator fun invoke(){
        repository.deleteAll()
    }
}