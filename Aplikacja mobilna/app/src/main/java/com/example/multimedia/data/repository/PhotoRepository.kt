package com.example.multimedia.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.multimedia.data.model.Photo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
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
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileName = "${UUID.randomUUID()}.jpg"
        val photoRef = storageRef.child("photos/$fileName")

        //if (!task.isSuccessful) throw task.exception ?: Exception("Upload failed")
        //val uploadTask = photoRef.putFile(photoUri)

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
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }

            }.addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPhotos(currentUserId: String?): LiveData<List<Photo>> {
        val photosLiveData = MutableLiveData<List<Photo>>()

        firestore.collection("photos")
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    photosLiveData.value = emptyList()
                    return@addSnapshotListener
                }

                val all = snapshots.documents.mapNotNull { doc ->
                    doc.toObject(Photo::class.java)?.copy(id = doc.id)
                }

                // filtrujemy publiczne (user_id == null) + swoje
                val filtered = all.filter { photo ->
                    photo.user_id == null || photo.user_id == currentUserId
                }

                photosLiveData.value = filtered
            }

        return photosLiveData
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

}
