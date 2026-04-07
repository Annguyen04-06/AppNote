package com.example.appnote.repository

import android.net.Uri
import com.example.appnote.model.Note
import com.example.appnote.model.User
import com.example.appnote.util.PasswordValidator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val realtimeDb = FirebaseDatabase.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Auth operations - Users in Firestore
    suspend fun registerUser(userId: String, email: String, password: String, displayName: String): Result<User> {
        return try {
            // Double-check password strength for security
            if (!PasswordValidator.isStrongPassword(password)) {
                throw Exception(PasswordValidator.getPasswordStrengthMessage(password))
            }
            
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("User ID not found")
            
            val user = User(
                uid = uid,
                email = email,
                displayName = displayName,
                role = "user"
            )
            
            // Lưu vào Firestore
            firestore.collection("users").document(uid).set(user).await()
            
            // Lưu userId mapping vào Realtime DB
            realtimeDb.reference.child("userAuth").child(userId).setValue(mapOf("uid" to uid)).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(userId: String, password: String): Result<User> {
        return try {
            // Lấy uid từ Realtime DB
            val userAuthSnapshot = realtimeDb.reference.child("userAuth").child(userId).get().await()
            val uid = userAuthSnapshot.child("uid").value as? String ?: throw Exception("User not found in userAuth: $userId")
            
            // Lấy user từ Firestore
            val userDoc = firestore.collection("users").document(uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found in Firestore for uid: $uid")
            val email = user.email ?: throw Exception("Email not found for user: $uid")
            
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(user)
        } catch (e: Exception) {
            android.util.Log.e("FirebaseRepository", "Login error: ${e.message}", e)
            Result.failure(Exception("Login failed: ${e.message}"))
        }
    }

    fun logoutUser() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    // Note operations - Realtime Database for real-time sync
    suspend fun addNote(note: Note): Result<String> {
        return try {
            val noteId = realtimeDb.reference.child("notes").push().key ?: throw Exception("Cannot generate note ID")
            val newNote = note.copy(id = noteId)
            realtimeDb.reference.child("notes").child(noteId).setValue(newNote).await()
            Result.success(noteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNote(note: Note): Result<Unit> {
        return try {
            realtimeDb.reference.child("notes").child(note.id).setValue(note).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            realtimeDb.reference.child("notes").child(noteId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNoteById(noteId: String): Result<Note> {
        return try {
            val snapshot = realtimeDb.reference.child("notes").child(noteId).get().await()
            val note = snapshot.getValue(Note::class.java) ?: throw Exception("Note not found")
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserNotes(): Result<List<Note>> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val snapshot = realtimeDb.reference.child("notes")
                .orderByChild("userId")
                .equalTo(currentUser.uid)
                .get()
                .await()
            
            val notes = snapshot.children.mapNotNull { it.getValue(Note::class.java) }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addActivityHistory(userId: String, action: String, details: String): Result<Unit> {
        return try {
            val history = mapOf(
                "userId" to userId,
                "action" to action,
                "details" to details,
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("activity_logs").add(history).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserActivityHistory(userId: String): Result<List<Map<String, Any>>> {
        return try {
            val snapshot = firestore.collection("activity_logs")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()
            
            val histories = snapshot.documents.mapNotNull { it.data }
            Result.success(histories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ========== PERMISSION & ROLE MANAGEMENT ==========
    
    suspend fun getCurrentUserRole(): Result<String> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val userDoc = firestore.collection("users").document(currentUser.uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("User data not found")
            Result.success(user.role)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun isUserAdmin(): Result<Boolean> {
        return try {
            val roleResult = getCurrentUserRole()
            if (roleResult.isSuccess) {
                val role = roleResult.getOrNull() ?: "user"
                Result.success(role == "admin")
            } else {
                Result.failure(Exception("Cannot determine admin status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            // Only admin can get all users
            val isAdmin = isUserAdmin().getOrNull() ?: false
            if (!isAdmin) {
                throw Exception("Only admin can view all users")
            }
            
            val snapshot = firestore.collection("users").get().await()
            val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUserRole(userId: String, newRole: String): Result<Unit> {
        return try {
            // Only admin can update user roles
            val isAdmin = isUserAdmin().getOrNull() ?: false
            if (!isAdmin) {
                throw Exception("Only admin can update user roles")
            }
            
            if (newRole !in listOf("admin", "user")) {
                throw Exception("Invalid role: $newRole")
            }
            
            firestore.collection("users").document(userId).update("role", newRole).await()
            
            // Log activity
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            addActivityHistory(currentUser.uid, "UPDATE_ROLE", "Changed user $userId role to $newRole")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            // Only admin can delete users
            val isAdmin = isUserAdmin().getOrNull() ?: false
            if (!isAdmin) {
                throw Exception("Only admin can delete users")
            }
            
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            if (userId == currentUser.uid) {
                throw Exception("Cannot delete your own account")
            }
            
            // Delete user's notes
            val notesSnapshot = realtimeDb.reference.child("notes")
                .orderByChild("userId")
                .equalTo(userId)
                .get()
                .await()
            
            for (noteSnapshot in notesSnapshot.children) {
                noteSnapshot.ref.removeValue().await()
            }
            
            // Delete user document
            firestore.collection("users").document(userId).delete().await()
            
            // Log activity
            addActivityHistory(currentUser.uid, "DELETE_USER", "Deleted user: $userId")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotesWithPermission(): Result<List<Note>> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val roleResult = getCurrentUserRole().getOrNull() ?: "user"
            
            val snapshot = if (roleResult == "admin") {
                // Admin can see all notes
                realtimeDb.reference.child("notes").get().await()
            } else {
                // User can see: their own notes + notes shared with all
                realtimeDb.reference.child("notes").get().await()
            }
            
            val notes = snapshot.children.mapNotNull { 
                val note = it.getValue(Note::class.java) ?: return@mapNotNull null
                
                // Filter based on role
                if (roleResult == "admin") {
                    // Admin sees everything
                    note
                } else {
                    // User sees: own notes OR shared with all
                    if (note.userId == currentUser.uid || note.isSharedWithAll) {
                        // Auto set isReadOnly if shared with all (not created by user)
                        if (note.isSharedWithAll && note.createdBy != currentUser.uid) {
                            note.copy(isReadOnly = true)
                        } else {
                            note
                        }
                    } else {
                        null  // Hide notes not owned and not shared
                    }
                }
            }
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun canDeleteNote(noteId: String): Result<Boolean> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val roleResult = getCurrentUserRole().getOrNull() ?: "user"
            
            val noteResult = getNoteById(noteId).getOrNull() ?: return Result.success(false)
            
            // Admin có thể xóa bất kỳ note nào
            if (roleResult == "admin") {
                return Result.success(true)
            }
            
            // User chỉ có thể xóa note của mình và nếu nó không phải read-only
            // Note is read-only if it was created by someone else
            val isReadOnly = noteResult.createdBy != "" && noteResult.createdBy != currentUser.uid
            val canDelete = noteResult.userId == currentUser.uid && !isReadOnly
            Result.success(canDelete)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun canEditNote(noteId: String): Result<Boolean> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val roleResult = getCurrentUserRole().getOrNull() ?: "user"
            
            val noteResult = getNoteById(noteId).getOrNull() ?: return Result.success(false)
            
            // Admin có thể edit bất kỳ note nào
            if (roleResult == "admin") {
                return Result.success(true)
            }
            
            // User chỉ có thể edit note của mình và nếu nó không phải read-only
            // Note is read-only if it was created by someone else
            val isReadOnly = noteResult.createdBy != "" && noteResult.createdBy != currentUser.uid
            val canEdit = noteResult.userId == currentUser.uid && !isReadOnly
            Result.success(canEdit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
