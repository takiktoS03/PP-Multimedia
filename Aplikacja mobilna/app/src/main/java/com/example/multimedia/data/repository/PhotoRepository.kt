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
        val photos = MutableLiveData<List<Photo>>()
        photos.value = listOf() // Przykładowa pusta lista, aby uniknąć błędów.
        return photos
    }

}
