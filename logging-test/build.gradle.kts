import deps.Deps.mainDep
import deps.Deps.ver
import org.jetbrains.dokka.gradle.DokkaTask
import tools.GitTools
import tools.Info

plugins {
    java
    id("org.jetbrains.kotlin.jvm")
    id("maven-publish")
    id("signing")
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
    description = "Test library for capturing analytics and dev metrics logging requests"
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

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "javadoc"
    }
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