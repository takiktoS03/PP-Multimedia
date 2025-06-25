package com.example.multimedia.ui.gallery.components

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.multimedia.data.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun FullScreenImagePreview(filePath: String, photo: Photo) {
    val context = LocalContext.current

    var resolution by remember(filePath) { mutableStateOf("Nieznana") }
    var fileSize by remember(filePath) { mutableStateOf("Nieznana") }
    var imageLoaded by remember(filePath) { mutableStateOf(false) }

    var detailsVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 500.dp)
            .verticalScroll(scrollState)
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        // Obraz
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(filePath)
                .allowHardware(false)
                .crossfade(true)
                .build(),
            contentDescription = "Podgląd zdjęcia",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp),
            onSuccess = { success ->
                val drawable = success.result.drawable
                val (width, height) = if (drawable is BitmapDrawable) {
                    drawable.bitmap.width to drawable.bitmap.height
                } else {
                    drawable.intrinsicWidth to drawable.intrinsicHeight
                }
                resolution = "${width} x ${height}"
                imageLoaded = true
            },
            onError = {
                resolution = "Nieznana"
                fileSize = "Nieznany"
                imageLoaded = false
            }
        )

        // Przycisk rozwijania
        IconButton(
            onClick = { detailsVisible = !detailsVisible },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Icon(
                imageVector = if (detailsVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowDown,
                contentDescription = if (detailsVisible) "Zwiń" else "Pokaż szczegóły",
                tint = Color.White
            )
        }

        // Rozwijane informacje
        AnimatedVisibility(visible = detailsVisible) {
            Column(modifier = Modifier.padding(16.dp)) {
                val dateFormat = remember {
                    java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
                }
                val dateFormatted = photo.uploaded_at?.toDate()?.let { dateFormat.format(it) } ?: "Nieznana"

                Text("Tytuł: ${photo.title}", color = Color.White)
                Text("Opis: ${photo.description}", color = Color.White)
                Text("Lokalizacja: ${photo.location}", color = Color.White)
                Text("Tagi: ${photo.tags.joinToString(", ")}", color = Color.White)
                Text("Przesłano: $dateFormatted", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rozdzielczość: $resolution px", color = Color.White)
                Text("Rozmiar pliku: $fileSize", color = Color.White)
            }
        }
    }

    // Pobieranie metadanych
    LaunchedEffect(filePath, imageLoaded) {
        if (imageLoaded) {
            try {
                val contentLength = withContext(Dispatchers.IO) {
                    var connection: HttpURLConnection? = null
                    try {
                        connection = (URL(filePath).openConnection() as HttpURLConnection).apply {
                            requestMethod = "HEAD"
                            connect()
                        }
                        connection.contentLengthLong
                    } finally {
                        connection?.disconnect()
                    }
                }

                fileSize = if (contentLength >= 0) {
                    when {
                        contentLength < 1024 -> "$contentLength B"
                        contentLength < 1024 * 1024 -> "${contentLength / 1024} KB"
                        else -> String.format("%.2f MB", contentLength / (1024.0 * 1024.0))
                    }
                } else {
                    "Nieznany"
                }
            } catch (e: Exception) {
                fileSize = "Nieznany"
            }
        }
    }
}

