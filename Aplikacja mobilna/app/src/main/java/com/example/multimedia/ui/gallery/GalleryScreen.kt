package com.example.multimedia.ui.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import coil.compose.rememberAsyncImagePainter
import com.example.multimedia.data.model.Photo
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos by viewModel.photos.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    val snackbarMessages = remember { Channel<String>(Channel.UNLIMITED) }
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedPhotoIds = remember { mutableStateListOf<String>() }
    var selectionMode by remember { mutableStateOf(false) }

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

    selectedImageUris.let { uri ->
        if (showDialog) {
            PhotoUploadDialog(
                onDismiss = {
                    showDialog = false
                    selectedImageUris.clear() },
                onSubmit = { title, desc, loc, tags ->
                    showDialog = false
                    val failedUris = mutableListOf<Uri>()

                    coroutineScope.launch {
                        if (selectionMode && selectedPhotoIds.isNotEmpty()) {
                            val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                            selectedPhotos.forEach { photo ->
                                val updatedPhoto = photo.copy(
                                    title = title,
                                    description = desc,
                                    location = loc,
                                    tags = tags
                                )
                                viewModel.updatePhoto(updatedPhoto)
                            }
                            snackbarMessages.trySend("Zaktualizowano ${selectedPhotoIds.size} zdjęć.")
                            selectedPhotoIds.clear()
                            selectionMode = false
                        } else if (selectedImageUris.isNotEmpty()) {
                            selectedImageUris.forEach { uri ->
                                val success = CompletableDeferred<Boolean>()
                                val locationFromExif = getExifLocation(context, uri) ?: ""
                                val location = loc.ifBlank { locationFromExif }

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
                }


            )

        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (selectionMode) {
                TopAppBar(
                    title = { Text("${selectedPhotoIds.size} zaznaczone") },
                    actions = {
                        IconButton(onClick = {
                            val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                            selectedPhotos.forEach { viewModel.deletePhoto(it) }
                            selectedPhotoIds.clear()
                            selectionMode = false
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń")
                        }
                        IconButton(onClick = {
                            showDialog = true
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                        }
                    }
                )
            }
        }
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
                items(photos, key = { it.id }) { photo ->
                    val isSelected by remember(selectedPhotoIds, photo.id) {
                        derivedStateOf { selectedPhotoIds.contains(photo.id) }
                    }

                    PhotoItem(
                        photo = photo,
                        isSelected = isSelected,
                        onClick = {
                            if (selectionMode) {
                                if (isSelected) selectedPhotoIds.remove(photo.id)
                                else selectedPhotoIds.add(photo.id)

                                if (selectedPhotoIds.isEmpty()) selectionMode = false
                            }
                        },
                        onLongClick = {
                            if (!selectionMode) {
                                selectionMode = true
                                selectedPhotoIds.add(photo.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PhotoItem(
    photo: Photo,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (isSelected) Modifier.border(2.dp, Color.Red)
                else Modifier
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                    onTap = { onClick() }
                )
            }
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
