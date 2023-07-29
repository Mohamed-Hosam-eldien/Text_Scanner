package com.codingtester.textscanner.domain.use_case

import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class GetAllNotes(
    private val noteRepository: NoteRepository
) {
    operator fun invoke(): Flow<List<Note>> {
        return noteRepository.getAllNotes()
    }
}