package com.example.multimedia.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.multimedia.data.repository.VerificationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

data class LoginState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val name: String = "",
    val isRegistering: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val nameError: String? = null,
    val error: String? = null,
    val canSubmit: Boolean = false
)

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun onEmailChange(email: String) = update { it.copy(email = email) }
    fun onPasswordChange(pw: String) = update { it.copy(password = pw) }
    fun onConfirmPasswordChange(pw: String) = update { it.copy(confirmPassword = pw) }
    fun onNameChange(name: String) = update { it.copy(name = name) }
    fun toggleMode() = update { it.copy(isRegistering = !it.isRegistering, error = null) }
    fun setError(msg: String) = update { it.copy(error = msg) }

    private fun update(transform: (LoginState) -> LoginState) {
        _state.value = transform(_state.value).let { validate(it) }
    }

    private fun validate(state: LoginState): LoginState {
        val emailOk = android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()
        val passwordOk = state.password.length >= 8 &&
                state.password.any { it.isUpperCase() } &&
                state.password.any { it.isLowerCase() }
        val nameOk = state.name.matches(Regex("^[A-Z][a-zA-Z]*$"))
        val confirmOk = state.password == state.confirmPassword

        val canSubmit = if (state.isRegistering) emailOk && passwordOk && nameOk && confirmOk
        else emailOk && passwordOk

        return state.copy(
            emailError = if (!emailOk && state.email.isNotBlank()) "Nieprawidłowy email" else null,
            passwordError = if (!passwordOk && state.password.isNotBlank()) "Hasło musi mieć min. 8 znaków, dużą i małą literę" else null,
            nameError = if (state.isRegistering && !nameOk && state.name.isNotBlank()) "Niepoprawne imię" else null,
            confirmPasswordError = if (state.isRegistering && !confirmOk && state.confirmPassword.isNotBlank()) "Hasła nie są identyczne" else null,
            canSubmit = canSubmit
        )
    }

    suspend fun register(
        navController: NavController,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val s = state.value

        if (!s.canSubmit) {
            onFailure("Upewnij się, że wszystkie pola są poprawnie wypełnione")
            return
        }

        try {
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(s.email, s.password)
                .await()

            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return onFailure("Brak UID")

            val userData = mapOf(
                "email" to s.email,
                "name" to s.name,
                "password_hash" to hashPassword(s.password),
                "created_at" to FieldValue.serverTimestamp(),
                "isVerified" to false
            )

            FirebaseFirestore.getInstance("image-db")
                .collection("users")
                .document(uid)
                .set(userData)
                .await()

            Log.d("Register", "Utworzono użytkownika w Firestore: $uid")

            val repo = VerificationRepository()
            var codeSent = false
            val latch = kotlinx.coroutines.CompletableDeferred<Unit>()

            Log.d("Register", "Wywołanie sendVerificationCode()")

            Log.d("Register", "🔸 Repo utworzone, startujemy wysyłkę kodu")

            try {
                repo.sendVerificationCode(uid, s.email) { success ->
                    Log.d("Register", "✅ Callback z repo: success = $success")
                    codeSent = success
                    latch.complete(Unit)
                }
            } catch (e: Exception) {
                Log.e("Register", "❌ Exception: ${e.message}")
                onFailure("Błąd przy wysyłaniu kodu: ${e.message}")
                return
            }

            Log.d("Register", "⏳ Oczekiwanie na latch")
            latch.await()


            if (codeSent) {
                Log.d("Register", "Kod wysłany – success")
                onSuccess()
            } else {
                Log.e("Register", "Nie udało się wysłać kodu")
                onFailure("Nie udało się wysłać kodu weryfikacyjnego")
            }

        } catch (e: Exception) {
            val errorMessage = if (e.message?.contains("email address is already in use") == true) {
                "Podany adres e-mail jest już zarejestrowany"
            } else {
                e.message ?: "Błąd rejestracji"
            }
            Log.e("Register", "Wyjątek: $errorMessage")
            onFailure(errorMessage)
        }
    }

    suspend fun login(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(state.value.email, state.value.password)
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure("Niepoprawny email lub hasło")
        }
    }

    suspend fun loginAsGuest(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        try {
            FirebaseAuth.getInstance()
                .signInAnonymously()
                .await()
            onSuccess()
        } catch (e: Exception) {
            onFailure("Błąd logowania jako gość")
        }
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    fun resetForm() {
        _state.value = LoginState()
    }
}
