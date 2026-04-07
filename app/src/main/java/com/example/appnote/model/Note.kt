package com.example.appnote.model

data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String = "",
    val fileUrl: String = "",
    val fileName: String = "",
    val createdBy: String = "",  // UID của người tạo note
    val isReadOnly: Boolean = false,  // True nếu user không được phép edit/delete
    val isSharedWithAll: Boolean = false  // True nếu admin chia sẻ cho tất cả user
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "id" to id,
            "title" to title,
            "description" to description,
            "userId" to userId,
            "timestamp" to timestamp,
            "imageUrl" to imageUrl,
            "fileUrl" to fileUrl,
            "fileName" to fileName,
            "createdBy" to createdBy,
            "isReadOnly" to isReadOnly,
            "isSharedWithAll" to isSharedWithAll
        )
    }
}
