package com.codingtester.textscanner.domain.use_case

import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.repository.NoteRepository

class GetNote(
    private val noteRepository: NoteRepository
) {

    suspend operator fun invoke(id: Int): Note {
        return noteRepository.getNoteById(id)
    }
}