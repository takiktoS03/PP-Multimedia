package com.example.multimedia.ui.gallery

import android.content.Intent
import android.net.Uri
import android.util.Log
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.documentfile.provider.DocumentFile
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.multimedia.R
import com.example.multimedia.data.model.Photo
import com.example.multimedia.ui.sideBar.DrawerScaffold
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    navController: NavController
) {
    val photos by viewModel.photos.observeAsState(emptyList())
    val showDialog        by remember { derivedStateOf { viewModel.isEditDialogVisible } }
    val editingPhoto      by remember { derivedStateOf { viewModel.photoBeingEdited } }

    val snackbarMessages = remember { Channel<String>(Channel.UNLIMITED) }
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedPhotoIds = remember { mutableStateListOf<String>() }
    var selectionMode by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Snackbar loop
    LaunchedEffect(Unit) {
        for (message in snackbarMessages) {
            snackbarHostState.showSnackbar(message)
        }
    }

    val selectedImageUris = remember { mutableStateListOf<Uri>() }
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImageUris.clear()
            selectedImageUris.addAll(uris)
            viewModel.showEditDialog(emptyList())
        }
    }

    val folderPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri != null) {
            val folder = DocumentFile.fromTreeUri(context, uri)
            val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, takeFlags)
            if (folder != null) {
                viewModel.downloadPhotosToFolder(context, folder) { success ->
                    if (success) {
                        snackbarMessages.trySend("Zdjęcia zapisane.")
                    } else {
                        snackbarMessages.trySend("Niektóre zdjęcia nie zostały zapisane.")
                    }
                    selectedPhotoIds.clear()
                    selectionMode = false
                }
            }
        }
    }

    if (showDialog) {
        PhotoUploadDialog(
            photo = editingPhoto,
            navController = navController,
            onDismiss = {
                viewModel.dismissDialog()
            },
            onSubmit = { title, desc, loc, tags ->
                if (viewModel.selectedPhotos.isNotEmpty()) {
                    Log.d("GalleryScreen","onSubmit selectedPhotos: title=$title, desc=$desc, loc=$loc, tags=$tags")
                    viewModel.selectedPhotos.forEach { photo ->
                        val updated = photo.copy(
                            title = title,
                            description = desc,
                            location = loc,
                            tags = tags
                        )
                        viewModel.updatePhoto(updated)
                    }
                    snackbarMessages.trySend("Zaktualizowano ${viewModel.selectedPhotos.size} zdjęć.")
                    selectedPhotoIds.clear()
                    selectionMode = false
                    viewModel.dismissDialog()
                }
                else if (selectedImageUris.isNotEmpty()) {
                    val failedUris = mutableListOf<Uri>()
                    coroutineScope.launch {
                        selectedImageUris.forEach { uri ->
                            val success = CompletableDeferred<Boolean>()
                            val locationFromExif = getExifLocation(context, uri) ?: ""
                            val locationToUse = loc.ifBlank { locationFromExif }
                            Log.d("GalleryScreen","onSubmit selectedImageUris: title=$title, desc=$desc, loc=$loc, tags=$tags")
                            viewModel.uploadPhoto(
                                uri = uri,
                                title = title,
                                description = desc,
                                location = locationToUse,
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
                        viewModel.dismissDialog()
                        Log.d("GalleryScreen","dismiss called")
                    }
                }
            }
        )
    }

    if (showFilterSheet) {
        ModalBottomSheet(onDismissRequest = { showFilterSheet = false }) {
            FilterContent(
                onApply = { selectedSort, selectedTags ->
                    viewModel.applyFilters(selectedSort, selectedTags)
                    showFilterSheet = false
                }
            )
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    if (selectionMode) {
                        Text("${selectedPhotoIds.size} zaznaczone")
                    } else {
                        Text("Galeria")
                    }
                },
                actions = {
                    if (selectionMode) {
                        IconButton(onClick = {
                            val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                            selectedPhotos.forEach { viewModel.deletePhoto(it) }
                            selectedPhotoIds.clear()
                            selectionMode = false
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Usuń")
                        }

                        IconButton(onClick = {
                            val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                            viewModel.showEditDialog(selectedPhotos)
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edytuj")
                        }

                        IconButton(onClick = {
                            val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                            folderPickerLauncher.launch(null)
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Pobierz")
                        }
                    }
                }
            )
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

            Button(
                onClick = { showFilterSheet = true },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text("Filtruj / Sortuj")
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
