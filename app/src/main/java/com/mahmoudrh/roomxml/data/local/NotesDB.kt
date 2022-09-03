package com.mahmoudrh.roomxml.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mahmoudrh.roomxml.domain.models.Note

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDB : RoomDatabase() {
    abstract val noteDAO: NoteDAO

    companion object{
        const val DATABASE_NAME = "NotesDB"
    }
}