package com.codingtester.textscanner.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    val content: String = "",
    val dateInMilliSecond: Long=0
) {
    @PrimaryKey(autoGenerate = true)
    val noteId: Int? = null
}

class InvalidNoteException: Exception()
