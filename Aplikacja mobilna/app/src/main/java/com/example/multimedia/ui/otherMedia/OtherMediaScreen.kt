package com.example.multimedia.ui.otherMedia

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multimedia.R
import com.example.multimedia.data.model.MediaType
import com.example.multimedia.data.model.OtherMedia

@Composable
fun OtherMediaScreen(viewModel: OtherMediaViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val mediaList by viewModel.mediaList.collectAsState()

    var editableItem by remember { mutableStateOf<OtherMedia?>(null) }
    var editedTitle by remember { mutableStateOf("") }
    var editedDesc by remember { mutableStateOf("") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val audioList = mediaList.filter { it.type == MediaType.AUDIO }
    val videoList = mediaList.filter { it.type == MediaType.VIDEO }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        pickedUri = it
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("Dodaj nowe audio/video")
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Tytuł") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            OutlinedTextField(
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Row {
                Button(onClick = { launcher.launch("*/*") }) {
                    Text("Wybierz plik")
                }
                Spacer(Modifier.width(8.dp))
                pickedUri?.let {
                    Text(getFileName(context, it))
                }
            }
        }

        item {
            Button(
                enabled = pickedUri != null && title.isNotBlank(),
                onClick = {
                    val type = if (pickedUri.toString().contains("audio")) MediaType.AUDIO else MediaType.VIDEO
                    pickedUri?.let {
                        viewModel.uploadMedia(it, title, desc, type)
                        title = ""
                        desc = ""
                        pickedUri = null
                    }
                }) {
                Text("Dodaj")
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text("🎧 Pliki audio", style = MaterialTheme.typography.titleMedium)
        }

        items(audioList) { item ->
            if (editableItem?.id == item.id) {
                EditMediaItem(
                    title = editedTitle,
                    description = editedDesc,
                    onTitleChange = { editedTitle = it },
                    onDescriptionChange = { editedDesc = it },
                    onSave = {
                        viewModel.updateMedia(item.id, editedTitle, editedDesc, item.type)
                        editableItem = null
                    },
                    onCancel = { editableItem = null },
                    onDelete = { showDeleteDialog = true }
                )
            } else {
                MediaListItem(item, onLongPress = {
                    editableItem = item
                    editedTitle = item.title
                    editedDesc = item.description
                })
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            Text("🎥 Pliki wideo", style = MaterialTheme.typography.titleMedium)
        }

        items(videoList) { item ->
            if (editableItem?.id == item.id) {
                EditMediaItem(
                    title = editedTitle,
                    description = editedDesc,
                    onTitleChange = { editedTitle = it },
                    onDescriptionChange = { editedDesc = it },
                    onSave = {
                        viewModel.updateMedia(item.id, editedTitle, editedDesc, item.type)
                        editableItem = null
                    },
                    onCancel = { editableItem = null },
                    onDelete = { showDeleteDialog = true }
                )
            } else {
                MediaListItem(item, onLongPress = {
                    editableItem = item
                    editedTitle = item.title
                    editedDesc = item.description
                })
            }
        }
    }

    // 🔴 Potwierdzenie usuwania
    if (showDeleteDialog && editableItem != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Potwierdź usunięcie") },
            text = { Text("Czy na pewno chcesz usunąć ten plik? Tej operacji nie można cofnąć.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteMedia(editableItem!!.id, editableItem!!.type)
                        editableItem = null
                        showDeleteDialog = false
                    }
                ) {
                    Text("Usuń", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Anuluj")
                }
            }
        )
    }
}

@Composable
fun MediaListItem(media: OtherMedia, onLongPress: () -> Unit = {}) {
    val iconRes = when (media.type) {
        MediaType.AUDIO -> R.drawable.baseline_audiotrack_24
        MediaType.VIDEO -> R.drawable.baseline_videocam_24
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .pointerInput(Unit) {
                detectTapGestures(onLongPress = { onLongPress() })
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        ListItem(
            leadingContent = {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null
                )
            },
            headlineContent = { Text(media.title) },
            supportingContent = {
                Column {
                    Text(media.description, style = MaterialTheme.typography.bodySmall)
                    Text(media.type.name, style = MaterialTheme.typography.labelSmall)
                }
            }
        )
    }
}

@Composable
fun EditMediaItem(
    title: String,
    description: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Nowy tytuł") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = onDescriptionChange,
                label = { Text("Nowy opis") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(Modifier.padding(top = 8.dp)) {
                Button(onClick = onSave) {
                    Text("Zapisz")
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onCancel) {
                    Text("Anuluj")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Usuń")
                }
            }
        }
    }
}

fun getFileName(context: android.content.Context, uri: Uri): String {
    var name = "plik"
    val cursor = context.contentResolver.query(uri, null, null, null, null)
    cursor?.use {
        val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (it.moveToFirst() && nameIndex >= 0) {
            name = it.getString(nameIndex)
        }
    }
    return name
}
