import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    // UNCOMMENT FOR newrelic
//    id("newrelic")
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
            dimension("integration")
            applicationIdSuffix = ".datadog"
            setMinSdkVersion(19)

            manifestPlaceholders(mapOf(
                "testapp.datadogtoken" to (project.findProperty("testapp.datadogtoken") ?: "")
            ))
        }
        create("firebase") {
            dimension("integration")
            applicationIdSuffix = ".firebase"
        }
        create("appcenter3") {
            dimension("integration")
            applicationIdSuffix = ".appcenter3"

            manifestPlaceholders(mapOf(
                "testapp.acsecret" to (project.findProperty("testapp.acsecret") ?: "")
            ))
        }
        create("newrelic") {
            dimension("integration")
            applicationIdSuffix = ".newrelic"

            manifestPlaceholders(mapOf(
                "testapp.newrelictoken" to (project.findProperty("testapp.newrelictoken") ?: "")
            ))
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

// UNCOMMENT for newrelic
//newrelic {
//    // disable map uploads
//    uploadMapsForVariant("")
//}


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
    firebaseImplementation(mainDep(producer = "google", name = "firebase-analytics-ktx"))
    firebaseImplementation(mainDep(producer = "google", name = "firebase-crashlytics-ktx"))

    // This is the most recent of the AppCenter library releases.
    // appcenter
    appcenter3Implementation(project(":logging-android-appcenter3"))
    appcenter3Implementation(mainDep(producer = "microsoft", name = "appcenter-analytics3"))
    appcenter3Implementation(mainDep(producer = "microsoft", name = "appcenter-crashes3"))

    // datadog
    datadogImplementation(project(":logging-android-datadog"))
    datadogImplementation(mainDep(producer = "datadog", name = "ddsdk"))

    // newrelic
    newrelicImplementation(project(":logging-android-newrelic"))
    newrelicImplementation(mainDep(producer = "newrelic", name = "android-agent"))
}

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

fun DependencyHandlerScope.newrelicImplementation(dependencyNotation: Any) = add(
    configurationName = "newrelicImplementation",
    dependencyNotation = dependencyNotation
)

// This forces us to create a google-services.json for all of the flavors.
apply(plugin = "com.google.gms.google-services")

apply(plugin = "com.google.firebase.crashlytics")