package com.example.multimedia.ui.gallery

import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import android.location.Location
import androidx.compose.material3.Button
import androidx.compose.ui.Modifier
import com.google.android.gms.location.LocationServices
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.multimedia.data.model.Photo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.isGranted
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoUploadDialog(
    photo: Photo? = null,
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, location: String, tags: List<String>) -> Unit,
    navController: NavController
) {
    var title by remember { mutableStateOf(photo?.title ?: "") }
    var description by remember { mutableStateOf(photo?.description ?: "") }
    var location by remember { mutableStateOf(photo?.location ?: "") }
    var tagsInput by remember { mutableStateOf(photo?.tags?.joinToString(", ") ?: "") }

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    // Nasłuchuj lokalizacji wybranej z mapy
    val savedStateHandle = remember { navController.currentBackStackEntry?.savedStateHandle }
    LaunchedEffect(savedStateHandle?.get<LatLng>("picked_location")) {
        savedStateHandle?.get<LatLng>("picked_location")?.let { latLng ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val country = address.countryName ?: "Nieznany kraj"
                val locality = address.locality ?: "Nieznane miasto"
                location = "$locality, $country"
            } else {
                location = "${latLng.latitude}, ${latLng.longitude}"
            }
            savedStateHandle.remove<LatLng>("picked_location")
        }
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val tags = tagsInput.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                onSubmit(title, description, location, tags)
            }) {
                Text("Wyślij")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        title = { Text("Wpisz dane zdjęcia") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł") })
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") })
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokalizacja") })
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tagi (oddziel przecinkiem)") })

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { navController.navigate("location_picker") }) {
                    Text("Wybierz lokalizację na mapie")
                }

                Button(onClick = {
                    if (locationPermissionState.status.isGranted) {
                        val permissionCheck = ContextCompat.checkSelfPermission(
                            context, android.Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { loc: Location? ->
                                    if (loc != null) {
                                        // Reverse geocoding
                                        val geocoder = Geocoder(context, Locale.getDefault())
                                        val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            val address = addresses[0]
                                            val country = address.countryName ?: "Nieznany kraj"
                                            val locality = address.locality ?: "Nieznane miasto"
                                            location = "$locality, $country"
                                        } else {
                                            location = "Brak danych lokalizacji"
                                        }
                                    } else {
                                        location = "Brak lokalizacji"
                                    }
                                }
                                .addOnFailureListener {
                                    location = "Błąd pobierania lokalizacji"
                                }
                        } else {
                            location = "Brak uprawnień"
                        }
                    } else {
                        locationPermissionState.launchPermissionRequest()
                    }
                }) {
                    Text("Pobierz lokalizację")
                }
            }
        }
    )
}