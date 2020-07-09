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
        applicationId = "com.fsryan.tools.loggingtestapp"
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

    flavorDimensions("integration")

    productFlavors {
        create("datadog") {
            setDimension("integration")
            applicationIdSuffix = ".datadog"
            setMinSdkVersion(19)

            manifestPlaceholders["testapp.datadogtoken"] = project.findProperty("testapp.datadogtoken") ?: ""
        }
        create("firebase") {
            setDimension("integration")
            applicationIdSuffix = ".firebase"
        }
        create("appcenter") {
            setDimension("integration")
            applicationIdSuffix = ".appcenter"

            manifestPlaceholders["testapp.acsecret"] = project.findProperty("testapp.acsecret") ?: ""
        }
        create("appcenter3") {
            setDimension("integration")
            applicationIdSuffix = ".appcenter3"

            manifestPlaceholders["testapp.acsecret"] = project.findProperty("testapp.acsecret") ?: ""
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

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    implementation(mainDep(producer = "androidx", name = "appcompat"))
    implementation(mainDep(producer = "androidx", name = "core-ktx"))
    implementation(mainDep(producer = "androidx", name = "constraint-layout"))

    // Firebase
    firebaseImplementation(project(":logging-android-firebase"))
    firebaseImplementation(mainDep(producer = "google", name = "firebase-core"))
    firebaseImplementation(mainDep(producer = "google", name = "firebase-analytics"))
    firebaseImplementation(mainDep(producer = "google", name = "firebase-crashlytics-jdk"))
    firebaseImplementation(mainDep(producer = "google", name = "gms-tagmanager"))

    // Probably should be retired, but left for historical purposes
    // appcenter
    appcenterImplementation(project(":logging-android-appcenter"))
    appcenterImplementation(mainDep(producer = "microsoft", name = "appcenter-analytics"))
    appcenterImplementation(mainDep(producer = "microsoft", name = "appcenter-crashes"))

    // This is the most recent of the AppCenter library releases.
    // appcenter
    appcenter3Implementation(project(":logging-android-appcenter3"))
    appcenter3Implementation(mainDep(producer = "microsoft", name = "appcenter-analytics3"))
    appcenter3Implementation(mainDep(producer = "microsoft", name = "appcenter-crashes3"))

    // datadog
    datadogImplementation(project(":logging-android-datadog"))
    datadogImplementation(mainDep(producer = "datadog", name = "ddsdk"))
}

fun DependencyHandlerScope.appcenterImplementation(dependencyNotation: Any) = add(
    configurationName = "appcenterImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.appcenter3Implementation(dependencyNotation: Any) = add(
    configurationName = "appcenter3Implementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.firebaseImplementation(dependencyNotation: Any) = add(
    configurationName = "firebaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.datadogImplementation(dependencyNotation: Any) = add(
    configurationName = "datadogImplementation",
    dependencyNotation = dependencyNotation
)

// This forces us to create a google-services.json for all of the flavors.
apply(plugin = "com.google.gms.google-services")