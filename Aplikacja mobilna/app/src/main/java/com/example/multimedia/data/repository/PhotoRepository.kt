package com.example.multimedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.multimedia.data.model.Photo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {
    fun uploadPhoto(photo: Photo, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // upload logic
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
