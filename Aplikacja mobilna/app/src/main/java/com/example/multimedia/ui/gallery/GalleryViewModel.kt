package com.example.multimedia.ui.gallery

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.PhotoRepository
import com.google.firebase.Timestamp
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PhotoRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    val rawPhotos: LiveData<List<Photo>> = repository.getPhotos()
    private val _filters = MutableLiveData<Pair<String?, List<String>>>(null to emptyList())
    val filters: LiveData<Pair<String?, List<String>>> = _filters

    val photos: LiveData<List<Photo>> = MediatorLiveData<List<Photo>>().apply {
        fun update() {
            val list = rawPhotos.value   ?: emptyList()
            val (sort, tags) = _filters.value ?: (null to emptyList())

            var filtered = if (tags.isNotEmpty()) {
                list.filter { it.tags.any { tag -> tag in tags } }
            } else list

            filtered = when (sort) {
                "Tytuł A-Z"     -> filtered.sortedBy     { it.title.lowercase() }
                "Tytuł Z-A"     -> filtered.sortedByDescending { it.title.lowercase() }
                "Data rosnąco"  -> filtered.sortedBy     { it.uploaded_at }
                "Data malejąco" -> filtered.sortedByDescending { it.uploaded_at }
                else            -> filtered
            }

            value = filtered
        }

        // obserwuj oba źródła
        addSource(rawPhotos ) { update() }
        addSource(_filters   ) { update() }
    }

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
            title       = title,
            description = description,
            location    = location,
            tags        = tags,
            file_path   = "",
            uploaded_at = Timestamp.now()
        )
        repository.uploadPhoto(uri, meta, onSuccess, onFailure)
    }

    fun deletePhoto(photo: Photo) {
        repository.deletePhoto(photo,
            onComplete = { /* ewentualne akcje po usunięciu */ },
            onError    = { /* obsługa błędu */ }
        )
    }

    fun updatePhoto(photo: Photo) {
        repository.updatePhoto(photo)
    }


    fun applyFilters(sort: String?, tags: List<String>) {
        _filters.value = sort to tags
    }

    fun refresh() {
        viewModelScope.launch {
        }
    }

}
