package com.example.multimedia.ui.gallery.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.multimedia.data.model.Photo

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
