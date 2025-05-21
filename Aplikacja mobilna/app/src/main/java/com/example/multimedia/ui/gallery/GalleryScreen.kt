package com.example.multimedia.ui.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import com.example.multimedia.data.model.Photo
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos by viewModel.photos.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    val snackbarMessages = remember { Channel<String>(Channel.UNLIMITED) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        for (message in snackbarMessages) {
            snackbarHostState.showSnackbar(message)
        }
    }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            showDialog = true
        }
    }

    selectedImageUris?.let { uri ->
        if (showDialog) {
            PhotoUploadDialog(
                onDismiss = {
                    showDialog = false
                    selectedImageUris.clear() },
                onSubmit = { title, desc, loc, tags ->
                    showDialog = false
                    val failedUris = mutableListOf<Uri>()

                    coroutineScope.launch {
                        selectedImageUris.forEach { uri ->
                            val success = CompletableDeferred<Boolean>()
                            val locationFromExif = getExifLocation(context, uri) ?: ""
                            val location = if (loc.isNotBlank()) loc else locationFromExif

                            viewModel.uploadPhoto(
                                uri = uri,
                                title = title,
                                description = desc,
                                location = location,
                                tags = tags,
                                onSuccess = { success.complete(true) },
                                onFailure = {
                                    failedUris.add(uri)
                                    success.complete(false)
                                }
                            )
                            success.await()
                        }
                        if (failedUris.isEmpty()) {
                            snackbarMessages.trySend("Wszystkie zdjęcia przesłane!")
                        } else {
                            failedUris.forEach {
                                snackbarMessages.trySend("Błąd przesyłania: ${it.lastPathSegment}")
                            }
                        }
                        selectedImageUris.clear()
                    }
                }

            )

        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {

            Text(
                text = "Galeria",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Button(
                onClick = { pickImagesLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text("Dodaj zdjęcia")
            }

            LazyColumn {
                items(photos) { photo ->
                    PhotoItem(photo = photo)
                }
            }
        }
    }
}



@Composable
fun PhotoItem(photo: Photo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberAsyncImagePainter(photo.file_path),
                contentDescription = "Zdjęcie",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = photo.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = photo.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
