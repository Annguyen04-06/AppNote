# Firebase Storage Security Rules Configuration

## Problem
Images and files are not uploading because the Firebase Storage security rules are not properly configured.

## Solution

Go to Firebase Console → Your Project → Storage → Rules and replace the default rules with:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    // Allow authenticated users to upload files
    match /files/{allPaths=**} {
      allow read, write: if request.auth != null;
    }
    
    // Allow authenticated users to upload images
    match /images/{allPaths=**} {
      allow read, write: if request.auth != null;
    }
    
    // Fallback rules for other paths
    match /{allPaths=**} {
      allow read: if request.auth != null;
      allow write: if request.auth != null;
    }
  }
}
```

## Steps to Apply the Rules

1. **Open Firebase Console**
   - Go to https://console.firebase.google.com/
   - Select your project

2. **Navigate to Storage**
   - Click on "Storage" in the left sidebar
   - Click on the "Rules" tab

3. **Replace the Rules**
   - Clear the current rules
   - Paste the rules above
   - Click "Publish"

4. **Alternative - More Restrictive Rules** (if you want stricter security)
   ```javascript
   rules_version = '2';
   service firebase.storage {
     match /b/{bucket}/o {
       // Files: Allow read to authenticated users, write only if they own it
       match /files/{userId}/{fileName=**} {
         allow read: if request.auth != null;
         allow write: if request.auth.uid == userId;
       }
       
       // Images: Allow read to authenticated users, write only if they own it
       match /images/{userId}/{fileName=**} {
         allow read: if request.auth != null;
         allow write: if request.auth.uid == userId;
       }
     }
   }
   ```

## Troubleshooting

If uploads still fail:

1. **Check Authentication**
   - Make sure the user is logged in
   - The `auth.currentUser` must not be null

2. **Check Console Logs**
   - Look for "FirebaseRepository", "ImageUpload", or "FileUpload" tags in Android logcat
   - These logs will show the exact error message

3. **Common Errors**
   - `Permission denied: User is not authenticated` → User not logged in
   - `Storage bucket not initialized` → Check Firebase Console setup
   - `Operation not permitted` → Check Storage Rules

4. **Restart the App**
   - After updating Storage Rules, rebuild and reinstall the app

## Verification

After setting the rules and restarting the app:
1. Try uploading an image
2. Try uploading a file
3. Check the console logs for success messages
4. If successful, you should see URLs like: `https://firebasestorage.googleapis.com/...`

## Important Notes

- The current implementation requires users to be **authenticated** before uploading
- Files and images are stored in separate folders (`/files/` and `/images/`)
- Each file gets a unique name using timestamp to prevent overwriting

