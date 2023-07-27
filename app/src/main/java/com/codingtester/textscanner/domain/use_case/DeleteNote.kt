package com.codingtester.textscanner.domain.use_case

import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.repository.NoteRepository

class DeleteNote(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(note: Note) {
        return noteRepository.deleteNote(note)
    }
}