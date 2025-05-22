package com.example.multimedia.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VerificationScreen(navController: NavController, userId: String) {
    var codeInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Wprowadź kod weryfikacyjny wysłany na e-mail")

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = { codeInput = it },
            label = { Text("Kod weryfikacyjny") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val db = FirebaseFirestore.getInstance()
            db.collection("verifications").document(userId).get()
                .addOnSuccessListener { document ->
                    val correctCode = document.getString("code")
                    if (codeInput == correctCode) {
                        db.collection("users").document(userId)
                            .update("isVerified", true)
                        success = true
                    } else {
                        error = "Niepoprawny kod"
                    }
                }
        }) {
            Text("Zweryfikuj")
        }

        if (success) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("Kod poprawny. Konto zostało zweryfikowane!", color = MaterialTheme.colorScheme.primary)
        }

        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
