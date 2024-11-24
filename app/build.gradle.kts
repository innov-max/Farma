plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    kotlin("plugin.serialization") version "1.9.0"
    alias(libs.plugins.google.gms.google.services)
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
            excludes += "META-INF/androidx.localbroadcastmanager_localbroadcastmanager.version"
            excludes += "META-INF/androidx.appcompat_appcompat.version"
            excludes += "build-data.properties"


        }
    }

}

dependencies {
    // AndroidX libraries
    implementation(libs.androidx.core.ktx)  // Ensure the latest compatible version
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.runtime.livedata)

    // Firebase and Google Play services
    implementation(libs.play.services.location)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.maps)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // Debug tools
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Material Design and Lottie
    implementation(libs.lottie.compose)
    implementation(libs.material3)
    implementation(libs.androidx.material)

    // Ktor Client libraries (removed duplicates and version conflicts)
    implementation(libs.ktor.client.core.v233)
    implementation(libs.ktor.client.cio.v233)
    implementation (libs.ktor.client.android)
    implementation(libs.ktor.client.serialization.v233)
    implementation(libs.ktor.client.json)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)


    // Coil for image loading
    implementation("io.coil-kt:coil-compose:2.0.0")

    // Coroutines for asynchronous tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")

    // Retrofit and OkHttp
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3") {
        exclude(group = "androidx.customview", module = "customview")
    }

    // Security
    implementation("androidx.security:security-crypto:1.1.0-alpha03")  // Keep the latest version

    // Room Database
    implementation(libs.androidx.room.runtime)
    implementation("androidx.room:room-ktx:2.5.0")
    annotationProcessor("androidx.room:room-compiler:2.4.0")

    // OSMDroid for maps
    implementation("org.osmdroid:osmdroid-android:6.1.12")

    // MPAndroidChart for charting
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    implementation("com.android.volley:volley:1.2.1") {
        exclude(group = "androidx.customview", module = "customview")
    }


}
configurations.all {
    resolutionStrategy {
        force("androidx.core:core:1.13.1")
        force("androidx.versionedparcelable:versionedparcelable:1.1.1")
    }
}




