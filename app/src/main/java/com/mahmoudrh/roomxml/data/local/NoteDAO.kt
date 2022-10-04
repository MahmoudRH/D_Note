package com.mahmoudrh.roomxml.data.local

import androidx.room.*
import com.mahmoudrh.roomxml.domain.models.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {

    @Insert
    suspend fun insertNote(note: Note)

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("SELECT * FROM notes")
    fun getAllNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :searchWord || '%'")
    fun searchNotes(searchWord: String? = null): Flow<List<Note>>


}