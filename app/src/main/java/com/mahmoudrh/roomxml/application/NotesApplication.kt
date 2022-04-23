package com.mahmoudrh.roomxml.application

import android.app.Application
import com.mahmoudrh.roomxml.data.NotesDB
import com.mahmoudrh.roomxml.data.NotesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class NotesApplication: Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    val database by lazy { NotesDB.getInstance(this,applicationScope) }
    val repository by lazy { NotesRepository(database.NoteDAO()) }
}