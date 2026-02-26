plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.22"
    id("com.google.devtools.ksp") version "1.9.22-1.0.17"
}

android {
    namespace = "com.example.marsphotos"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.marsphotos"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Bloque KSP movido correctamente aquí adentro para los argumentos
        //noinspection WrongGradleMethod
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
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
        // ACTUALIZADO: Ambos deben ser VERSION_17 para evitar el error de "Inconsistent JVM-target"
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        // ACTUALIZADO: Debe coincidir con las compileOptions
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Configuración adicional para asegurar que KSP use la versión correcta de Java
// (Añadir esto si el error de JVM-target persiste)
kotlin {
    jvmToolchain(17)
}

dependencies {
    // GSON
    implementation("com.google.code.gson:gson:2.10.1")

    // --- JETPACK COMPOSE BOM ---
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    // Ciclo de vida y ViewModel
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // Retrofit y Serialización
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")

    // Coil y WorkManager
    implementation("io.coil-kt:coil-compose:2.5.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Navegación
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // --- BASE DE DATOS ROOM ---
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    // Corregido: KSP ahora funcionará porque las versiones de Java están alineadas
    ksp("androidx.room:room-compiler:$room_version")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.02.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    debugImplementation("androidx.compose.ui:ui-tooling")
}