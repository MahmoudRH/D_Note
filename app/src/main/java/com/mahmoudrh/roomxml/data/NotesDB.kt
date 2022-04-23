package com.mahmoudrh.roomxml.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

@Database(entities = [Note::class], version = 1, exportSchema = false)
abstract class NotesDB : RoomDatabase() {

    abstract fun NoteDAO(): NoteDAO

    companion object {

        @Volatile
        private var INSTANCE: NotesDB? = null
        fun getInstance(context: Context, scope: CoroutineScope) :NotesDB{
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(context.applicationContext, NotesDB::class.java, "NotesDB")
                        .addCallback(NoteCallBack(scope))
                        .build()

                INSTANCE = instance
                instance
            }
        }
    }


    private class NoteCallBack(private val scope:CoroutineScope): RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { notesDB ->
                scope.launch {
                    populateDatabase(notesDB.NoteDAO())
                }
            }
        }

        private suspend fun populateDatabase(noteDAO: NoteDAO) {
            noteDAO.deleteAll()

            var note =Note(title = "First Note", content = "Hello 1 World Some Extra Text1", date = "1645540695000")
            Log.e("Mah ", "populateDatabase: ${Date().time.toString()}", )
            noteDAO.insertNote(note)

            note = Note(title = "Second Note", content = "Hello 2 World Some Extra Text2", date = "1646750295000")
            noteDAO.insertNote(note)

            note = Note(title = "Third Note", content = "Hello 3 World Some Extra Text3",  date = Date().time.toString())
            noteDAO.insertNote(note)

        }
    }

}