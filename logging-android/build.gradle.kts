import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import tools.GitTools
import tools.Info

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("com.github.dcendents.android-maven")
    id("fsryan-gradle-publishing")
}

group = "com.fsryan.tools"
version = "0.0.1${if (project.hasProperty("postfixDate")) ".${Info.timestamp}" else ""}"

android {

    compileSdkVersion(ver("global", "android", "compileSdk").toInt())

    defaultConfig {
        minSdkVersion(ver("global", "android", "minSdk").toInt())
        targetSdkVersion(ver("global", "android", "targetSdk").toInt())
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("boolean", "CRASHLYTICS_ENABLED_BY_DEFAULT", "false")
        }

        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")

            buildConfigField("boolean", "CRASHLYTICS_ENABLED_BY_DEFAULT", "true")
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

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    implementation(mainDep(producer = "google", name = "firebase-core"))
    implementation(mainDep(producer = "google", name = "firebase-analytics"))
    implementation(mainDep(producer = "google", name = "firebase-crashlytics-jdk"))
}

fsPublishingConfig {
    developerName = "Ryan Scott"
    developerId = "fsryan"
    developerEmail = "fsryan.developer@gmail.com"
    siteUrl = "https://github.com/ryansgot/logging"
    baseArtifactId = project.name
    groupId = project.group.toString()
    versionName = project.version.toString()
    releaseRepoUrl = "s3://repo.fsryan.com/release"
    snapshotRepoUrl = "s3://repo.fsryan.com/snapshot"
    description = "Logging for Analytics events and Developer events on the JVM"
    awsAccessKeyId = if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!
    awsSecretKey = if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!
    extraPomProperties = mapOf(
        "gitrev" to GitTools.gitHash(true)
    )
}