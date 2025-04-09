package com.example.multimedia.data.model

import com.google.firebase.firestore.GeoPoint

data class Photo(
    val id: String,
    val uri: String,
    val title: String,
    val tags: List<String>,
    val location: GeoPoint?,
    val timestamp: Long
)
