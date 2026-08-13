plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp") version "2.3.10"

    kotlin("plugin.serialization") version "2.4.0" //For Kotlin serialization (for navigation3)
}

android {
    namespace = "com.mjapa21.smartwallet"
    compileSdk {
        version = release(37) { //changed this from 36 to 37 to fix BUILD error
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.mjapa21.smartwallet"
        minSdk = 24
        targetSdk = 36
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)


    //Room
    val room_version = "2.8.4"

    implementation("androidx.room:room-runtime:${room_version}")
    ksp("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:${room_version}")


    //Koin for dependecy injection
    implementation(platform("io.insert-koin:koin-bom:4.2.0"))
    implementation("io.insert-koin:koin-android")
    implementation("io.insert-koin:koin-compose")
    implementation("io.insert-koin:koin-compose-viewmodel")


    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    //Navigation3
    implementation("androidx.navigation3:navigation3-runtime:1.1.3")
    implementation("androidx.navigation3:navigation3-ui:1.1.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-navigation3:2.11.0")


}