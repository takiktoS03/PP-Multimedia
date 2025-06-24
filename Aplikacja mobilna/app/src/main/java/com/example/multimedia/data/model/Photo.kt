package com.example.multimedia.data.model

import com.google.firebase.Timestamp

data class Photo(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val file_path: String = "",
    val location: String = "",
    val tags: List<String> = emptyList(),
    val uploaded_at: Timestamp? = null,
    val user_id: String? = null
)
