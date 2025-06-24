package com.example.multimedia.ui.gallery

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.PhotoRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PhotoRepository) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    val rawPhotos: LiveData<List<Photo>>
    private val _filters = MutableLiveData<Pair<String?, List<String>>>(null to emptyList())
    val filters: LiveData<Pair<String?, List<String>>> = _filters

    var isEditDialogVisible by mutableStateOf(false)
        private set
    var photoBeingEdited by mutableStateOf<Photo?>(null)
        private set
    var selectedPhotos by mutableStateOf<List<Photo>>(emptyList())
        private set

    init {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        rawPhotos = repository.getPhotos(currentUserId)
    }

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
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        val finalLocation = location.ifBlank { "Nieznana lokalizacja" }.take(200) // ⬅ ZMIANA

        val meta = Photo(
            title       = title,
            description = description,
            location    = finalLocation, // ⬅ ZMIANA
            tags        = tags,
            file_path   = "",
            uploaded_at = Timestamp.now(),
            user_id     = currentUserId
        )
        Log.d("uploadPhoto", "Uploading photo with location = '$location'")

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

    // 2 funkcje obslugujace edycje zdjec (+ powrot z mapy)
    fun showEditDialog(photos: List<Photo>) {
        selectedPhotos = photos
        isEditDialogVisible = true
        photoBeingEdited = photos.firstOrNull()
    }

    fun dismissDialog() {
        isEditDialogVisible = false
        photoBeingEdited = null
        selectedPhotos = emptyList()
    }

    private var _pendingImageUris by mutableStateOf<List<Uri>>(emptyList())
    val pendingImageUris: List<Uri> get() = _pendingImageUris


    fun setPendingImageUris(uris: List<Uri>) {
        _pendingImageUris = uris
    }
    fun clearPendingImageUris() {
        _pendingImageUris = emptyList()
    }


    // Pobieranie zdjec na pamiec telefonu
    fun downloadPhotosToFolder(
        context: Context,
        folder: DocumentFile,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            var allSuccess = true
            for (photo in selectedPhotos) {
                val success = repository.downloadPhotoToFolder(context, folder, photo)
                if (!success) allSuccess = false
            }
            withContext(Dispatchers.Main) {
                onComplete(allSuccess)
            }
        }
    }

}
