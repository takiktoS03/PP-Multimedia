package com.example.multimedia.data.repository

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.multimedia.data.model.Photo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {
    private val storageRef = firebaseStorage.getReferenceFromUrl("gs://image-management-cbaee.firebasestorage.app")
    fun uploadPhoto(
        photoUri: Uri,
        meta: Photo,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val photoRef = storageRef.child("photos/$fileName")

        val uploadTask = photoRef.putFile(photoUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
            photoRef.downloadUrl
        }.addOnSuccessListener { uri ->
            val photo = meta.copy(
                file_path = uri.toString(),
                uploaded_at = Timestamp.now()
            )

            firestore.collection("photos")
                .add(photo)
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onFailure(it) }

        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }

    fun getPhotos(): LiveData<List<Photo>> {
        val liveData = MutableLiveData<List<Photo>>()

        firestore.collection("photos")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    liveData.value = emptyList()
                    return@addSnapshotListener
                }

                val photos = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Photo::class.java)?.copy(id = doc.id)
                }

                liveData.value = photos
            }

        return liveData
    }

}
