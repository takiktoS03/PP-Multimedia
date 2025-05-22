package com.example.multimedia.ui.gallery

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.PhotoRepository
import com.google.firebase.Timestamp
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PhotoRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    val photos: LiveData<List<Photo>> = repository.getPhotos()

    fun uploadPhoto(
        uri: Uri,
        title: String,
        description: String,
        location: String,
        tags: List<String>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val meta = Photo(
            title = title,
            description = description,
            location = location,
            tags = tags,
            file_path = "",
            uploaded_at = Timestamp.now()
        )

        repository.uploadPhoto(uri, meta, onSuccess, onFailure)
    }

    fun deletePhoto(photo: Photo) {
        repository.deletePhoto(photo,
            onComplete = { /* sukces, np. odświeżenie UI */ },
            onError = { e -> /* obsługa błędu */ }
        )
    }

    fun updatePhoto(photo: Photo) {
        repository.updatePhoto(photo)
    }
}
