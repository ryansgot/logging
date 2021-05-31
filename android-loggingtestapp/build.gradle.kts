import deps.Deps
import deps.Deps.Versions

plugins {
    id("com.android.application")
    id("kotlin-android")
    // UNCOMMENT FOR newrelic
//    id("newrelic")
}

android {

    Versions.Global.Android.let { version ->
        compileSdkVersion(version.compileSdk)

        defaultConfig {
            minSdkVersion(version.minSdk)
            targetSdkVersion(version.targetSdk)
            versionCode = 1
            versionName = "1.0"
            consumerProguardFile("consumer-proguard-rules.pro")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
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
        create("firebase") {
            dimension("integration")
            applicationIdSuffix = ".firebase"
        }
        create("appcenter3") {
            dimension("integration")
            applicationIdSuffix = ".appcenter3"

            manifestPlaceholders(mapOf(
                "testapp.acsecret" to (project.findProperty("testapp.acsecret") ?: ""),
                "fsac_analytics_enabled" to "true",
                "fsac_crashes_enabled" to "true"
            ))
        }
        create("appcenter4") {
            dimension("integration")
            applicationIdSuffix = ".appcenter4"

            manifestPlaceholders(mapOf(
                "testapp.acsecret" to (project.findProperty("testapp.acsecret") ?: ""),
                "fsac_analytics_enabled" to "true",
                "fsac_crashes_enabled" to "true"
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
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
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

    with(Deps.Main.AndroidX) {
        implementation(appCompat)
        implementation(coreKtx)
        implementation(constraintLayout)
    }

    implementation(Deps.Main.JetBrains.kotlinSTDLib)

    // Firebase
    firebaseImplementation(project(":logging-android-firebase"))
    with(Deps.Main.Google) {
        firebaseImplementation(analytics)
        firebaseImplementation(crashlytics)
    }

    // appcenter
    appcenter3Implementation(project(":logging-android-appcenter3"))
    with(Deps.Main.Microsoft) {
        appcenter3Implementation(analytics3)
        appcenter3Implementation(crashes3)
    }

    // appcenter
    appcenter4Implementation(project(":logging-android-appcenter4"))
    with(Deps.Main.Microsoft) {
        appcenter4Implementation(analytics4)
        appcenter4Implementation(crashes4)
    }

    // newrelic
    newrelicImplementation(project(":logging-android-newrelic"))
    newrelicImplementation(Deps.Main.NewRelic.agent)
}

fun DependencyHandlerScope.appcenter3Implementation(dependencyNotation: Any) = add(
    configurationName = "appcenter3Implementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.appcenter4Implementation(dependencyNotation: Any) = add(
    configurationName = "appcenter4Implementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.firebaseImplementation(dependencyNotation: Any) = add(
    configurationName = "firebaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.newrelicImplementation(dependencyNotation: Any) = add(
    configurationName = "newrelicImplementation",
    dependencyNotation = dependencyNotation
)

// This forces us to create a google-services.json for all of the flavors.
apply(plugin = "com.google.gms.google-services")

apply(plugin = "com.google.firebase.crashlytics")