package com.example.multimedia.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multimedia.ui.home.HomeViewModel

@Composable
fun HomeScreen(title: String, isLoading: Boolean) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    HomeScreen("Preview title", false)
}

@Composable
@Preview
fun HomeScreenPreviewLoading() {
    HomeScreen("Preview title", true)
}