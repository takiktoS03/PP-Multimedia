package com.example.multimedia.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.multimedia.data.model.Album
import com.example.multimedia.data.model.AlbumPhoto
import com.example.multimedia.data.model.Photo
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlbumRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val photoRepository: PhotoRepository
) {
    private val userId get() = FirebaseAuth.getInstance().currentUser?.uid

    /** Pobierz wszystkie albumy zalogowanego użytkownika */
    fun getAlbumsForUser(): LiveData<List<Album>> {
        val live = MutableLiveData<List<Album>>()
        firestore.collection("albums")
            .whereEqualTo("user_id", userId)
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents
                    ?.mapNotNull { it.toObject(Album::class.java)?.copy(id = it.id) }
                    ?: emptyList()
                live.value = list
            }
        return live
    }

    fun getPhotosInAlbum(albumId: String): LiveData<List<Photo>> {
        val result = MutableLiveData<List<Photo>>()

        // krok 1: pobierz powiązania album–photo
        firestore.collection("album_photos")
            .whereEqualTo("album_id", albumId)
            .addSnapshotListener { snap, _ ->
                val ids = snap
                    ?.documents
                    ?.mapNotNull { it.getString("photo_id") }
                    ?: emptyList()

                if (ids.isEmpty()) {
                    result.value = emptyList()
                    return@addSnapshotListener
                }

                // krok 2: pobierz obiekty Photo o tych ID
                firestore.collection("photos")
                    .whereIn(FieldPath.documentId(), ids)
                    .addSnapshotListener { photoSnap, _ ->
                        val photos = photoSnap
                            ?.documents
                            ?.mapNotNull { it.toObject(Photo::class.java)?.copy(id = it.id) }
                            ?: emptyList()
                        result.value = photos
                    }
            }

        return result
    }

    /** Stwórz nowy album */
    fun createAlbum(
        name: String,
        description: String
    ) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val docRef = firestore.collection("albums").document()
        val album = Album(
            id          = docRef.id,
            user_id     = uid,
            name        = name,
            description = description,
            created_at  = Timestamp.now()
        )
        docRef.set(album)
    }

    /** Usuń cały album wraz ze zdjęciami i powiązaniami w album_photos */
    fun deleteAlbum(albumId: String, onComplete: () -> Unit, onError: (Exception) -> Unit) {
        // krok 1: pobierz wszystkie dokumenty album_photos dla tego albumu
        firestore.collection("album_photos")
            .whereEqualTo("album_id", albumId)
            .get()
            .addOnSuccessListener { snap ->
                val batch = firestore.batch()
                // usuń powiązania
                for (doc in snap.documents) {
                    batch.delete(doc.reference)
                }
                // usuń sam album
                batch.delete(firestore.collection("albums").document(albumId))
                // commit
                batch.commit()
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    /** Pobierz listę identyfikatorów zdjęć przypisanych do danego albumu */
    fun getPhotoIdsForAlbum(albumId: String): LiveData<List<String>> {
        val live = MutableLiveData<List<String>>()
        firestore.collection("album_photos")
            .whereEqualTo("album_id", albumId)
            .addSnapshotListener { snap, _ ->
                live.value = snap?.documents
                    ?.mapNotNull { it.getString("photo_id") }
                    ?: emptyList()
            }
        return live
    }

    /** Dodaj zdjęcia do albumu */
    fun addPhotosToAlbum(albumId: String, photoIds: List<String>) {
        photoIds.forEach { pid ->
            val ap = AlbumPhoto(album_id = albumId, photo_id = pid)
            firestore.collection("album_photos").add(ap)
        }
    }
}
