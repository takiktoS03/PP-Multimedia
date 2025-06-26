package com.example.multimedia.ui.album

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.multimedia.data.model.Album
import com.example.multimedia.data.model.Photo
import com.example.multimedia.data.repository.AlbumRepository
import com.example.multimedia.data.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumViewModel @Inject constructor(
    private val albumRepo: AlbumRepository,
    private val photoRepo: PhotoRepository
) : ViewModel() {

    val albums: LiveData<List<Album>> = albumRepo.getAlbumsForUser()

    // ID aktualnie wybranego albumu
    private val _currentAlbumId = MutableLiveData<String?>(null)
    val currentAlbumId: LiveData<String?> = _currentAlbumId

//    val currentAlbumPhotos: LiveData<List<Photo>>
//
//    init {
//        // 1) LiveData ze zmieniającą się listą photoIds:
//        val photoIds = MediatorLiveData<List<String>>().apply {
//            fun switch(id: String?) {
//                sources.forEach { removeSource((it as LiveData<*>)) }
//                if (id == null) value = emptyList()
//                else addSource(albumRepo.getPhotoIdsForAlbum(id)) { value = it }
//            }
//            // gdy albumId się zmieni, przerejestruj źródło:
//            addSource(_currentAlbumId) { switch(it) }
//        }
//
//        // 2) LiveData przetwarzająca te photoIds → pełne Photo:
//        currentAlbumPhotos = MediatorLiveData<List<Photo>>().apply {
//            addSource(photoIds) { ids ->
//                // gdy zmienią się id, podłączemy źródło photoRepo.getPhotosByIds(ids)
//                sources.forEach { removeSource((it as LiveData<*>)) }
//                if (ids.isEmpty()) value = emptyList()
//                else addSource(photoRepo.getPhotosByIds(ids)) { value = it }
//            }
//        }
//    }

    fun selectAlbum(albumId: String) {
        _currentAlbumId.value = albumId
    }

    fun createAlbum(name: String, desc: String ) =
        albumRepo.createAlbum(name, desc)

    fun deleteAlbums(
        albumIds: List<String>,
        onComplete: () -> Unit = {},
        onError: (Exception) -> Unit = {}
    ) {
        if (albumIds.isEmpty()) return onComplete()

        val remaining = albumIds.toMutableList()
        albumIds.forEach { id ->
            albumRepo.deleteAlbum(id,
                onComplete = {
                    remaining.remove(id)
                    if (remaining.isEmpty()) onComplete()
                },
                onError = { e ->
                    onError(e)
                }
            )
        }
    }

    fun addPhotosToAlbum(photoIds: List<String>) {
        _currentAlbumId.value?.let { albumId ->
            albumRepo.addPhotosToAlbum(albumId, photoIds)
        }
    }

    fun uploadPhotoIntoAlbum(
        uri: Uri,
        meta: Photo,
        albumId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        photoRepo.uploadPhoto(uri, meta, albumId, onSuccess, onFailure)
    }
}
