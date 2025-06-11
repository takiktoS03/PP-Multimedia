package com.example.multimedia.ui.gallery

import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import java.util.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LocationPickerScreen(
    onLocationPicked: (LatLng) -> Unit,
    onCancel: () -> Unit
) {
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(52.2297, 21.0122), 10f) // Warszawa
    }

    // Pole do wpisywania adresu
    var address by remember { mutableStateOf("") }
    val context = LocalContext.current
    val geocoder = remember { Geocoder(context, Locale.getDefault()) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Mapa
        GoogleMap(
            modifier = Modifier
                .fillMaxSize()
                .pointerInteropFilter { false }, // pozwala na dotyk pól tekstowych
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng -> selectedLocation = latLng }
        ) {
            selectedLocation?.let { location ->
                Marker(
                    state = MarkerState(position = location),
                    title = "Wybrane miejsce"
                )
            }
        }

        // Pasek wyszukiwania na wierzchu mapy
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter)
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Wpisz miasto lub adres") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (address.isNotBlank()) {
                        val locations = geocoder.getFromLocationName(address, 1)
                        if (!locations.isNullOrEmpty()) {
                            val location = locations[0]
                            val latLng = LatLng(location.latitude, location.longitude)
                            // Przesuń mapę
                            cameraPositionState.move(
                                CameraUpdateFactory.newLatLngZoom(latLng, 15f)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = 8.dp)
            ) {
                Text("Szukaj")
            }
        }

        // Przyciski na dole
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Button(
                onClick = { selectedLocation?.let { onLocationPicked(it) } },
                enabled = selectedLocation != null
            ) {
                Text("Zatwierdź lokalizację")
            }

            TextButton(onClick = { onCancel() }) {
                Text("Anuluj")
            }
        }
    }
}
