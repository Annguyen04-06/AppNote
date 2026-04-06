package com.example.appnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnote.model.User
import com.example.appnote.repository.FirebaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = FirebaseRepository()
    
    private val _isLoading = MutableStateFlow(true) // Start with true for initial load
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _isAuthenticated = MutableStateFlow(repository.getCurrentUser() != null)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated
    
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized
    
    init {
        // Initialize app state
        viewModelScope.launch {
            _isLoading.value = false
            _isInitialized.value = true
        }
    }

    fun register(userId: String, email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.registerUser(userId, email, password, displayName)
            result.onSuccess { user ->
                _currentUser.value = user
                _isAuthenticated.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Registration failed"
                _isLoading.value = false
            }
        }
    }

    fun login(userId: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.loginUser(userId, password)
            result.onSuccess { user ->
                _currentUser.value = user
                _isAuthenticated.value = true
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Login failed"
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        repository.logoutUser()
        _currentUser.value = null
        _isAuthenticated.value = false
        _error.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
