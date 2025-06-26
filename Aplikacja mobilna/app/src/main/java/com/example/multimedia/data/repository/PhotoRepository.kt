package com.example.multimedia.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.multimedia.data.model.Photo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val firestore: FirebaseFirestore
) {
    private val storageRef = firebaseStorage.getReferenceFromUrl("gs://image-management-cbaee.firebasestorage.app")

    fun uploadPhoto(
        photoUri: Uri,
        meta: Photo,
        albumId: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val photoRef = storageRef.child("photos/$fileName")

        photoRef.putFile(photoUri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                photoRef.downloadUrl
            }.addOnSuccessListener { uri ->
                val photoToSave = meta.copy(
                    file_path   = uri.toString(),
                    uploaded_at = Timestamp.now()
                )

                firestore.collection("photos")
                    .add(photoToSave)
                    .addOnSuccessListener { docRef ->
                        if (albumId != null)
                        {
                            addPhotoToAlbum(albumId, docRef.id)
                        }
                        onSuccess() }
                    .addOnFailureListener { onFailure(it) }

            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPhotos(currentUserId: String?, albumId: String?): LiveData<List<Photo>> =
        if (albumId.isNullOrEmpty()) {
            // zwykła galeria: publiczne + moje prywatne
            MutableLiveData<List<Photo>>().also { live ->
                firestore.collection("photos")
                    .addSnapshotListener { snap, _ ->
                        val all = snap?.documents
                            ?.mapNotNull { it.toObject(Photo::class.java)?.copy(id = it.id) }
                            ?: emptyList()
                        live.value = all.filter { it.user_id == null || it.user_id == currentUserId }
                    }
            }
        } else {
            // podgaleria: najpierw pobierz powiązania album_photos → listę photoId
            val idsLive = MutableLiveData<List<String>>()
            firestore.collection("album_photos")
                .whereEqualTo("album_id", albumId)
                .addSnapshotListener { snap, _ ->
                    idsLive.value = snap?.documents?.mapNotNull { it.getString("photo_id") } ?: emptyList()
                }

            // otem, gdy zmieni się lista id, pobierz dokumenty photos
            MediatorLiveData<List<Photo>>().also { photosLive ->
                photosLive.addSource(idsLive) { ids ->
                    if (ids.isEmpty()) {
                        photosLive.value = emptyList()
                    } else {
                        firestore.collection("photos")
                            .whereIn(FieldPath.documentId(), ids)
                            .addSnapshotListener { snap, _ ->
                                photosLive.value = snap
                                    ?.documents
                                    ?.mapNotNull { it.toObject(Photo::class.java)?.copy(id = it.id) }
                                    ?: emptyList()
                            }
                    }
                }
            }
        }


    fun getPhotosByIds(ids: List<String>): LiveData<List<Photo>> {
        val live = MutableLiveData<List<Photo>>()
        if (ids.isEmpty()) {
            live.value = emptyList()
        } else {
            firestore.collection("photos")
                .whereIn(FieldPath.documentId(), ids)
                .get()
                .addOnSuccessListener { snap ->
                    live.value = snap.documents.mapNotNull { it.toObject(Photo::class.java)?.copy(id = it.id) }
                }
                .addOnFailureListener { live.value = emptyList() }
        }
        return live
    }

    fun deletePhoto(photo: Photo, onComplete: () -> Unit, onError: (Exception) -> Unit) {
        firestore.collection("photos").document(photo.id ?: "")
            .delete()
            .addOnSuccessListener {
                firebaseStorage.getReferenceFromUrl(photo.file_path)
                    .delete()
                    .addOnSuccessListener { onComplete() }
                    .addOnFailureListener { onError(it) }
            }
            .addOnFailureListener { onError(it) }
    }

    fun updatePhoto(photo: Photo) {
        firestore.collection("photos")
            .document(photo.id)
            .set(photo)
    }

    fun downloadPhotoToFolder(
        context: Context,
        folder: DocumentFile,
        photo: Photo
    ): Boolean {
        return try {
            val url = photo.file_path
            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connect()
            val inputStream = connection.inputStream
            val bytes = inputStream.readBytes()

            Log.d("DownloadPhoto","⤵️ Pobieranie z: ${photo.file_path}")

            val fileName = "${photo.title ?: "zdjecie"}.jpg"
            val file = folder.createFile("image/jpeg", fileName)

            file?.uri?.let { uri ->
                context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
                true
            } ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun addPhotoToAlbum(
        albumId: String,
        photoId: String
    ) {
        val map = mapOf(
            "album_id"   to albumId,
            "photo_id"   to photoId,
        )
        firestore
            .collection("album_photos")
            .add(map)
    }

}
