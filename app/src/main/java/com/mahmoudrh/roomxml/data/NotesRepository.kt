package com.mahmoudrh.roomxml.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class NotesRepository(private val noteDAO: NoteDAO) {

    val allNotes: Flow<List<Note>> = noteDAO.getAllNotes()


    fun searchNotes(searchWord: String): Flow<List<Note>> {
        return noteDAO.searchNotes(searchWord)
    }


    @WorkerThread
    suspend fun insert(note: Note) {
        noteDAO.insertNote(note)
    }

    @WorkerThread
    suspend fun update(note: Note) {
        noteDAO.updateNote(note)
    }

    @WorkerThread
    suspend fun delete(note: Note) {
        noteDAO.deleteNote(note)
    }

    @WorkerThread
    suspend fun deleteAll() {
        noteDAO.deleteAll()
    }

}