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

@Composable
fun GalleryScreen(viewModel: GalleryViewModel = hiltViewModel()) {
    val photos by viewModel.photos.observeAsState(emptyList())

    var showDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedImageUri = it
            showDialog = true
        }
    }

    selectedImageUri?.let { uri ->
        if (showDialog) {
            PhotoUploadDialog(
                onDismiss = { showDialog = false },
                onSubmit = { title, desc, loc, tags ->
                    showDialog = false

                    val locationFromExif = getExifLocation(context, uri) ?: ""
                    val location = if (loc.isNotBlank()) loc else locationFromExif

                    viewModel.uploadPhoto(
                        uri = uri,
                        title = title,
                        description = desc,
                        location = location,
                        tags = tags,
                        onSuccess = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Zdjęcie przesłane!")
                            }
                        },
                        onFailure = { error ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Błąd: ${error.localizedMessage}")
                            }
                        }
                    )
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
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
            ) {
                Text("Dodaj zdjęcie")
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
