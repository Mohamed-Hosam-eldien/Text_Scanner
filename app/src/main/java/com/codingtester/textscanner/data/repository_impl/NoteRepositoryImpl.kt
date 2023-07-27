package com.codingtester.textscanner.data.repository_impl

import com.codingtester.textscanner.data.data_source.NoteDao
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class NoteRepositoryImpl(
    private val noteDao: NoteDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    override suspend fun getNoteById(id: Int): Note = noteDao.getNoteById(id)

    override suspend fun insertNote(note: Note) = noteDao.insertNote(note)

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)
}