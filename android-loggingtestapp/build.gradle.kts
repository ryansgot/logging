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
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    signingConfigs {
        create("defaultSigning") {
            storeFile = file("debug.keystore")
            keyPassword = "android"
            storePassword = "android"
            keyAlias = "androiddebugkey"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("defaultSigning")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = project.hasProperty("minifyEnabled")
            signingConfig = signingConfigs.getByName("defaultSigning")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    flavorDimensions("integration")

    productFlavors {
        create("firebase") {
            setDimension("integration")
            applicationIdSuffix = ".firebase"
        }
        create("appcenter3") {
            setDimension("integration")
            applicationIdSuffix = ".appcenter3"
            sequenceOf(
                "testapp.acsecret" to project.findProperty("testapp.acsecret")?.toString().orEmpty(),
                "fsac_analytics_enabled" to "true",
                "fsac_crashes_enabled" to "true"
            ).forEach { (k, v) -> manifestPlaceholders[k] = v }
        }
        create("appcenter4") {
            setDimension("integration")
            applicationIdSuffix = ".appcenter3" // <-- done to reduce number of test packages
            sequenceOf(
                "testapp.acsecret" to project.findProperty("testapp.acsecret")?.toString().orEmpty(),
                "fsac_analytics_enabled" to "true",
                "fsac_crashes_enabled" to "true"
            ).forEach { (k, v) -> manifestPlaceholders[k] = v }
        }
        create("newrelic") {
            setDimension("integration")
            applicationIdSuffix = ".newrelic"
            manifestPlaceholders["testapp.newrelictoken"] = project.findProperty("testapp.newrelictoken") ?: ""
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

    configurations {
        create("appcenter3DebugImplementation")
        create("appcenter3ReleaseImplementation")
        create("appcenter4DebugImplementation")
        create("appcenter4ReleaseImplementation")
        create("firebaseDebugImplementation")
        create("firebaseReleaseImplementation")
        create("newrelicDebugImplementation")
        create("newrelicReleaseImplementation")
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

    with(Deps.Main.AndroidX) {
        implementation(appCompat)
        implementation(coreKtx)
        implementation(constraintLayout)
    }

    implementation(Deps.Main.JetBrains.kotlinSTDLib)

    // Firebase
    firebaseReleaseImplementation(project(":logging-android-firebase", configuration = "releaseRuntimeElements"))
    firebaseDebugImplementation(project(":logging-android-firebase", configuration = "debugRuntimeElements"))
    with(Deps.Main.Google) {
        firebaseImplementation(analytics)
        firebaseImplementation(crashlytics)
    }

    // appcenter3
    appcenter3ReleaseImplementation(project(":logging-android-appcenter3", configuration = "releaseRuntimeElements"))
    appcenter3DebugImplementation(project(":logging-android-appcenter3", configuration = "debugRuntimeElements"))
    with(Deps.Main.Microsoft) {
        appcenter3Implementation(analytics3)
        appcenter3Implementation(crashes3)
    }

    // appcenter
    appcenter4ReleaseImplementation(project(":logging-android-appcenter4", configuration = "releaseRuntimeElements"))
    appcenter4DebugImplementation(project(":logging-android-appcenter4", configuration = "debugRuntimeElements"))
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

fun DependencyHandlerScope.appcenter3DebugImplementation(dependencyNotation: Any) = add(
    configurationName = "appcenter3DebugImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.appcenter3ReleaseImplementation(dependencyNotation: Any) = add(
    configurationName = "appcenter3ReleaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.appcenter4DebugImplementation(dependencyNotation: Any) = add(
    configurationName = "appcenter4DebugImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.appcenter4ReleaseImplementation(dependencyNotation: Any) = add(
    configurationName = "appcenter4ReleaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.firebaseDebugImplementation(dependencyNotation: Any) = add(
    configurationName = "firebaseDebugImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.firebaseReleaseImplementation(dependencyNotation: Any) = add(
    configurationName = "firebaseReleaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.newrelicReleaseImplementation(dependencyNotation: Any) = add(
    configurationName = "newrelicReleaseImplementation",
    dependencyNotation = dependencyNotation
)

fun DependencyHandlerScope.newrelicDebugImplementation(dependencyNotation: Any) = add(
    configurationName = "newrelicDebugImplementation",
    dependencyNotation = dependencyNotation
)

// This forces us to create a google-services.json for all of the flavors.
apply(plugin = "com.google.gms.google-services")

apply(plugin = "com.google.firebase.crashlytics")