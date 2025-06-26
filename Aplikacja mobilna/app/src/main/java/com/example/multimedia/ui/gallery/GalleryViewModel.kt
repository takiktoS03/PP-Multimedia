package com.example.multimedia.ui.gallery

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.PhotoRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GalleryViewModel @Inject constructor(
    private val repository: PhotoRepository,
    private val savedStateHandle: SavedStateHandle,
    private val auth: FirebaseAuth) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is gallery Fragment"
    }
    val text: LiveData<String> = _text

    private val albumId: String? = savedStateHandle.get<String>("albumId")
    private val currentUserId: String? get() = auth.currentUser?.uid

    val photos: LiveData<List<Photo>> =
        repository.getPhotos(currentUserId, albumId)

    private val _filters = MutableLiveData<Pair<String?, List<String>>>(null to emptyList())
    val filters: LiveData<Pair<String?, List<String>>> = _filters

    private val _currentAlbumId = MutableLiveData<String?>(null)

//    private val albumPhotoIds: LiveData<List<String>> = _currentAlbumId.switchMap { albumId ->
//        if (albumId.isNullOrEmpty()) MutableLiveData(emptyList())
//        else albumRepo.getPhotoIdsForAlbum(albumId)
//    }

//    private val albumPhotos: LiveData<List<Photo>> = albumPhotoIds.switchMap { ids ->
//        repository.getPhotosByIds(ids)
//    }

    var isEditDialogVisible by mutableStateOf(false)
        private set
    var photoBeingEdited by mutableStateOf<Photo?>(null)
        private set
    var selectedPhotos by mutableStateOf<List<Photo>>(emptyList())
        private set

//    val photos: LiveData<List<Photo>> = MediatorLiveData<List<Photo>>().apply {
//        var rawList   = emptyList<Photo>()
//        var albumList = emptyList<Photo>()
//
//        fun update() {
//            val source = if (albumId.isNullOrEmpty()) rawList else albumList
//            val (sort, tags) = _filters.value ?: (null to emptyList())
//            var filtered = if (tags.isNotEmpty())
//                source.filter { it.tags.any { t -> t in tags } }
//            else source
//
//            filtered = when (sort) {
//                "Tytuł A-Z"     -> filtered.sortedBy     { it.title.lowercase() }
//                "Tytuł Z-A"     -> filtered.sortedByDescending { it.title.lowercase() }
//                "Data rosnąco"  -> filtered.sortedBy     { it.uploaded_at }
//                "Data malejąco" -> filtered.sortedByDescending { it.uploaded_at }
//                else            -> filtered
//            }
//
//            value = filtered
//        }
//        // Obserwacja różnych źródeł jednocześnie
//        addSource(rawPhotos)   { rawList   = it; update() }
//        addSource(albumPhotos) { albumList = it; update() }
//        addSource(_filters)    { update() }
//        //addSource(_currentAlbumId) { update() }
//    }

    fun selectAlbum(albumId: String?) {
        _currentAlbumId.value = albumId
    }

    fun applyFilters(sort: String?, tags: List<String>) {
        _filters.value = sort to tags
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
        //val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val meta = Photo(
            title       = title,
            description = description,
            location    = location,
            tags        = tags,
            file_path   = "",
            uploaded_at = Timestamp.now(),
            user_id     = currentUserId
        )

        repository.uploadPhoto(uri, meta, albumId, onSuccess, onFailure)
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

    fun showEditDialog(photos: List<Photo>) {
        selectedPhotos = photos
        isEditDialogVisible = true
        photoBeingEdited = photos.firstOrNull()
    }

    fun updateSelectedPhotos(photos: List<Photo>) {
        selectedPhotos = photos
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
