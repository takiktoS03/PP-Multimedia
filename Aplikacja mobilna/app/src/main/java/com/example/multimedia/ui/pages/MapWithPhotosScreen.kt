package com.example.multimedia.ui.gallery

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.multimedia.data.model.Photo
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import coil.compose.AsyncImage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapWithPhotosScreen(
    viewModel: GalleryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val photos by viewModel.photos.observeAsState(emptyList())
    val context = LocalContext.current

    val geocoder = remember { Geocoder(context, Locale.getDefault()) }
    var selectedPhoto by remember { mutableStateOf<Photo?>(null) }

    val photosWithLocation = photos.mapNotNull { photo ->
        val loc = photo.location.trim()

        val latLngFromCoords = runCatching {
            val parts = loc.split(",").map { it.trim() }
            if (parts.size == 2) {
                val lat = parts[0].toDoubleOrNull()
                val lng = parts[1].toDoubleOrNull()
                if (lat != null && lng != null) LatLng(lat, lng) else null
            } else null
        }.getOrNull()

        if (latLngFromCoords != null) {
            Pair(photo, latLngFromCoords)
        } else {
            val addresses = try {
                geocoder.getFromLocationName(loc, 1)
            } catch (e: Exception) {
                null
            }

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                Pair(photo, LatLng(address.latitude, address.longitude))
            } else {
                null
            }
        }
    }


    val cameraPositionState = rememberCameraPositionState {
        if (photosWithLocation.isNotEmpty()) {
            val firstLocation = photosWithLocation.first().second
            position = CameraPosition.fromLatLngZoom(firstLocation, 5f)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa zdjęć") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Wróć")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)) {

            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                photosWithLocation.forEach { (photo, latLng) ->
                    Marker(
                        state = MarkerState(position = latLng),
                        title = photo.title,
                        snippet = photo.location,
                        onClick = {
                            selectedPhoto = photo
                            false
                        }
                    )
                }
            }
        }
        selectedPhoto?.let { photo ->
            AlertDialog(
                onDismissRequest = { selectedPhoto = null },
                confirmButton = {
                    TextButton(onClick = { selectedPhoto = null }) { Text("Zamknij") }
                },
                title = { Text(photo.title) },
                text = {
                    Column {
                        Text(photo.description)
                        Spacer(modifier = Modifier.height(8.dp))
                        AsyncImage( // Z biblioteki Coil
                            model = photo.file_path,
                            contentDescription = "Miniaturka",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            )
        }
    }
}
