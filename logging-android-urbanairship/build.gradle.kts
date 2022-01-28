import deps.Deps
import deps.Deps.Versions
import tools.GitTools
import tools.Info

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("fsryan-gradle-publishing")
    id("org.jetbrains.dokka")
}

group = "com.fsryan.tools"
version = "${Versions.Global.FSRyan.publication}${if (project.hasProperty("postfixDate")) ".${Info.timestamp}" else ""}"

android {

    Versions.Global.Android.let { version ->
        compileSdkVersion(version.compileSdk)

        defaultConfig {
            minSdkVersion(version.minSdk)
            targetSdkVersion(version.targetSdk)
            consumerProguardFile("consumer-proguard-rules.pro")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }

        getByName("debug") {
            isMinifyEnabled = false
            consumerProguardFiles("proguard-rules.pro")
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    api(project(":logging"))

    implementation(Deps.Main.AndroidX.annotation)
    implementation(Deps.Main.JetBrains.kotlinSTDLib)
    api(Deps.Main.Airship.core)
}

fsPublishingConfig {
    developerName = "Ryan Scott"
    developerId = "fsryan"
    developerEmail = "fsryan.developer@gmail.com"
    siteUrl = "https://github.com/ryansgot/logging"
    baseArtifactId = project.name
    groupId = project.group.toString()
    versionName = project.version.toString()

    licenseName = "Apache License, Version 2.0"
    licenseUrl = "https://www.apache.org/licenses/LICENSE-2.0.txt"
    licenseDistribution = "repo"

//    releaseRepoUrl = "s3://repo.fsryan.com/release"
//    snapshotRepoUrl = "s3://repo.fsryan.com/snapshot"
//    awsAccessKeyId = if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!
//    awsSecretKey = if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!

    releaseRepoUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
    releaseBasicUser = project.findProperty("com.fsryan.ossrh.release.username")?.toString().orEmpty()
    releaseBasicPassword = project.findProperty("com.fsryan.ossrh.release.password")?.toString().orEmpty()
    snapshotRepoUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/"
    snapshotBasicUser = project.findProperty("com.fsryan.ossrh.snapshot.username")?.toString().orEmpty()
    snapshotBasicPassword = project.findProperty("com.fsryan.ossrh.snapshot.password")?.toString().orEmpty()
    useBasicCredentials = true
    description = "Logging for Analytics events and Developer events on Dalvik or ART with Urban Airship destinations"
    extraPomProperties = mapOf(
        "gitrev" to GitTools.gitHash(true)
    )
    dependencyNameOverrides = mapOf(
        "logging-android-urbanairshipDebug" to mapOf(
            "logging" to "logging-android-debug"
        ),
        "logging-android-urbanairshipRelease" to mapOf(
            "logging" to "logging-android"
        )
    )
}