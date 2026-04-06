package com.example.appnote.model

data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val userId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val imageUrl: String = "",
    val fileUrl: String = "",
    val fileName: String = ""
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
            "fileName" to fileName
        )
    }
}
