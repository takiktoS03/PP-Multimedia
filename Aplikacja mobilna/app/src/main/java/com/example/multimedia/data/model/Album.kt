package com.example.multimedia.data.model

import com.google.firebase.Timestamp

data class Album(
    val id: String = "",
    val user_id: String? = null,
    val name: String = "",
    val description: String = "",
    val created_at: Timestamp? = null
)
