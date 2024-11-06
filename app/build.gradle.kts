plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0"
}



android {
    namespace = "com.example.mkulifarm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mkulifarm"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
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
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.lottie.compose)
    implementation (libs.material3)
    implementation (libs.androidx.material)
    implementation ("io.ktor:ktor-client-core:2.1.1")
    implementation ("io.ktor:ktor-client-cio:2.1.1")
    implementation ("io.ktor:ktor-client-serialization:2.1.1")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation ("io.coil-kt:coil-compose:2.0.0")
    implementation ("io.ktor:ktor-client-logging:2.3.3") // Ktor Client Logging
    implementation ("io.ktor:ktor-client-cio:2.3.3") // Ktor Client CIO engine
    implementation (libs.kotlinx.serialization.json)
    implementation ("io.ktor:ktor-client-json:2.3.3")
    implementation ("io.ktor:ktor-client-core:2.3.3") // Ktor Client Core
    implementation ("io.ktor:ktor-client-json:2.3.3") // Ktor Client JSON
    implementation ("io.ktor:ktor-client-serialization:2.3.3") // Ktor Client Serialization
    implementation ("io.ktor:ktor-client-logging:2.3.3") // Ktor Client Logging
    implementation ("io.ktor:ktor-client-cio:2.3.3") // Ktor Client CIO engine
    implementation (libs.kotlinx.serialization.json.v160) // Kotlinx Serialization
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.squareup.okhttp3:okhttp:4.9.3")


}



