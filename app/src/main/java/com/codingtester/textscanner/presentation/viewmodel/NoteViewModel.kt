package com.codingtester.textscanner.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codingtester.textscanner.domain.model.Note
import com.codingtester.textscanner.domain.use_case.NoteUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val useCases: NoteUseCases
) : ViewModel() {

    private val _noteState = MutableLiveData<List<Note>>()
    val noteState: LiveData<List<Note>> = _noteState

    init {
        getAllNotes()
    }
    fun addNote(note: Note) = viewModelScope.launch {
        useCases.addNote.invoke(note)
    }
    private fun getAllNotes() = viewModelScope.launch {
        useCases.getAllNotes.invoke().collectLatest {
            _noteState.value = it
        }
    }
    fun deleteNote(note: Note) = viewModelScope.launch {
        useCases.deleteNote.invoke(note)
    }
}