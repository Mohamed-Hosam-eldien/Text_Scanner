package com.codingtester.textscanner.domain.use_case

import com.codingtester.textscanner.domain.model.InvalidNoteException
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.repository.NoteRepository

class AddNote(
    private val noteRepository: NoteRepository
) {

    @Throws(InvalidNoteException::class)
    suspend operator fun invoke(note: Note) {
        if(note.content.isBlank()) {
            throw InvalidNoteException()
        }
        noteRepository.insertNote(note)
    }
}