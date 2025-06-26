package com.example.multimedia.data.repository

import android.net.Uri
import com.example.multimedia.data.model.MediaType
import com.example.multimedia.data.model.OtherMedia
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.util.UUID

class OtherMediaRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) {

    suspend fun addMedia(fileUri: Uri, title: String, description: String, type: MediaType): OtherMedia {
        val userId = auth.currentUser?.uid ?: throw Exception("User not logged in")
        val folder = if (type == MediaType.VIDEO) "videos" else "audio"
        val fileId = UUID.randomUUID().toString()
        val fileRef = storage.reference.child("$folder/$fileId")

        fileRef.putFile(fileUri).await()
        val downloadUrl = fileRef.downloadUrl.await().toString()

        val media = OtherMedia(
            id = fileId,
            title = title,
            description = description,
            url = downloadUrl,
            type = type,
            userId = userId
        )

        firestore.collection(folder).document(fileId).set(media).await()
        return media
    }

    suspend fun getUserMedia(): List<OtherMedia> {
        val userId = auth.currentUser?.uid ?: return emptyList()

        val videos = firestore.collection("videos")
            .whereEqualTo("userId", userId)
            .get().await()
            .toObjects(OtherMedia::class.java)

        val audios = firestore.collection("audio")
            .whereEqualTo("userId", userId)
            .get().await()
            .toObjects(OtherMedia::class.java)

        return videos + audios
    }
}