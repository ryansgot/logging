import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("io.fabric")
}

android {

    compileSdkVersion(ver("global", "android", "compileSdk").toInt())

    defaultConfig {
        applicationId = "com.fsryan.tools.loggingtestapp.firebase"
        minSdkVersion(ver("global", "android", "minSdk").toInt())
        targetSdkVersion(ver("global", "android", "targetSdk").toInt())
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = project.hasProperty("minifyEnabled")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        // Works around an issue in the kotlin-android plugin where the
        // type became Any
        (this as KotlinJvmOptions).jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(project(":logging"))
    implementation(project(":logging-android"))
    implementation(project(":logging-android-firebase"))

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    implementation(mainDep(producer = "androidx", name = "appcompat"))
    implementation(mainDep(producer = "androidx", name = "core-ktx"))
    implementation(mainDep(producer = "androidx", name = "constraint-layout"))

    implementation(mainDep(producer = "google", name = "firebase-core"))
    implementation(mainDep(producer = "google", name = "firebase-analytics"))
    implementation(mainDep(producer = "google", name = "firebase-crashlytics-jdk"))
    implementation(mainDep(producer = "google", name = "gms-tagmanager"))
}

apply(plugin = "com.google.gms.google-services")