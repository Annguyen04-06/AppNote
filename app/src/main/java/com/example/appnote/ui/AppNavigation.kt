package com.example.appnote.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appnote.ui.screens.AddEditNoteScreen
import com.example.appnote.ui.screens.ModernLoginScreen
import com.example.appnote.ui.screens.ModernNotesListScreen
import com.example.appnote.ui.screens.ModernRegisterScreen
import com.example.appnote.ui.screens.UserManagementScreen
import com.example.appnote.viewmodel.AuthViewModel
import com.example.appnote.viewmodel.NoteViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    noteViewModel: NoteViewModel
) {
    val navController = rememberNavController()
    val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

    LaunchedEffect(isAuthenticated) {
        if (isAuthenticated) {
            navController.navigate("notes_list") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            ModernLoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("notes_list") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            ModernRegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    // Logout after successful registration to force login
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("notes_list") {
            ModernNotesListScreen(
                authViewModel = authViewModel,
                noteViewModel = noteViewModel,
                onAddNote = {
                    navController.navigate("add_note")
                },
                onEditNote = { note ->
                    noteViewModel.selectNote(note)
                    navController.navigate("edit_note/${note.id}")
                },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("notes_list") { inclusive = true }
                    }
                },
                onManageUsers = {
                    navController.navigate("user_management")
                }
            )
        }

        composable("add_note") {
            AddEditNoteScreen(
                authViewModel = authViewModel,
                noteViewModel = noteViewModel,
                note = null,
                onBack = {
                    noteViewModel.loadUserNotes()
                    navController.popBackStack()
                }
            )
        }

        composable("edit_note/{noteId}") { backStackEntry ->
            val selectedNote by noteViewModel.selectedNote.collectAsState()
            selectedNote?.let {
                AddEditNoteScreen(
                    authViewModel = authViewModel,
                    noteViewModel = noteViewModel,
                    note = it,
                    onBack = {
                        noteViewModel.loadUserNotes()
                        noteViewModel.clearSelectedNote()
                        navController.popBackStack()
                    }
                )
            }
        }

        composable("user_management") {
            UserManagementScreen(
                authViewModel = authViewModel,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
