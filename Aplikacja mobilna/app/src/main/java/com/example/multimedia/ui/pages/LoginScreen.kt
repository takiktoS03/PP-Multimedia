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
import java.security.MessageDigest

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var email               by remember { mutableStateOf("") }
    var password            by remember { mutableStateOf("") }
    var confirmPassword     by remember { mutableStateOf("") }
    var name                by remember { mutableStateOf("") }
    var isRegistering       by remember { mutableStateOf(false) }
    var error               by remember { mutableStateOf<String?>(null) }

    // regex imienia: zaczyna się od wielkiej, tylko litery
    val nameRegex     = remember { "^[A-Z][a-zA-Z]*\$".toRegex() }
    // regex hasła: min. 8 znaków, co najmniej jedna duża i jedna mała litera
    val passwordRegex = remember { "^(?=.*[a-z])(?=.*[A-Z]).{8,}\$".toRegex() }

    // walidacje
    val isEmailValid      = remember(email)    { android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    val isNameValid       = remember(name)     { nameRegex.matches(name) }
    val isPasswordValid   = remember(password) { passwordRegex.matches(password) }
    val doPasswordsMatch  = remember(password, confirmPassword) { password == confirmPassword }

    Column(
        modifier            = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text  = if (isRegistering) "Zarejestruj się" else "Zaloguj się",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(24.dp))

        if (isRegistering) {
            OutlinedTextField(
                value     = name,
                onValueChange = { name = it },
                label     = { Text("Imię") },
                isError   = name.isNotEmpty() && !isNameValid,
                modifier  = Modifier.fillMaxWidth()
            )
            if (name.isNotEmpty() && !isNameValid) {
                Text(
                    text  = "Imię musi zaczynać się od wielkiej litery i zawierać tylko litery",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value          = email,
            onValueChange  = { email = it },
            label          = { Text("Email") },
            isError        = email.isNotEmpty() && !isEmailValid,
            modifier       = Modifier.fillMaxWidth()
        )
        if (email.isNotEmpty() && !isEmailValid) {
            Text(
                text  = "Podaj poprawny adres e-mail",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value              = password,
            onValueChange      = { password = it },
            label              = { Text("Hasło") },
            visualTransformation = PasswordVisualTransformation(),
            isError            = isRegistering && password.isNotEmpty() && !isPasswordValid,
            modifier           = Modifier.fillMaxWidth()
        )
        if (isRegistering && password.isNotEmpty() && !isPasswordValid) {
            Text(
                text  = "Hasło musi mieć min. 8 znaków, jedną dużą i jedną małą literę",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (isRegistering) {
            OutlinedTextField(
                value              = confirmPassword,
                onValueChange      = { confirmPassword = it },
                label              = { Text("Powtórz hasło") },
                visualTransformation = PasswordVisualTransformation(),
                isError            = confirmPassword.isNotEmpty() && !doPasswordsMatch,
                modifier           = Modifier.fillMaxWidth()
            )
            if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                Text(
                    text  = "Hasła nie są identyczne",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        } else {
            Spacer(modifier = Modifier.height(24.dp))
        }

        val canSubmit = if (isRegistering) {
            name.isNotBlank() && isNameValid &&
                    email.isNotBlank() && isEmailValid &&
                    password.isNotBlank() && isPasswordValid &&
                    confirmPassword.isNotBlank() && doPasswordsMatch
        } else {
            email.isNotBlank() && isEmailValid &&
                    password.isNotBlank()
        }

        Button(
            onClick = {
                error = null
                if (isRegistering) {
                    // REJESTRACJA
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val uid = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                            Log.d("Register", "UID: $uid")

                            // haszowanie hasła
                            val passwordHash = hashPassword(password)
                            val userData = hashMapOf(
                                "email"         to email,
                                "name"          to name,
                                "password_hash" to passwordHash
                            )

                            FirebaseFirestore
                                .getInstance("image-db")
                                .collection("users")
                                .document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    onLoginSuccess()
                                }
                                .addOnFailureListener {
                                    error = "Błąd zapisu danych: ${it.message}"
                                }
                        }
                        .addOnFailureListener {
                            error = it.message
                        }
                } else {
                    // LOGOWANIE
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { onLoginSuccess() }
                        .addOnFailureListener {
                            error = "Niepoprawny email lub hasło"
                        }
                }
            },
            enabled  = canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isRegistering) "Zarejestruj się" else "Zaloguj się")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = {
            isRegistering = !isRegistering
            error = null
        }) {
            Text(
                if (isRegistering)
                    "Masz już konto? Zaloguj się"
                else
                    "Nie masz konta? Zarejestruj się"
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        error?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}

// Funkcja pomocnicza do haszowania hasła SHA-256
fun hashPassword(password: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val bytes = md.digest(password.toByteArray(Charsets.UTF_8))
    return bytes.joinToString("") { "%02x".format(it) }
}
