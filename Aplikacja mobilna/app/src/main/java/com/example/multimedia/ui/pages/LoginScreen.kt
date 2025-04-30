package com.example.multimedia.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestoreSettings

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (isRegistering) "Zarejestruj się" else "Zaloguj się",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isRegistering) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Imię") }
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isRegistering) {
            Button(onClick = {
                if (email.isBlank() || password.isBlank() || name.isBlank()) {
                    error = "Wprowadź imię, email i hasło"
                    return@Button
                }

                FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        Log.d("Register", "UID: $uid")

                        val userData = hashMapOf(
                            "email" to email,
                            "name" to name,
                            "created_at" to FieldValue.serverTimestamp()
                        )

                        FirebaseFirestore.getInstance("image-db")
                            .collection("users")
                            .document(uid ?: "")
                            .set(userData)
                            .addOnSuccessListener {
                                Log.d("Register", "Zapisano do Firestore")
                                onLoginSuccess()
                            }
                            .addOnFailureListener {
                                Log.e("Register", "Błąd zapisu do Firestore: ${it.message}")
                            }
                    }
                    .addOnFailureListener {
                        Log.e("Register", "Rejestracja nie powiodła się: ${it.message}")
                    }

            }) {
                Text("Zarejestruj się")
            }

            TextButton(onClick = {
                isRegistering = false
                error = null
            }) {
                Text("Masz już konto? Zaloguj się")
            }
        }
        else {
            Button(onClick = {
                if (email.isBlank() || password.isBlank()) {
                    error = "Wprowadź email i hasło"
                    return@Button
                }

                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { onLoginSuccess() }
                    .addOnFailureListener {
                        error = "Niepoprawny email lub hasło"
                    }
            }) {
                Text("Zaloguj się")
            }

            TextButton(onClick = {
                isRegistering = true
                error = null
            }) {
                Text("Nie masz konta? Zarejestruj się")
            }
        }

        TextButton(onClick = {
            FirebaseAuth.getInstance()
                .signInAnonymously()
                .addOnSuccessListener { onContinueAsGuest() }
                .addOnFailureListener {
                    error = "Logowanie gościa nie powiodło się: ${it.message}"
                }
        }) {
            Text("Kontynuuj jako gość")
        }

        if (error != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
