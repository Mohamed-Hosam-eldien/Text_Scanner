package com.codingtester.textscanner.presentation.ui.main

import com.codingtester.textscanner.domain.model.Note

interface OnClickNote {

    fun onClickToDelete(note: Note)

    fun onClickToSaveFile(note: Note)
}