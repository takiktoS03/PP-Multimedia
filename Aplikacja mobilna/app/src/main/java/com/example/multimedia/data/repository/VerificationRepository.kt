package com.example.multimedia.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException

class VerificationRepository {
    private val TAG = "VerificationCode"

    private val client = OkHttpClient()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    // WŁAŚCIWY adres do Twojej funkcji wysyłającej email
    private val functionUrl = "https://us-central1-image-management-cbaee.cloudfunctions.net/sendCode"

    fun sendVerificationCode(email: String, onComplete: (Boolean) -> Unit) {
        Log.d(TAG, "START: sendVerificationCode()")

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.e(TAG, "❌ Brak zalogowanego użytkownika – nie mogę wysłać kodu")
            onComplete(false)
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        Log.d(TAG, "Używam Firestore INSTANCE: ${firestore.app.name}")
        Log.d(TAG, "Używany UID: $userId")

        val code = (100000..999999).random().toString()
        Log.d(TAG, "Wygenerowano kod: $code dla: $email")

        firestore.collection("verifications").document(userId)
            .set(mapOf("code" to code, "email" to email))
            .addOnSuccessListener {
                Log.d(TAG, "Kod zapisany w Firestore")

                val json = JSONObject()
                json.put("email", email)
                json.put("code", code)

                val body = json.toString().toRequestBody(JSON)

                val request = Request.Builder()
                    .url(functionUrl)
                    .post(body)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        Log.e(TAG, "❌ Błąd HTTP: ${e.message}")
                        onComplete(false)
                    }

                    override fun onResponse(call: Call, response: Response) {
                        if (response.isSuccessful) {
                            Log.d(TAG, "✅ Kod wysłany poprawnie na email")
                            onComplete(true)
                        } else {
                            Log.e(TAG, "❌ Odpowiedź serwera: ${response.code}")
                            onComplete(false)
                        }
                    }
                })
            }
            .addOnFailureListener {
                Log.e(TAG, "❌ Błąd zapisu do Firestore", it)
                onComplete(false)
            }
    }
}
