# Hướng Dẫn Setup Firestore & Realtime DB Rules

## 1. Deploy Firestore Rules

### Cách 1: Dùng Firebase CLI
```bash
# Cài đặt Firebase CLI nếu chưa có
npm install -g firebase-tools

# Login vào Firebase
firebase login

# Deploy Firestore Rules
firebase deploy --only firestore:rules
```

### Cách 2: Dùng Firebase Console
1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project của bạn
3. Vào **Firestore Database** → **Rules**
4. **Copy & Paste** nội dung từ file `firestore.rules` vào editor
5. Click **Publish**

---

## 2. Deploy Realtime Database Rules

### Cách 1: Dùng Firebase CLI
```bash
firebase deploy --only database
```

### Cách 2: Dùng Firebase Console
1. Vào [Firebase Console](https://console.firebase.google.com/)
2. Chọn project của bạn
3. Vào **Realtime Database** → **Rules**
4. **Copy & Paste** nội dung từ file `realtime-db.rules.json` vào editor
5. Click **Publish**

---

## 3. Tạo Admin Account Đầu Tiên

### Cách 1: Dùng Firebase Console (Nhanh nhất)
1. Vào **Firestore** → Collection **users**
2. Click **Add document**
3. Set **Document ID** = UIDs của admin user (lấy từ **Authentication** tab)
4. Thêm fields sau:
   ```json
   {
     "uid": "user-uid-here",
     "email": "admin@example.com",
     "displayName": "Admin User",
     "role": "admin",
     "createdAt": [timestamp]
   }
   ```

### Cách 2: Tạo account qua App + Console
1. **Register** user bình thường qua app
2. Vào Firestore **users** collection
3. Edit user document
4. Thay đổi `role` từ `"user"` thành `"admin"`

---

## 4. Quyền Admin & User

### Admin có thể:
- ✅ Xem **tất cả note** của các user khác
- ✅ **Sửa/Xóa** note của bất kỳ user nào
- ✅ **Xem danh sách tất cả user**
- ✅ **Thay đổi role** user (user ↔ admin)
- ✅ **Xóa user** (cùng tất cả note của họ)

### User bình thường có thể:
- ✅ Xem **chỉ note của mình**
- ✅ **Tạo/Sửa/Xóa** note của mình
- ❌ Xem note của user khác
- ❌ Quản lý user

---

## 5. Cấu Trúc Database

### Firestore Structure
```
users/
  ├── userId1/
  │   ├── uid: "userId1"
  │   ├── email: "user@example.com"
  │   ├── displayName: "User Name"
  │   ├── role: "admin" | "user"
  │   └── createdAt: 1234567890
  └── userId2/
      └── ... (user bình thường)

activity_logs/
  ├── logId1/
  │   ├── userId: "userId1"
  │   ├── action: "ADD_NOTE" | "UPDATE_NOTE" | "DELETE_NOTE" | "UPDATE_ROLE" | "DELETE_USER"
  │   ├── details: "action details"
  │   └── timestamp: 1234567890
  └── ...
```

### Realtime Database Structure
```
notes/
  ├── noteId1/
  │   ├── id: "noteId1"
  │   ├── title: "Note Title"
  │   ├── description: "Note content"
  │   ├── userId: "userId1"
  │   ├── timestamp: 1234567890
  │   ├── imageUrl: "url-if-exists"
  │   ├── fileUrl: "url-if-exists"
  │   └── fileName: "filename-if-exists"
  └── ...

userAuth/
  ├── userId1/
  │   └── uid: "firebase-uid-1"
  └── ...
```

---

## 6. Troubleshooting

### Lỗi "Permission denied"
- **Nguyên nhân**: User không có quyền access dữ liệu
- **Giải pháp**: 
  - Kiểm tra Firestore/Realtime DB Rules đã deploy chưa
  - Kiểm tra role của user trong Firestore (admin | user)
  - Kiểm tra auth status của user

### Admin không thấy tất cả user
- **Nguyên nhân**: Firestore Rules chưa cấp quyền đúng
- **Giải pháp**: 
  - Đảm bảo rule `allow read: if isAdmin(request.auth.uid);` đã deploy
  - Check user có role "admin" trong Firestore chưa

### Đặt lại Firestore Rules về Test Mode (Không Security)
⚠️ **CHỈ DÙNG VÀO LÚC DEVELOPMENT/DEBUG**
```json
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if true;
    }
  }
}
```

---

## 7. Test Permission

### Test bằng App
1. **Create account user bình thường** qua app
2. **Create account admin** qua app
3. Admin account đổi role thành "admin" trong Firestore
4. Test:
   - User bình thường: chỉ xem note của mình
   - Admin: xem tất cả note + quản lý user

### Test bằng Firebase Console
1. Vào **Firestore** → **users** collection
2. Filter: `role == "admin"`
3. Verify admin users hiển thị đúng

---

Xong! Database Rules của bạn sẽ hỗ trợ phân quyền admin/user đầy đủ. 🎉
