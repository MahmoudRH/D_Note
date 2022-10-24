package com.mahmoudrh.roomxml.di

import android.app.Application
import androidx.room.Room
import com.mahmoudrh.roomxml.data.local.NotesDB
import com.mahmoudrh.roomxml.data.repository.NotesRepositoryImpl
import com.mahmoudrh.roomxml.domain.repository.NotesRepository
import com.mahmoudrh.roomxml.domain.usecases.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): NotesDB {
        return Room.databaseBuilder(app, NotesDB::class.java, NotesDB.DATABASE_NAME).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(database: NotesDB): NotesRepository {
        return NotesRepositoryImpl(database.noteDAO)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NotesRepository): NoteUseCases {
        return NoteUseCases(
            getAllNotes = GetAllNotes(repository),
            deleteNote = DeleteNote(repository),
            updateNote = UpdateNote(repository),
            searchNotes = SearchNotes(repository),
            deleteAllNotes = DeleteAllNotes(repository),
            insertNote = InsertNote(repository)
        )
    }
}