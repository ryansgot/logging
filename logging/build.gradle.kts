import deps.Deps.mainDep
import deps.Deps.testDep
import tools.GitTools
import java.util.Date

plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("fsryan-gradle-publishing")
}

group = "com.fsryan.tools"
version = "0.0.1${if (project.hasProperty("postfixDate")) ".${Date().time}" else ""}"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    testImplementation(testDep(producer = "mockk", name = "core"))
    testImplementation(testDep(producer = "junit5", name = "api"))
    testImplementation(testDep(producer = "junit5", name = "params"))
    testRuntimeOnly(testDep(producer = "junit5", name = "engine"))
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
    description = "Logging for Analytics events and Developer events on Android"
    awsAccessKeyId = if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!
    awsSecretKey = if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!
    extraPomProperties = mapOf(
        "gitrev" to GitTools.gitHash(true)
    )
}

tasks.test {
    useJUnitPlatform {}
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.6"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}