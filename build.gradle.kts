buildscript {
    repositories {
        maven { url = uri("https://plugins.gradle.org/m2/") }
        jcenter()
        mavenCentral()
        google()
        maven { url = uri("https://maven.fabric.io/public") }
        maven {
            url = uri("s3://repo.fsryan.com/release")
            credentials(AwsCredentials::class) {
                setAccessKey(if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!)
                setSecretKey(if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!)
            }
        }
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:4.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("com.google.gms:google-services:4.3.4")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.3.0")
        classpath("com.fsryan.gradle:fsryan-gradle-publishing:0.0.6")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4")
        classpath("com.newrelic.agent.android:agent-gradle-plugin:5.28.0")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        maven { url = uri("https://dl.bintray.com/datadog/datadog-maven") }
        maven {
            url = uri("s3://repo.fsryan.com/release")
            credentials(AwsCredentials::class) {
                setAccessKey(if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!)
                setSecretKey(if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!)
            }
        }
    }
}