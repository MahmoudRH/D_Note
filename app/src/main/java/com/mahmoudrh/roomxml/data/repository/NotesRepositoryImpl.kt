package com.mahmoudrh.roomxml.data.repository

import com.mahmoudrh.roomxml.data.local.NoteDAO
import com.mahmoudrh.roomxml.domain.models.Note
import com.mahmoudrh.roomxml.domain.repository.NotesRepository
import kotlinx.coroutines.flow.Flow

class NotesRepositoryImpl(private val dao: NoteDAO) : NotesRepository {

    override suspend fun insert(note: Note) {
        dao.insertNote(note)
    }

    override suspend fun update(note: Note) {
        dao.updateNote(note)
    }

    override suspend fun delete(note: Note) {
        dao.deleteNote(note)
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override fun searchNotes(searchWord: String): Flow<List<Note>> {
        return dao.searchNotes(searchWord)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return dao.getAllNotes()
    }
}