package com.codingtester.textscanner.data.di

import android.app.Application
import androidx.room.Room
import com.codingtester.textscanner.data.data_source.NoteDatabase
import com.codingtester.textscanner.data.repository_impl.NoteRepositoryImpl
import com.codingtester.textscanner.domain.repository.NoteRepository
import com.codingtester.textscanner.domain.use_case.AddNote
import com.codingtester.textscanner.domain.use_case.DeleteNote
import com.codingtester.textscanner.domain.use_case.GetAllNotes
import com.codingtester.textscanner.domain.use_case.GetNote
import com.codingtester.textscanner.domain.use_case.NoteUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun provideDatabase(app: Application): NoteDatabase {
        return Room.databaseBuilder(
            app,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()
    }
    @Singleton
    @Provides
    fun provideDataRepository(
        noteDatabase: NoteDatabase
    ): NoteRepository {
        return NoteRepositoryImpl(noteDatabase.noteDao())
    }
    @Singleton
    @Provides
    fun provideUseCase(
        repository: NoteRepository
    ): NoteUseCases {
        return NoteUseCases(
            addNote = AddNote(repository),
            getNote = GetNote(repository),
            getAllNotes = GetAllNotes(repository),
            deleteNote = DeleteNote(repository)
        )
    }
}