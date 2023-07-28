package com.codingtester.textscanner.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,
    val content: String = "",
    val dateInMilliSecond: Long=0
)

class InvalidNoteException: Exception()
