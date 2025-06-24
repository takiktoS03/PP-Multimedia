package com.example.multimedia.ui.gallery

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.multimedia.data.model.Photo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PhotoUploadDialog(
    photo: Photo? = null,
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, location: String, tags: List<String>) -> Unit,
    navController: NavController,
    viewModel: GalleryViewModel = hiltViewModel()
) {
    var title by rememberSaveable { mutableStateOf(photo?.title ?: "") }
    var description by rememberSaveable { mutableStateOf(photo?.description ?: "") }
    var location by rememberSaveable { mutableStateOf(photo?.location ?: "") }
    var tagsInput by rememberSaveable { mutableStateOf(photo?.tags?.joinToString(", ") ?: "") }

    val context = LocalContext.current
    val locationPermissionState = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    // Nasłuchuj lokalizacji wybranej z mapy
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    LaunchedEffect(savedStateHandle?.get<LatLng>("picked_location")) {
        savedStateHandle?.get<LatLng>("picked_location")?.let { latLng ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val country = address.countryName ?: "Nieznany kraj"
                val locality = address.locality ?: "Nieznane miasto"
                val street = address.thoroughfare ?: "Nieznana ulica"
                val number = address.subThoroughfare ?: "Nieznany numer"
                location = "$locality, $country, $street, $number"
                location = listOfNotNull(locality, country, street ,number)
                    .filter { it.isNotBlank() }
                    .joinToString(", ")
            } else {
                location = "${latLng.latitude}, ${latLng.longitude}"
            }
            savedStateHandle.remove<LatLng>("picked_location")
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title   = { Text(if (photo == null) "Dodaj zdjęcie" else "Edytuj zdjęcie") },
        confirmButton = {
            TextButton(onClick = {
                val tags = tagsInput
                    .split(",")
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                Log.d("PhotoUploadDialog", "SUBMIT: title=$title, desc=$description, loc=$location, tags=$tags")
                onSubmit(title, description, location, tags)
                Log.d("PhotoUploadDialog", "CALLING onDismiss()")
                onDismiss()
            },
                //enabled = title.isNotBlank() && description.isNotBlank()
            ) {
                Text("Wyślij")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Anuluj")
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tytuł") }
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Opis") }
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Lokalizacja") }
                )
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Tagi (oddziel przecinkiem)") }
                )

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
                            fusedLocationClient.getCurrentLocation(
                                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
                            ).addOnSuccessListener { loc: Location? ->
                                if (loc != null) {
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
                                    location = "Brak włączonej lokalizacji"
                                }
                            }.addOnFailureListener {
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
