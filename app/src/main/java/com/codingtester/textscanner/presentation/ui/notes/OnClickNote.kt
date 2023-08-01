package com.codingtester.textscanner.presentation.ui.notes

import com.codingtester.textscanner.domain.model.Note

interface OnClickNote {
    fun onClickToDelete(note: Note)
    fun onClickToSaveFile(note: Note)
}