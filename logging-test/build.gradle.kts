import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.dokka.gradle.DokkaTask
import tools.GitTools
import tools.Info

import java.text.SimpleDateFormat
import java.util.Date

plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    id("com.jfrog.bintray")
    id("maven-publish")
    id("fsryan-gradle-publishing")
    id("org.jetbrains.dokka") version "0.10.0"
}

group = "com.fsryan.tools"
version = "${ver(domain = "global", producer = "fsryan", name = "publication")}${if (project.hasProperty("postfixDate")) ".${Info.timestamp}" else ""}"

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    implementation(deps.Deps.testDep(producer = "junit5", name = "api"))

    implementation(project(":logging"))
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
    description = "Test library for capturing analytics and dev metrics logging requests"
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
    setPublications("mavenToBintray")
    publish = false

    pkg.apply {
        repo = "maven"
        name = project.name
        desc = "Library including classes for capturing analytics and dev metrics logging requests for test purposes"
        websiteUrl = "https://github.com/ryansgot/logging/${project.name}"
        issueTrackerUrl = "https://github.com/ryansgot/logging/issues"
        vcsUrl = "https://github.com/ryansgot/logging.git"
        publicDownloadNumbers = true
        setLicenses("Apache-2.0")
        setLabels("jvm", "logging", "analytics", "analytics events", "telemetry")
        version.apply {
            name = project.version.toString()
            released = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(Date())
            vcsTag = "v${project.version}"
        }
    }
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

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "javadoc"
    }
}

project.afterEvaluate {
    checkNotNull(project.tasks.findByName("release")).dependsOn(checkNotNull(project.tasks.findByName("bintrayUpload")))
}