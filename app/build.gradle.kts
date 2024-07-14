plugins {
    // android
    id("com.android.application")
    // jetbrains
    id("org.jetbrains.kotlin.android")
}

android {

    namespace  = "ir.farsroidx.andromeda"
    compileSdk = 34

    defaultConfig {
        applicationId             = "ir.farsroidx.andromeda.viewmodel"
        minSdk                    = 21
        targetSdk                 = 34
        versionCode               = 1
        versionName               = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")

    implementation(project(":m31"))
}