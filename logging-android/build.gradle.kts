import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import deps.Deps.mainDep
import deps.Deps.ver
import tools.GitTools
import tools.Info

import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("android-maven")
    id("fsryan-gradle-publishing")
    id("com.jfrog.bintray")
}

group = "com.fsryan.tools"
version = "${ver(domain = "global", producer = "fsryan", name = "publication")}${if (project.hasProperty("postfixDate")) ".${Info.timestamp}" else ""}"

android {

    compileSdkVersion(ver("global", "android", "compileSdk").toInt())

    defaultConfig {
        minSdkVersion(ver("global", "android", "minSdk").toInt())
        targetSdkVersion(ver("global", "android", "targetSdk").toInt())
        versionCode = 1
        versionName = "1.0"
        consumerProguardFile("consumer-proguard-rules.pro")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        // Works around an issue in the kotlin-android plugin where the
        // type became Any
        (this as KotlinJvmOptions).jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(project(":logging"))

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))
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
    description = "Logging for Analytics events and Developer events on Dalvik or ART"
    awsAccessKeyId = if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!
    awsSecretKey = if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!
    extraPomProperties = mapOf(
        "gitrev" to GitTools.gitHash(true)
    )
    additionalPublications.add("bintray")
}

bintray {
    user = if (project.hasProperty("bintrayUser")) project.property("bintrayUser").toString() else ""
    key = if (project.hasProperty("bintrayApiKey")) project.property("bintrayApiKey").toString() else ""
    setPublications("${project.name}ReleaseToBintray", "${project.name}DebugToBintray")
    publish = false

    pkg.apply {
        repo = "maven"
        name = project.name
        desc = "Android library building upon the base logging library. Handles android-specific concerns like Context. debug variant contains some helpful debug-specific loggers."
        websiteUrl = "https://github.com/ryansgot/logging/${project.name}"
        issueTrackerUrl = "https://github.com/ryansgot/logging/issues"
        vcsUrl = "https://github.com/ryansgot/logging.git"
        publicDownloadNumbers = true
        setLicenses("Apache-2.0")
        setLabels("jvm", "logging", "android", "analytics", "analytics events", "telemetry")
        version.apply {
            name = project.version.toString()
            released = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(Date())
            vcsTag = "v${project.version}"
        }
    }
}

project.afterEvaluate {
    checkNotNull(project.tasks.findByName("release")).dependsOn(checkNotNull(project.tasks.findByName("bintrayUpload")))
}