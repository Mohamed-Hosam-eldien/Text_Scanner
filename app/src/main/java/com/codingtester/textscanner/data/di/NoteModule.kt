package com.codingtester.textscanner.data.di

import android.content.Context
import androidx.room.Room
import com.codingtester.textscanner.data.data_source.NoteDatabase
import com.codingtester.textscanner.data.repository_impl.NoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            NoteDatabase.DATABASE_NAME
        ).build()


    @Provides
    @Singleton
    fun provideDataRepository(
        noteDatabase: NoteDatabase
    ): NoteRepositoryImpl {
        return NoteRepositoryImpl(noteDatabase.noteDao())
    }
}