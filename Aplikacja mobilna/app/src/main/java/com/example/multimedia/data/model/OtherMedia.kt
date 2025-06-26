package com.example.multimedia.data.model

enum class MediaType {
    AUDIO, VIDEO
}

data class OtherMedia(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val type: MediaType = MediaType.VIDEO,
    val userId: String = ""
)