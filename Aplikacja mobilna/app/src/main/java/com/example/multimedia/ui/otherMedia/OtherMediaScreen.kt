package com.example.multimedia.ui.otherMedia

import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.multimedia.data.model.MediaType
import com.example.multimedia.data.model.OtherMedia

@Composable
fun OtherMediaScreen(viewModel: OtherMediaViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var pickedUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    val mediaList by viewModel.mediaList.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        pickedUri = it
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("Dodaj nowe audio/video")
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Tytuł") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = desc,
            onValueChange = { desc = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row {
            Button(onClick = { launcher.launch("*/*") }) {
                Text("Wybierz plik")
            }
            Spacer(Modifier.width(8.dp))
            pickedUri?.let {
                Text(getFileName(context, it))
            }
        }

        Spacer(Modifier.height(8.dp))

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

        Spacer(Modifier.height(24.dp))

        Text("Twoje media:", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(mediaList.size) { i ->
                val item = mediaList[i]
                MediaListItem(item)
            }
        }
    }
}

@Composable
fun MediaListItem(media: OtherMedia) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(enabled = false) {}
    ) {
        Text(text = "🎬 ${media.title}", style = MaterialTheme.typography.bodyLarge)
        Text(text = media.description, style = MaterialTheme.typography.bodySmall)
        Text(text = media.type.name, style = MaterialTheme.typography.labelSmall)
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