package com.example.multimedia.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AccountScreen(onLogout: () -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val isAnonymous = user?.isAnonymous ?: true
    val email = user?.email ?: "Użytkownik gość"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Konto", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Zalogowano jako:")
        Text(text = email, style = MaterialTheme.typography.bodyLarge)

        if (isAnonymous) {
            Text("Tryb gościa", color = MaterialTheme.colorScheme.secondary)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            onLogout()
        }) {
            Text("Wyloguj się")
        }
    }
}
