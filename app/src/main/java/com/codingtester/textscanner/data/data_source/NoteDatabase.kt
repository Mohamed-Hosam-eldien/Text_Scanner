package com.codingtester.textscanner.data.data_source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.codingtester.textscanner.domain.model.Note

@Database(
    entities = [Note::class],
    version = 1
)

abstract class NoteDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        const val DATABASE_NAME = "NOTES_SCANNER_DB"
    }
}