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

    private val _currentAlbumId = MutableLiveData<String?>(null)

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
