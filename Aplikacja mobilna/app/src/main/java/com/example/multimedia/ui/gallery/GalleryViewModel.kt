package com.example.multimedia.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.PhotoRepository
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PhotoRepository
) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    val photos: LiveData<List<Photo>> = repository.getPhotos()

    fun refreshGallery() {
        // trigger loading from repository
    }
}
