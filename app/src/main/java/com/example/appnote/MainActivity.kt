package com.example.appnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.example.appnote.ui.AppNavigation
import com.example.appnote.ui.theme.AppNoteTheme
import com.example.appnote.viewmodel.AuthViewModel
import com.example.appnote.viewmodel.NoteViewModel

class MainActivity : ComponentActivity() {
    private lateinit var authViewModel: AuthViewModel
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)
        noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

        setContent {
            AppNoteTheme {
                AppNavigation(authViewModel, noteViewModel)
            }
        }
    }
}