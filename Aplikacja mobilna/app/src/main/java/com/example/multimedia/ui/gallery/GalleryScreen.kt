package com.example.multimedia.ui.gallery

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
// import androidx.compose.ui.draw.background      // ← 🔥 TO BYŁO BRAKUJĄCE!
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
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
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import android.graphics.BitmapFactory
import coil.ImageLoader
import coil.imageLoader
import coil.request.ImageRequest
import android.graphics.drawable.BitmapDrawable
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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

    var expandedPhoto by remember { mutableStateOf<Photo?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

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
            viewModel.setPendingImageUris(uris)
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
                val safeLocation = loc.ifBlank { "Nieznana lokalizacja" }.take(200)
                val safeTags = tags.map { it.trim() }.filter { it.isNotEmpty() }

                if (viewModel.selectedPhotos.isNotEmpty()) {
                    Log.d("GalleryScreen","onSubmit selectedPhotos: title=$title, desc=$desc, loc=$safeLocation, tags=$safeTags")
                    viewModel.selectedPhotos.forEach { photo ->
                        val updated = photo.copy(
                            title = title,
                            description = desc,
                            location = safeLocation,
                            tags = safeTags
                        )
                        viewModel.updatePhoto(updated)
                    }
                    snackbarMessages.trySend("Zaktualizowano ${viewModel.selectedPhotos.size} zdjęć.")
                    selectedPhotoIds.clear()
                    selectionMode = false
                    viewModel.dismissDialog()
                } else if (viewModel.pendingImageUris.isNotEmpty()) {
                    val failedUris = mutableListOf<Uri>()
                    coroutineScope.launch {
                        viewModel.pendingImageUris.forEach { uri ->
                        val success = CompletableDeferred<Boolean>()
                            val locationFromExif = getExifLocation(context, uri) ?: ""
                            val locationToUse = safeLocation.ifBlank { locationFromExif }
                            Log.d("GalleryScreen","onSubmit selectedImageUris: title=$title, desc=$desc, loc=$locationToUse, tags=$safeTags")
                            viewModel.uploadPhoto(
                                uri = uri,
                                title = title,
                                description = desc,
                                location = locationToUse,
                                tags = safeTags,
                                onSuccess = { success.complete(true) },
                                onFailure = { ex ->
                                    Log.e("GalleryScreen", "Upload failed for $uri: ${ex.message}", ex)
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
                        viewModel.clearPendingImageUris()
                        viewModel.dismissDialog()
                        Log.d("GalleryScreen","dismiss called")
                    }
                }
            }
        )
    }

    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Potwierdzenie usunięcia") },
            text = { Text("Czy na pewno chcesz usunąć ${selectedPhotoIds.size} zdjęcie(-ć)?") },
            confirmButton = {
                TextButton(onClick = {
                    val selectedPhotos = photos.filter { selectedPhotoIds.contains(it.id) }
                    selectedPhotos.forEach { viewModel.deletePhoto(it) }
                    selectedPhotoIds.clear()
                    selectionMode = false
                    showDeleteConfirmation = false
                }) {
                    Text("Tak")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Anuluj")
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

    val dynamicTitle = if (selectionMode) "${selectedPhotoIds.size} zaznaczone" else "Galeria"

    DrawerScaffold(
        navController = navController,
        currentRoute = "gallery",
        title = dynamicTitle,
        snackbarHostState = snackbarHostState,
        actions = {
            if (selectionMode) {
                IconButton(onClick = {
                    showDeleteConfirmation = true
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
    { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(8.dp)
        ) {
            if (!selectionMode) {
                Text(
                    text = "Galeria",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            if (!selectionMode) {
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
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos, key = { it.id }) { photo ->
                    val isSelected by remember(selectedPhotoIds, photo.id) {
                        derivedStateOf { selectedPhotoIds.contains(photo.id) }
                    }
                    PhotoItem(
                        photo = photo,
                        isSelected = isSelected,
                        selectionMode = selectionMode,
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
                        },
                        onPreview = { expandedPhoto = it }
                    )
                }
            }
        }
    }

    val dateFormat = remember {
        java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
    }
    val uploadedAtFormatted = expandedPhoto?.uploaded_at?.toDate()?.let { dateFormat.format(it) } ?: "Nieznana"

    if (expandedPhoto != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.95f))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { expandedPhoto = null }) // zamknij po kliknięciu
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(expandedPhoto!!.file_path),
                    contentDescription = "Pełnoekranowy podgląd",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Metadane
                Text(text = "Tytuł: ${expandedPhoto!!.title}", color = Color.White)
                Text(text = "Opis: ${expandedPhoto!!.description}", color = Color.White)
                Text(text = "Lokalizacja: ${expandedPhoto!!.location}", color = Color.White)
                Text(text = "Tagi: ${expandedPhoto!!.tags.joinToString(", ")}", color = Color.White)
                Text(text = "Przesłano: $uploadedAtFormatted", color = Color.White)

                // Te dwie wymagają pobrania z sieci – robimy to niżej
                val context = LocalContext.current
                var fileSize by remember { mutableStateOf("") }
                var resolution by remember { mutableStateOf("") }

                val imageLoader = coil.ImageLoader(context)

                //val imageLoader = coil.imageLoader(context) // użyj domyślnego globalnego loadera

                LaunchedEffect(expandedPhoto?.file_path) {
                    fileSize = "Nieznany"
                    resolution = "Nieznana"

                    try {
                        val url = URL(expandedPhoto!!.file_path)

                        // 1. Pobierz tylko nagłówek z rozmiarem pliku
                        val headConnection = (url.openConnection() as HttpURLConnection).apply {
                            requestMethod = "HEAD"
                            connectTimeout = 5000
                            readTimeout = 5000
                            connect()
                        }

                        val size = headConnection.contentLengthLong
                        if (size > 0) {
                            fileSize = when {
                                size < 1024 -> "$size B"
                                size < 1024 * 1024 -> "${size / 1024} KB"
                                else -> "%.2f MB".format(size / (1024.0 * 1024.0))
                            }
                        }
                        headConnection.disconnect()

                        // 2. Pobierz tylko nagłówki obrazu, by uzyskać rozdzielczość
                        val getConnection = (url.openConnection() as HttpURLConnection).apply {
                            doInput = true
                            connectTimeout = 5000
                            readTimeout = 5000
                            connect()
                        }
                        val inputStream = BufferedInputStream(getConnection.inputStream)

                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeStream(inputStream, null, options)
                        inputStream.close()
                        getConnection.disconnect()

                        if (options.outWidth > 0 && options.outHeight > 0) {
                            resolution = "${options.outWidth} x ${options.outHeight}"
                        }
                    } catch (e: Exception) {
                        Log.e("GalleryScreen", "Błąd podczas pobierania metadanych: ${e.message}")
                    }
                }

                if (fileSize.isNotEmpty()) {
                    Text(text = "Rozmiar: $fileSize", color = Color.White)
                }
                if (resolution.isNotEmpty()) {
                    Text(text = "Rozdzielczość: $resolution", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FullScreenImagePreview(filePath: String) {
    val context = LocalContext.current

    var resolution by remember(filePath) { mutableStateOf("Nieznana") }
    var fileSize by remember(filePath) { mutableStateOf("Nieznana") }
    var imageLoaded by remember(filePath) { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                .weight(1f),
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

        Spacer(modifier = Modifier.height(8.dp))

        Text("Rozdzielczość: $resolution", color = Color.White)
        Text("Rozmiar pliku: $fileSize", color = Color.White)
    }

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

@Composable
fun PhotoItem(
    photo: Photo,
    isSelected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onPreview: (Photo) -> Unit
)
 {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .then(
                if (isSelected) Modifier.border(2.dp, Color.Red)
                else Modifier
            )
            .pointerInput(isSelected) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                    onTap = {
                        if (selectionMode) {
                            onClick()  // zaznacz/odznacz
                        } else {
                            onPreview(photo)  // podgląd
                        }
                    }
                )
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
//            Image(
//                painter = rememberAsyncImagePainter(photo.file_path),
//                contentDescription = "Zdjęcie",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(200.dp)
//            )
            Image(
                painter = rememberAsyncImagePainter(
                    model = photo.file_path
                ),
                contentDescription = "Miniaturka",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // kwadrat
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = photo.title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = photo.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
