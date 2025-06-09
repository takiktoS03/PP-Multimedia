package com.example.multimedia.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.multimedia.MainActivity
import com.example.multimedia.ui.veryfication.VerificationViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VerificationScreen(navController: NavController, userId: String) {
    var codeInput by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var success by remember { mutableStateOf(false) }
    val verificationViewModel: VerificationViewModel = viewModel()

    LaunchedEffect(success) {
        if (success) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true } // czyści cały stos i wraca do login
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // 🔙 Przycisk "Wróć"
        TextButton(onClick = {
            val popped = navController.popBackStack()
            if (!popped) {
                navController.navigate("login") { popUpTo(0) { inclusive = true } }
            }
        }) {
            Text("← Wróć", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxSize(),
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
                println("🔍 Sprawdzam kod w Firestore...")
                val db = FirebaseFirestore.getInstance()
                println("Firestore używany w ekranie: ${db.app.name}")

                db.collection("verifications").document(userId).get()
                    .addOnSuccessListener { document ->
                        val correctCode = document.getString("code")
                        println("✔️ Kod z bazy: $correctCode")
                        if (codeInput == correctCode) {
                            db.collection("users").document(userId)
                                .update("isVerified", true)
                                .addOnSuccessListener {
                                    println("✔️ Zaktualizowano isVerified w bazie")
                                    success = true
                                }
                                .addOnFailureListener {
                                    error = "❌ Błąd zapisu weryfikacji"
                                    println("❌ Nie udało się zaktualizować Firestore")
                                }
                        } else {
                            error = "Niepoprawny kod"
                            println("❌ Niepoprawny kod")
                        }
                    }
                    .addOnFailureListener {
                        error = "❌ Błąd pobierania kodu z bazy"
                        println("❌ Błąd pobierania dokumentu verifications")
                    }
            }) {
                Text("Zweryfikuj")
            }

            if (success) {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Kod poprawny. Przekierowanie...", color = MaterialTheme.colorScheme.primary)
            }

            error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
