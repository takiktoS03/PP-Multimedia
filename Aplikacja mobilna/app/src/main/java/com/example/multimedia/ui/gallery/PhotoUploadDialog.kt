package com.example.multimedia.ui.gallery

import androidx.compose.foundation.layout.*
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*


@Composable
fun PhotoUploadDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, description: String, location: String, tags: List<String>) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var tagsInput by remember { mutableStateOf("") }

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
        title = { Text("Dodaj zdjęcie") },
        text = {
            Column {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Tytuł") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Opis") })
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Lokalizacja") })
                OutlinedTextField(value = tagsInput, onValueChange = { tagsInput = it }, label = { Text("Tagi (oddziel przecinkiem)") })
            }
        }
    )
}
