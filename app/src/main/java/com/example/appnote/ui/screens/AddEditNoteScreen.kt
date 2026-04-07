package com.example.appnote.ui.screens

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import coil.compose.AsyncImage
import com.example.appnote.model.Note
import com.example.appnote.ui.theme.AccentOrange
import com.example.appnote.ui.theme.LightBackground
import com.example.appnote.ui.theme.PrimaryBlue
import com.example.appnote.ui.theme.SecondaryPurple
import com.example.appnote.viewmodel.AuthViewModel
import com.example.appnote.viewmodel.NoteViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    authViewModel: AuthViewModel,
    noteViewModel: NoteViewModel,
    note: Note? = null,
    onBack: () -> Unit
) {
    var title by remember { mutableStateOf(note?.title ?: "") }
    var description by remember { mutableStateOf(note?.description ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileNameDisplay by remember { mutableStateOf("") }
    var isUploadingImage by remember { mutableStateOf(false) }
    var isUploadingFile by remember { mutableStateOf(false) }
    
    // For admin assigning note to user
    var selectedUserId by remember { mutableStateOf<String?>(null) }
    var showUserDropdown by remember { mutableStateOf(false) }

    val isLoading by noteViewModel.isLoading.collectAsState()
    val error by noteViewModel.error.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val currentUserRole by noteViewModel.currentUserRole.collectAsState()
    val allUsers by authViewModel.allUsers.collectAsState()
    val context = LocalContext.current
    val isAdmin = currentUserRole == "admin"

    val scope = rememberCoroutineScope()
    
    // Load users if admin
    LaunchedEffect(isAdmin) {
        if (isAdmin && allUsers.isEmpty()) {
            authViewModel.loadAllUsers()
        }
    }

    // Helper function to get file name from Uri
    fun getFileNameFromUri(uri: Uri): String {
        var displayName = "File"
        try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    displayName = it.getString(nameIndex)
                }
            }
        } catch (e: Exception) {
            displayName = "File_${System.currentTimeMillis()}"
        }
        return displayName
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            isUploadingImage = true
            scope.launch {
                // Attempt upload silently - no error notification if fails
                noteViewModel.uploadImage(uri, context) { success ->
                    isUploadingImage = false
                }
            }
        }
    }

    // File picker launcher
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedFileUri = uri
            fileNameDisplay = getFileNameFromUri(uri)  // Get actual file name
            isUploadingFile = true
            scope.launch {
                // Attempt upload silently - no error notification if fails
                noteViewModel.uploadFile(uri, context) { success ->
                    isUploadingFile = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (note == null) "New Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue,
                    navigationIconContentColor = Color.White,
                    titleContentColor = Color.White
                ),
                actions = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(24.dp)
                                .padding(end = 16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                }
            )
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Title Input
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Description Input
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }

            // Image Section - Modern Design
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Image header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🖼️ Add Image", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (isUploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 8.dp),
                                strokeWidth = 2.dp,
                                color = AccentOrange
                            )
                        }
                    }

                    // Image preview if selected
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(LightBackground, RoundedCornerShape(8.dp))
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Selected Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Button(
                            onClick = { selectedImageUri = null },
                            modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE74C3C)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Remove", fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isUploadingImage
                        ) {
                            Text("Choose Image")
                        }
                    }
                }
            }

            // Assign to User Section - For Admin Only
            if (isAdmin && note == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("👤 Assign to User (Optional)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = { showUserDropdown = !showUserDropdown },
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, PrimaryBlue)
                            ) {
                                Text(
                                    if (selectedUserId == null) "Select user..." else {
                                        val user = allUsers.find { it.uid == selectedUserId }
                                        user?.displayName ?: "Unknown"
                                    },
                                    modifier = Modifier.weight(1f),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown")
                            }
                            
                            if (showUserDropdown) {
                                DropdownMenu(
                                    expanded = showUserDropdown,
                                    onDismissRequest = { showUserDropdown = false },
                                    modifier = Modifier.fillMaxWidth(0.9f)
                                ) {
                                    // Add option to keep as personal note
                                    DropdownMenuItem(
                                        text = { Text("My Personal Note") },
                                        onClick = {
                                            selectedUserId = null
                                            showUserDropdown = false
                                        }
                                    )
                                    
                                    HorizontalDivider()
                                    
                                    // List all users except current admin
                                    allUsers.forEach { user ->
                                        if (user.uid != currentUser?.uid) {
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text(user.displayName, fontWeight = FontWeight.Bold)
                                                        Text(user.email, fontSize = 12.sp, color = Color.Gray)
                                                    }
                                                },
                                                onClick = {
                                                    selectedUserId = user.uid
                                                    showUserDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        if (selectedUserId != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFE0B2),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "👁️ This user will see the note as read-only",
                                    fontSize = 12.sp,
                                    color = Color(0xFFF57C00),
                                    modifier = Modifier.padding(12.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (isAdmin && note == null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFC8E6C9),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    "🔗 This note will be shared with all users (read-only view)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2E7D32),
                                    modifier = Modifier.padding(12.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // File Section - Modern Design
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // File header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📁 Add File", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (isUploadingFile) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(16.dp)
                                    .padding(start = 8.dp),
                                strokeWidth = 2.dp,
                                color = AccentOrange
                            )
                        }
                    }

                    // File info if selected
                    if (selectedFileUri != null) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = LightBackground)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(fileNameDisplay, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { 
                                        selectedFileUri = null
                                        fileNameDisplay = ""
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Text("✕", fontSize = 16.sp)
                                }
                            }
                        }
                    } else {
                        Button(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = SecondaryPurple),
                            shape = RoundedCornerShape(8.dp),
                            enabled = !isUploadingFile
                        ) {
                            Text("Choose File")
                        }
                    }
                }
            }

            // Save Button
            Button(
                onClick = {
                    if (title.isEmpty() || description.isEmpty()) {
                        return@Button
                    }

                    if (note == null) {
                        // Determine userId and isReadOnly
                        val noteUserId = selectedUserId ?: currentUser?.uid ?: ""
                        val isReadOnly = selectedUserId != null  // Read-only if assigned to another user
                        val isSharedWithAll = selectedUserId == null  // Share with all if not assigned to specific user
                        
                        val newNote = Note(
                            title = title,
                            description = description,
                            userId = noteUserId,
                            imageUrl = if (selectedImageUri != null) selectedImageUri.toString() else "",
                            fileUrl = if (selectedFileUri != null) selectedFileUri.toString() else "",
                            fileName = fileNameDisplay,
                            createdBy = currentUser?.uid ?: "",
                            isReadOnly = isReadOnly,
                            isSharedWithAll = isSharedWithAll
                        )
                        noteViewModel.addNote(newNote)
                    } else {
                        val updatedNote = note.copy(
                            title = title,
                            description = description,
                            imageUrl = if (selectedImageUri != null) selectedImageUri.toString() else note.imageUrl,
                            fileUrl = if (selectedFileUri != null) selectedFileUri.toString() else note.fileUrl,
                            fileName = if (fileNameDisplay.isNotEmpty()) fileNameDisplay else note.fileName
                        )
                        noteViewModel.updateNote(updatedNote)
                    }
                    onBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && title.isNotEmpty() && description.isNotEmpty()
            ) {
                Text(
                    if (note == null) "Create Note" else "Update",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
