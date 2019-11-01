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
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.50")
        classpath("com.google.gms:google-services:4.3.2")
        classpath("io.fabric.tools:gradle:1.31.0")
        classpath("com.fsryan.gradle:fsryan-gradle-publishing:0.0.4")
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
    }
}

allprojects {
    repositories {
        jcenter()
        google()
        maven {
            url = uri("s3://repo.fsryan.com/release")
            credentials(AwsCredentials::class) {
                setAccessKey(if (project.hasProperty("awsMavenAccessKey")) project.property("awsMavenAccessKey").toString() else System.getenv()["AWS_ACCES_KEY_ID"]!!)
                setSecretKey(if (project.hasProperty("awsMavenSecretKey")) project.property("awsMavenSecretKey").toString() else System.getenv()["AWS_SECRET_KEY"]!!)
            }
        }
    }
}