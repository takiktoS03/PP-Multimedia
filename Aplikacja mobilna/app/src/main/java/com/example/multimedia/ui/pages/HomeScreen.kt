package com.example.multimedia.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.multimedia.ui.home.HomeViewModel
import com.example.multimedia.R


@Composable
fun HomeScreen(title: String, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "👋 Witaj w MultimediaApp!",
                style = MaterialTheme.typography.headlineLarge,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = """
                    Aplikacja służy jako centrum multimedialne, w którym możesz:
                    
                    • Przeglądać galerię zdjęć i filmów 📷
                    • Zarządzać swoim kontem użytkownika 👤
                    
                    Po lewej stronie znajduje się panel nawigacyjny (menu), z którego możesz przełączać się pomiędzy:
                    
                    🏠 Stroną główną  
                    🖼️ Galerią  
                    👤 Panelem konta
                    
                    Kliknij ikonę menu w lewym górnym rogu, aby rozwinąć lub schować panel. 
                    Zalogowany użytkownik może przeglądać zasoby i edytować dane konta.
                    
                    Życzymy miłego korzystania!
                """.trimIndent(),
                style = MaterialTheme.typography.bodyLarge,
                lineHeight = 22.sp
            )
        }
    }
}
