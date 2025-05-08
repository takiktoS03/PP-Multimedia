package com.example.multimedia.ui.gallery

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import java.io.InputStream

fun getExifLocation(context: Context, uri: Uri): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        inputStream?.use {
            val exif = ExifInterface(it)
            val latLong = FloatArray(2)
            return if (exif.getLatLong(latLong)) {
                "${latLong[0]},${latLong[1]}"
            } else {
                null
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
