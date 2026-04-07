package com.example.appnote.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.InputStream

object CloudinaryUtil {
    
    private const val CLOUD_NAME = "dolefmpop" // Your Cloudinary cloud name
    private const val UPLOAD_PRESET = "appnote_unsigned" // Your unsigned upload preset
    private const val UPLOAD_URL = "https://api.cloudinary.com/v1_1/$CLOUD_NAME/image/upload"
    private val httpClient = OkHttpClient()
    
    fun init(context: Context) {
        // HTTP client is already initialized
        android.util.Log.d("CloudinaryUtil", "Cloudinary initialized for cloud: $CLOUD_NAME")
    }
    
    fun uploadImage(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                onError("Cannot open file")
                return
            }
            
            val fileBytes = inputStream.readBytes()
            inputStream.close()
            
            // Build multipart request
            val mediaType = "image/*".toMediaType()
            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "image.jpg", 
                    okhttp3.RequestBody.create(mediaType, fileBytes))
                .addFormDataPart("upload_preset", UPLOAD_PRESET)
                .build()
            
            val request = Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val jsonObject = JSONObject(responseBody)
                val secureUrl = jsonObject.optString("secure_url", "")
                
                if (secureUrl.isNotEmpty()) {
                    onSuccess(secureUrl)
                } else {
                    onError("URL not found in response")
                }
            } else {
                onError("Upload failed: ${response.code}")
            }
        } catch (e: Exception) {
            onError(e.message ?: "Upload error")
            android.util.Log.e("CloudinaryUtil", "Upload error", e)
        }
    }
    
    fun uploadFile(
        context: Context,
        uri: Uri,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        // Same as image upload - Cloudinary handles all file types
        uploadImage(context, uri, onSuccess, onError)
    }
}

