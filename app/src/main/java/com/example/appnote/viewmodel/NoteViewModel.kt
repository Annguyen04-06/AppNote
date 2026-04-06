package com.example.appnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnote.model.Note
import com.example.appnote.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote

    fun loadUserNotes() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.getUserNotes()
            result.onSuccess { notesList ->
                _notes.value = notesList.sortedByDescending { it.timestamp }
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load notes"
                _isLoading.value = false
            }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.addNote(note)
            result.onSuccess { noteId ->
                val newNote = note.copy(id = noteId)
                _notes.value = listOf(newNote) + _notes.value
                
                // Log activity
                repository.addActivityHistory(note.userId, "ADD_NOTE", "Created note: ${note.title}")
                
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to add note"
                _isLoading.value = false
            }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.updateNote(note)
            result.onSuccess {
                _notes.value = _notes.value.map { if (it.id == note.id) note else it }
                _selectedNote.value = note
                
                // Log activity
                repository.addActivityHistory(note.userId, "UPDATE_NOTE", "Updated note: ${note.title}")
                
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update note"
                _isLoading.value = false
            }
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val noteTitle = _notes.value.find { it.id == noteId }?.title ?: "Note"
            val userId = _notes.value.find { it.id == noteId }?.userId ?: ""
            
            val result = repository.deleteNote(noteId)
            result.onSuccess {
                _notes.value = _notes.value.filter { it.id != noteId }
                
                // Log activity
                if (userId.isNotEmpty()) {
                    repository.addActivityHistory(userId, "DELETE_NOTE", "Deleted note: $noteTitle")
                }
                
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to delete note"
                _isLoading.value = false
            }
        }
    }

    fun selectNote(note: Note) {
        _selectedNote.value = note
    }

    fun clearSelectedNote() {
        _selectedNote.value = null
    }


    fun clearError() {
        _error.value = null
    }

    fun uploadImage(uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        // Silently attempt upload - no error notification if fails
        onComplete(false)
    }

    fun uploadFile(uri: android.net.Uri, onComplete: (Boolean) -> Unit) {
        // Silently attempt upload - no error notification if fails
        onComplete(false)
    }
}
