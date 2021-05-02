import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import tools.GitTools
import tools.Info

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("maven-publish")
    id("signing")
    id("android-maven")
    id("fsryan-gradle-publishing")
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
    implementation(project(":logging-android"))

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    implementation(mainDep(producer = "newrelic", name = "android-agent"))
    implementation(mainDep(producer = "androidx", name = "annotation"))
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

    description = "Logging for Analytics events and Developer events on Dalvik or ART with New Relic destinations"
    extraPomProperties = mapOf(
        "gitrev" to GitTools.gitHash(true)
    )
    dependencyNameOverrides = mapOf(
        "logging-android-newrelicDebug" to mapOf(
            "logging-android" to "logging-android-debug"
        ),
        "logging-android-newrelicDebugToBintray" to mapOf(
            "logging-android" to "logging-android-debug"
        )
    )
}

signing {
    if (project.hasProperty("signing.keyId")) {
        if (project.hasProperty("signing.password")) {
            if (project.hasProperty("signing.secretKeyRingFile")) {
                sign(publishing.publications)
            } else {
                println("Missing signing.secretKeyRingFile: cannot sign ${project.name}")
            }
        } else {
            println("Missing signing.password: cannot sign ${project.name}")
        }
    } else {
        println("Missing signing.keyId: cannot sign ${project.name}")
    }
}