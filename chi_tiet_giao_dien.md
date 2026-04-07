# 🎨 Chi Tiết Giao Diện AppNote

**Ngày cập nhật:** April 7, 2026  
**Tác giả:** Nguyen An

---

## 📱 Tổng Quan

AppNote được xây dựng **100% với Jetpack Compose** (không dùng XML layout). Giao diện tuân theo **Material Design 3** với color scheme hiện đại.

---

## 🎯 Tóm Tắt Nhanh (Quick Overview)

| Thành Phần | File | Chức Năng |
|-----------|------|----------|
| 🧭 **Navigation** | `AppNavigation.kt` | Setup 5 routes (Login → Register → Notes → Add → Edit) |
| 🎨 **Màu sắc** | `Color.kt` | Định nghĩa bảng màu Material Design 3 |
| 🎭 **Theme** | `Theme.kt` | Cấu hình chủ đề Light theme |
| ✍️ **Typography** | `Type.kt` | Kiểu chữ (Font sizes, styles) |
| 🔐 **Login** | `AuthScreensModern.kt` | ModernLoginScreen() - Gradient header, inputs, button |
| 📝 **Register** | `AuthScreensModern.kt` | ModernRegisterScreen() - Form 5 fields, validation |
| 📋 **Notes List** | `NotesListScreenModern.kt` | ModernNotesListScreen() - TopAppBar (Blue), FAB (Purple), Cards |
| ➕ **Add/Edit** | `AddEditNoteScreen.kt` | AddEditNoteScreen() - Title, Description, Image, File inputs |

**Công nghệ sử dụng:**
- ✅ Jetpack Compose (100%)
- ✅ Material Design 3
- ✅ Navigation Compose
- ✅ StateFlow (State management)
- ✅ Coil (Image loading)

---

## 🎯 Bảng Màu (Color Palette)

```kotlin
// Color.kt - Định nghĩa toàn bộ màu
val PrimaryBlue = Color(0xFF007AFF)      // Xanh chính
val SecondaryPurple = Color(0xFF5856D6)  // Tím phụ
val AccentOrange = Color(0xFF9500)       // Cam nhấn
val LightBackground = Color(0xFFF5F5F7)  // Nền sáng
val TextPrimary = Color(0xFF1C1C1E)      // Text chính
val TextSecondary = Color(0xFF8E8E93)    // Text phụ
val CardBackground = Color.White         // Card
val Error = Color(0xFFD32F2F)           // Lỗi/Xóa
```

| Màu | Hex | Sử dụng |
|-----|-----|--------|
| 🔵 Primary Blue | `#007AFF` | TopAppBar, Save Button |
| 🟣 Secondary Purple | `#5856D6` | FAB, File Button |
| 🟠 Accent Orange | `#FF9500` | Image Button, Loading |
| ⚪ Light Background | `#F5F5F7` | App Background |
| ⚫ Text Primary | `#1C1C1E` | Tiêu đề, Text chính |
| ⚫ Text Secondary | `#8E8E93` | Mô tả, Text phụ |

---

## 📋 Chi Tiết Các Màn Hình

### 1️⃣ **Màn Hình Đăng Nhập (Login)**
`AuthScreensModern.kt` → `ModernLoginScreen()`

**Thành phần:**
- 🎨 **Gradient Header**: Xanh → Tím (PrimaryBlue → SecondaryPurple)
- 📝 **User ID Input**: `OutlinedTextField` với PrimaryBlue border
- 🔐 **Password Input**: Input bảo mật
- 🔵 **Login Button**: Nền Xanh (PrimaryBlue), Text trắng
- 🔗 **Sign Up Link**: "Don't have account? Sign up"

**Layout:**
```
┌─────────────────────┐
│   Gradient Xanh-Tím │ (Chiều cao: 200.dp)
├─────────────────────┤
│                     │
│  🔑 User ID Input   │ (Card trắng, 12.dp corner)
│                     │
│  🔐 Password Input  │ (Card trắng, 12.dp corner)
│                     │
│  ┌─────────────────┐│
│  │ Login (Xanh)    ││
│  └─────────────────┘│
│                     │
│  Sign up ← Link     │
│                     │
└─────────────────────┘
```

---

### 2️⃣ **Màn Hình Đăng Ký (Register)**
`AuthScreensModern.kt` → `ModernRegisterScreen()`

