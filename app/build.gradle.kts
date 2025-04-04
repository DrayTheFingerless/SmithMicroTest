plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "1.9.22"
    id("kotlin-kapt") // If you're using kapt
    alias(libs.plugins.hilt) // Apply the Hilt plugin
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ferreirarobert.smithmicrotest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ferreirarobert.smithmicrotest"
        minSdk = 33
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
        compose = true
    }

    kapt {
        correctErrorTypes = true
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    //Compose DataStore
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.firebase.firestore.ktx)
    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.firestore)

    implementation("org.osmdroid:osmdroid-android:6.1.17")

    //Hilt
    implementation(libs.hilt.android)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.work.runtime.ktx)
    kapt(libs.hilt.android.compiler)
    // If you're using Hilt with Jetpack Compose Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    testImplementation(libs.junit) // Add this line
    androidTestImplementation(libs.androidx.junit.v115)
    androidTestImplementation(libs.androidx.runner)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}