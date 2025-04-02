plugins {
    // android
    id("com.android.library")
    // jetbrains
    id("org.jetbrains.kotlin.android")
    // maven
    id("maven-publish")
}

android {

    namespace  = "ir.farsroidx.m31"
    compileSdk = 35

    defaultConfig {
        multiDexEnabled           = true
        minSdk                    = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {

        release {

            isMinifyEnabled = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "r8-rules.pro"
            )
        }
    }

    buildFeatures {
        buildConfig = false
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

    implementation("androidx.core:core-ktx:1.15.0")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    // Andromeda
    implementation("com.github.farsroidx:andromeda-extensions:2.1.0")

    // Jetbrains
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.0")

}

publishing {

    publications {

        register<MavenPublication>("release") {

            groupId    = "ir.farsroidx.m31"
            artifactId = "andromeda-viewmodel"
            version    = "2.0.0"

            afterEvaluate {

                from(
                    components["release"]
                )
            }
        }
    }
}