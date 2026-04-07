package com.example.appnote.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appnote.model.User
import com.example.appnote.repository.FirebaseRepository
import com.example.appnote.util.PasswordValidator
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
    
    private val _currentUserRole = MutableStateFlow("user")
    val currentUserRole: StateFlow<String> = _currentUserRole
    
    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin
    
    private val _allUsers = MutableStateFlow<List<User>>(emptyList())
    val allUsers: StateFlow<List<User>> = _allUsers

    init {
        // Initialize app state
        viewModelScope.launch {
            _isLoading.value = false
            _isInitialized.value = true
            
            // Load user role if authenticated
            if (_isAuthenticated.value) {
                loadUserRole()
            }
        }
    }

    fun register(userId: String, email: String, password: String, confirmPassword: String, displayName: String) {
        // Validation
        if (userId.isBlank()) {
            _error.value = "User ID cannot be empty"
            return
        }
        
        if (email.isBlank()) {
            _error.value = "Email cannot be empty"
            return
        }
        
        if (!email.contains("@")) {
            _error.value = "Invalid email address"
            return
        }
        
        if (displayName.isBlank()) {
            _error.value = "Display name cannot be empty"
            return
        }
        
        if (!PasswordValidator.isStrongPassword(password)) {
            _error.value = PasswordValidator.getPasswordStrengthMessage(password)
            return
        }
        
        if (password != confirmPassword) {
            _error.value = "Password and confirm password do not match"
            return
        }
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = repository.registerUser(userId, email, password, displayName)
            result.onSuccess { user ->
                _currentUser.value = user
                _currentUserRole.value = user.role
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
                _currentUserRole.value = user.role
                _isAuthenticated.value = true
                _isAdmin.value = user.role == "admin"
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
        _currentUserRole.value = "user"
        _isAdmin.value = false
    }

    private fun loadUserRole() {
        viewModelScope.launch {
            val result = repository.getCurrentUserRole()
            result.onSuccess { role ->
                _currentUserRole.value = role
                _isAdmin.value = role == "admin"
            }
        }
    }

    // ========== ADMIN MANAGEMENT FUNCTIONS ==========
    
    fun loadAllUsers() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.getAllUsers()
            result.onSuccess { users ->
                _allUsers.value = users
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to load users"
                _isLoading.value = false
            }
        }
    }

    fun updateUserRole(userId: String, newRole: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.updateUserRole(userId, newRole)
            result.onSuccess {
                _error.value = "Updated user role successfully"
                // Reload all users
                loadAllUsers()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to update user role"
                _isLoading.value = false
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = repository.deleteUser(userId)
            result.onSuccess {
                _allUsers.value = _allUsers.value.filter { it.uid != userId }
                _error.value = "User deleted successfully"
                _isLoading.value = false
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to delete user"
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
