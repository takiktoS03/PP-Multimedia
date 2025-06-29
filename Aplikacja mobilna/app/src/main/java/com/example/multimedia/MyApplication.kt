package com.example.multimedia

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // Debug‐provider do developmentu:
//        FirebaseAppCheck.getInstance()
//            .installAppCheckProviderFactory(
//                DebugAppCheckProviderFactory.getInstance()
//            )
    }
}
