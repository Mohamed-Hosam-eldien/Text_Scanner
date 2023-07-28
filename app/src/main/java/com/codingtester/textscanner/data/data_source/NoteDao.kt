package com.codingtester.textscanner.data.data_source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codingtester.textscanner.domain.model.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note)

    @Query("select * from note_table")
    fun getAllNotes(): Flow<List<Note>>

    @Query("select * from note_table where noteId = :id")
    suspend fun getNoteById(id: Int): Note

    @Delete
    suspend fun deleteNote(note: Note)
}