plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.hilt.android)
    kotlin("kapt")
    alias(libs.plugins.compose.compiler)
    id("com.google.gms.google-services")
}

    android {
    namespace = "com.example.multimedia"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.multimedia"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))

    // Firebase
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.auth)
    implementation(libs.google.firebase.firestore)

    // AndroidX / Jetpack Compose
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.material3.v130)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.coil.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.exifinterface)
    implementation(libs.firebase.functions.ktx)
    implementation(libs.google.firebase.functions.ktx)

    implementation(libs.firebase.functions.ktx.v2040)
    implementation(libs.google.firebase.auth.ktx)
    implementation(libs.com.google.firebase.firebase.firestore.ktx)
    implementation(libs.okhttp)

    implementation(libs.androidx.material3)
    implementation (libs.accompanist.permissions)

    implementation (libs.play.services.location)
    // Multimedia / media3
    implementation(libs.androidx.media3.common.ktx)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.google.firebase.storage.ktx)
    implementation(libs.firebase.appcheck.debug)
    kapt(libs.hilt.compiler)

    // Image
    implementation(libs.glide)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Debug only
    debugImplementation(libs.androidx.ui.tooling)
}