**Thành phần:**
- 🎨 **Gradient Header**: Xanh → Tím
- 👤 **User ID Input**: Unique identifier (e.g., "student123")
- 📧 **Email Input**: Validate email format
- 👨 **Display Name Input**: Tên hiển thị (hỗ trợ Tiếng Việt)
- 🔐 **Password Input**: Min 6 ký tự
- ✅ **Confirm Password**: Xác nhận lại password
- 🟣 **Create Account Button**: Nền Tím (SecondaryPurple)
- 🔗 **Back to Login**: Link quay lại

**Features:**
- ✅ Form validation
- ✅ Password strength check
- ✅ Unicode support (Tiếng Việt có dấu)

---

### 3️⃣ **Màn Hình Danh Sách Notes**
`NotesListScreenModern.kt` → `ModernNotesListScreen()`

**TopAppBar:**
- 🔵 **Nền**: PrimaryBlue (#007AFF)
- 📝 **Tiêu đề**: "Notes" (Bold, 24.sp)
- 👤 **Subtitle**: Tên user (12.sp, opacity 0.8)
- 🚪 **Logout Button**: Icon ExitToApp (trắng)

**FAB (Floating Action Button):**
- 🟣 **Màu**: SecondaryPurple
- 📝 **Icon**: Add (+)
- 📍 **Vị trí**: Bottom-Right (padding 16.dp)
- 🎭 **Shape**: RoundedCornerShape(16.dp)

**Note Cards:**
```
┌──────────────────────────────┐
│ 📌 Học tiếng Anh             │ ✏️ 🗑️
│                               │
│ Cần tập phát âm từ vocab mới  │
│                               │
│ 15/04/2026  📎 vocab.pdf     │
└──────────────────────────────┘
```

**Chi tiết Card:**
- ✏️ **Title**: PrimaryBlue, Bold, max 2 lines
- 📝 **Description**: Gray, max 2 lines
- 📅 **Date**: "dd/MM/yyyy" format (Gray, 12.sp)
- 📎 **File Badge**: Nếu có file → hiển thị "📎 fileName"
- **Edit Icon**: Button xanh (Edit) - RoundedCornerShape(8.dp)
- **Delete Icon**: Button đỏ (Delete) - RoundedCornerShape(8.dp)

**Elevation:**
- Default: 2.dp
- On Hover: 8.dp (animation)

**Empty State:**
```
    📝
    No Notes Yet
    Create your first note
```

---

### 4️⃣ **Màn Hình Tạo/Sửa Note**
`AddEditNoteScreen.kt`

**TopAppBar:**
- 🔵 **Nền**: PrimaryBlue
- 📝 **Tiêu đề**: "New Note" hoặc "Edit Note"
- ⬅️ **Back Button**: Icon ArrowBack (trắng)
- ⏳ **Loading Indicator**: Hiển thị khi đang lưu

**Input Fields:**

#### **Title Section**
```
┌──────────────────────────┐
│ Title                    │
│ ┌──────────────────────┐ │
│ │ Học tiếng Anh      │ │ (Xanh border khi focus)
│ └──────────────────────┘ │
└──────────────────────────┘
```
- White Card, RoundedCornerShape(12.dp)
- Single line, PrimaryBlue focus border
- Hỗ trợ Tiếng Việt

#### **Description Section**
```
┌──────────────────────────┐
│ Description              │
│ ┌──────────────────────┐ │
│ │ Cần tập phát âm..  │ │ (Height: 120.dp)
│ │                      │ │
│ │                      │ │
│ └──────────────────────┘ │
└──────────────────────────┘
```
- White Card, RoundedCornerShape(12.dp)
- Multiline, Height 120.dp
- PrimaryBlue focus border
- Hỗ trợ Tiếng Việt

#### **Image Section**
```
┌──────────────────────────────┐
│ 🖼️ Add Image               ⏳ │
├──────────────────────────────┤
│ ┌──────────────────────────┐ │
│ │   [Image Preview]        │ │ (Height: 150.dp)
│ └──────────────────────────┘ │
│           Remove (Red)       │
│ ┌──────────────────────────┐ │
│ │  Choose Image (Orange)   │ │
│ └──────────────────────────┘ │
└──────────────────────────────┘
```
- White Card, Elevation 2.dp
- Orange "Choose Image" button
- RED "Remove" button khi đã chọn
- Preview AsyncImage (ContentScale.Crop)
- Orange loading spinner khi uploading
- Button disabled khi uploading

#### **File Section**
```
┌──────────────────────────────┐
│ 📁 Add File                ⏳ │
├──────────────────────────────┤
│ ┌──────────────────────────┐ │
│ │ file_123456  ✕          │ │ (File info card)
│ └──────────────────────────┘ │
│ ┌──────────────────────────┐ │
│ │  Choose File (Purple)    │ │
│ └──────────────────────────┘ │
└──────────────────────────────┘
```
- White Card, Elevation 2.dp
- Purple "Choose File" button
- File info card (LightBackground)
- Hiển thị tên file gốc (e.g., "zarchiver.apk")
- X icon để xóa file
- Orange loading spinner khi uploading

**Save/Update Button:**
```
┌──────────────────────────────┐
│  Create Note (Blue)          │ (Height: 56.dp)
│  hoặc Update                 │
└──────────────────────────────┘
```
- Full width, Height 56.dp
- Primary Blue background
- White text, Bold, 16.sp
- RoundedCornerShape(12.dp)
- Disabled khi: isLoading=true HOẶC title/description rỗng

---

## 🏗️ Cấu Trúc Layout

### **Material Design 3 Composables Sử Dụng:**

```kotlin
// Scaffold - Main layout container
Scaffold(
    topBar = { TopAppBar(...) },
    floatingActionButton = { FloatingActionButton(...) },
    containerColor = LightBackground
)

// Cards
Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
)

// Buttons
Button(
    onClick = { ... },
    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
    shape = RoundedCornerShape(12.dp),
    enabled = condition
)

// Text Fields
OutlinedTextField(
    value = state,
    onValueChange = { state = it },
    label = { Text("Label") },
    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    colors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PrimaryBlue,
        unfocusedBorderColor = Color.Transparent
    )
)
```

---

## 🎬 Navigation Flow

```
┌─────────┐
│ Login   │
└────┬────┘
     │ Click "Sign Up"
     ↓
┌─────────────┐
│ Register    │
└────┬────────┘
     │ Create Account
     ↓
┌──────────────┐
│ Notes List   │ ← Main Screen
└─┬─────────┬──┘
  │ Click + │ Click Edit
  │ (FAB)   │
  ↓         ↓
┌────────────────────┐
│ Add/Edit Note      │
│ (Cùng screen)      │
└────────────────────┘
  │ Save
  ↓
┌──────────────┐
│ Notes List   │ ← Refresh
└──────────────┘
```

---

## 📐 Spacing & Sizing

### **Standard Values:**
```kotlin
// Corners (RoundedCornerShape)
- TopAppBar: Sharp (0.dp)
- Cards: 12.dp (inputs, main cards)
- Buttons: 12.dp (primary), 8.dp (secondary)
- File Badge: 4.dp (mini card)

// Padding
- Screen: 16.dp
- Card content: 16.dp
- Between elements: 12-16.dp
- InnerPadding: 8.dp (text fields)

// Height
- TopAppBar: Default (64.dp)
- FAB: Default (56.dp)
- Button: 56.dp
- TextField: 50-120.dp (depending)
- Description input: 120.dp
- Image preview: 150.dp
- Card: Wrap content

// Font Sizes
- Title: 24.sp (TopAppBar title)
- Subtitle: 12.sp (Display name)
- CardTitle: Material.titleMedium
- CardDescription: Material.bodySmall
- Labels: 14.sp
- Timestamps: 12.sp (labelSmall)
- Buttons: 16.sp

// Elevation (z-index)
- Default Card: 2.dp
- Hovered Card: 8.dp (animate)
- FAB: Default (6.dp)
- Button: Default (0-2.dp)
```

---

## 🌐 Hỗ Trợ Ngôn Ngữ

### **Tiếng Việt (Vietnamese Support)**

✅ **Hỗ trợ đầy đủ:**
- UTF-8 encoding trong tất cả TextFields
- `KeyboardOptions(keyboardType = KeyboardType.Text)` trên tất cả input
- Dấu, phụ âm: "Học tiếng Anh", "Khoa học Ebest"

✅ **UI Labels:**
- Tất cả label trong English (clean, professional)
- Nhưng user có thể nhập Tiếng Việt có dấu

**Ví dụ:**
```
UI: "Title" → User input: "Học tiếng Anh" ✅
UI: "Description" → User input: "Cần tập phát âm từ vocab mới" ✅
```

---

## 🎯 Animation & Transitions

```kotlin
// Card elevation animation
val elevation = if (isHovered) 8.dp else 2.dp  // Smooth animate
CardDefaults.cardElevation(defaultElevation = elevation)

// Loading spinner
CircularProgressIndicator(
    modifier = Modifier.size(16.dp),
    strokeWidth = 2.dp,
    color = AccentOrange  // Orange color
)

// Compose Navigation
navController.navigate("notes_list") {
    popUpTo("login") { inclusive = true }
}
```

---

## 🔧 Theme Configuration

```kotlin
// Theme.kt - Material Design 3
AppNoteTheme(
    darkTheme = false,  // Light mode only
    dynamicColor = false,
    content = content
)

// Light ColorScheme
lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryPurple,
    tertiary = AccentOrange,
    background = LightBackground,
    surface = CardBackground,
    error = Error
)
```

---

## 🧭 Navigation Setup (AppNavigation.kt)

**Chức năng:**
- Setup toàn bộ navigation flow của ứng dụng
- Quản lý chuyển đổi giữa các màn hình
- Tự động điều hướng dựa trên trạng thái xác thực

**Navigation Routes:**

```
1. LOGIN ("login") - ModernLoginScreen()
   ├─> Click "Sign Up" → REGISTER
   └─> Click "Login" (success) → NOTES_LIST

2. REGISTER ("register") - ModernRegisterScreen()
   ├─> Click "Back" → LOGIN
   └─> Click "Create Account" (success) → NOTES_LIST

3. NOTES_LIST ("notes_list") - ModernNotesListScreen()
   ├─> Click FAB (+) → ADD_NOTE
   ├─> Click Edit icon → EDIT_NOTE
   └─> Click Logout → LOGIN

4. ADD_NOTE ("add_note") - AddEditNoteScreen()
   ├─> Click "Create Note" (success) → NOTES_LIST (refresh)
   └─> Click Back → NOTES_LIST

5. EDIT_NOTE ("edit_note/{noteId}") - AddEditNoteScreen()
   ├─> Click "Update" (success) → NOTES_LIST (refresh)
   └─> Click Back → NOTES_LIST
```

**Auto-Navigation Logic:**
```kotlin
LaunchedEffect(isAuthenticated) {
    if (isAuthenticated) {
        navController.navigate("notes_list") {
            popUpTo("login") { inclusive = true }  // Xóa all previous routes
        }
    }
}
```

**Tech Stack:**
- Jetpack Navigation Compose
- LaunchedEffect để theo dõi state thay đổi
- NavController để điều khiển nhảy giữa routes
- StateFlow để quản lý authentication state

---

## 📱 Screen Size Adaptation

**Tested on:**
- Phone: 5.5" - 6.5" (Pixel 7)
- Orientation: Portrait (primary)

**Responsive:**
- ✅ `fillMaxWidth()` for full-width elements
- ✅ `Modifier.weight()` for proportional sizing
- ✅ `Column` & `Row` for adaptive layouts
- ✅ `LazyColumn` for scrollable lists

---

## 🚀 Performance Optimizations

```kotlin
// Lazy loading
LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(12.dp)
) {
    items(notes) { note ->
        ModernNoteCard(note = note, ...)
    }
}

// StateFlow for efficient recomposition
val notes by noteViewModel.notes.collectAsState()
```

---

## ✅ Checklist Giao Diện

- ✅ Material Design 3 tuân thủ
- ✅ 100% Jetpack Compose (không XML layout)
- ✅ Modern color palette (xanh, tím, cam)
- ✅ Gradient backgrounds
- ✅ Rounded corners trên cards & buttons
- ✅ Elevation effects
- ✅ Responsive layout
- ✅ Real-time data display
- ✅ Loading indicators
- ✅ Empty states
- ✅ Vietnamese text support
- ✅ Accessible (content descriptions)

---

## 📸 Visual Hierarchy

```
1. Visual Focus: PrimaryBlue (most important buttons)
2. Secondary: SecondaryPurple (FAB, secondary actions)
3. Neutral: White cards on LightBackground
4. Accent: AccentOrange (Add Image, loading)
5. Danger: Error Red (Delete, Remove)
```

---

**Kết luận:** Giao diện AppNote là **hiện đại, sạch sẽ, chuyên nghiệp** với Material Design 3 và 100% Jetpack Compose! 🎨✨
