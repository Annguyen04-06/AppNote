package com.example.appnote.util

object PasswordValidator {
    
    // Password requirements:
    // - Minimum 8 characters
    // - At least 1 uppercase letter (A-Z)
    // - At least 1 lowercase letter (a-z)
    // - At least 1 digit (0-9)
    // - At least 1 special character (!@#$%^&*)
    
    private const val MIN_LENGTH = 8
    private val UPPERCASE_REGEX = Regex("[A-Z]")
    private val LOWERCASE_REGEX = Regex("[a-z]")
    private val DIGIT_REGEX = Regex("[0-9]")
    private val SPECIAL_CHAR_REGEX = Regex("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>?/~`]")
    
    fun isStrongPassword(password: String): Boolean {
        return password.length >= MIN_LENGTH &&
                UPPERCASE_REGEX.containsMatchIn(password) &&
                LOWERCASE_REGEX.containsMatchIn(password) &&
                DIGIT_REGEX.containsMatchIn(password) &&
                SPECIAL_CHAR_REGEX.containsMatchIn(password)
    }
    
    fun getPasswordStrengthMessage(password: String): String {
        val errors = mutableListOf<String>()
        
        if (password.isEmpty()) {
            return "Password cannot be empty"
        }
        
        if (password.length < MIN_LENGTH) {
            errors.add("At least $MIN_LENGTH characters")
        }
        
        if (!UPPERCASE_REGEX.containsMatchIn(password)) {
            errors.add("Uppercase letter (A-Z)")
        }
        
        if (!LOWERCASE_REGEX.containsMatchIn(password)) {
            errors.add("Lowercase letter (a-z)")
        }
        
        if (!DIGIT_REGEX.containsMatchIn(password)) {
            errors.add("Number (0-9)")
        }
        
        if (!SPECIAL_CHAR_REGEX.containsMatchIn(password)) {
            errors.add("Special character (!@#$%^&*, etc)")
        }
        
        return if (errors.isEmpty()) {
            "✓ Strong password"
        } else {
            "Required: ${errors.joinToString(", ")}"
        }
    }
}
