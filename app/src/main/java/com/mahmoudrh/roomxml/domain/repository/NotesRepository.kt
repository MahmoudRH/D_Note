package com.mahmoudrh.roomxml.domain.repository

import com.mahmoudrh.roomxml.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun insert(note: Note)

    suspend fun update(note: Note)

    suspend fun delete(note: Note)

    suspend fun deleteAll()

    fun searchNotes(searchWord: String): Flow<List<Note>>

    fun getAllNotes(): Flow<List<Note>>
}