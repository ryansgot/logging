import deps.Deps
import deps.Deps.Versions
import tools.Info

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    jacoco
    `maven-publish`
    id("org.jetbrains.dokka")
}

version = "${Versions.Global.FSRyan.publication}${if (project.hasProperty("postfixDate")) ".${Info.timestamp}" else ""}"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val canBuildMacOSX64 = System.getProperty("os.name") == "Mac OS X"

kotlin {
    android {
        publishLibraryVariants("release")
    }
    if (canBuildMacOSX64) {
        iosArm32()
        iosArm64()
        iosSimulatorArm64()
        iosX64()
    }
    jvm("jvm") {
        attributes {
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 8)
        }
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    js {
        browser()
    }
    linuxX64()
    if (canBuildMacOSX64) {
        macosX64()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Deps.Test.JetBrains) {
                    api(project(":logging"))
                    api(test)
                    api(testAnnotations)
                }
            }
        }
        val sharedAndroidJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                with(Deps.Test.JUnit5) {
                    api(jupiterApi)
                }
            }
        }
        val androidMain by getting {
            dependsOn(sharedAndroidJvmMain)
            dependencies {
            }
        }
        val jvmMain by getting {
            dependsOn(sharedAndroidJvmMain)
            dependencies {

            }
        }
        val nonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
            }
        }
        val jsMain by getting {
            dependsOn(nonJvmMain)
            dependencies {
            }
        }
        val nativeMain by creating {
            dependsOn(nonJvmMain)
            dependencies {

            }
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
            dependencies {
            }
        }
        if (canBuildMacOSX64) {
            val macosX64Main by getting {
                dependsOn(nativeMain)
                dependencies {
                }
            }
            val iosMain by creating {
                dependsOn(nonJvmMain)
            }
            val iosArm32Main by getting {
                dependsOn(iosMain)
            }
            val iosArm64Main by getting {
                dependsOn(iosMain)
            }
            val iosX64Main by getting {
                dependsOn(iosMain)
            }
            val iosSimulatorArm64Main by getting {
                dependsOn(iosMain)
            }
        }
    }
}

android {
    compileSdk = Versions.Global.Android.compileSdk

    defaultConfig {
        minSdk = Versions.Global.Android.minSdk
        targetSdk = Versions.Global.Android.targetSdk

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isTestCoverageEnabled = true

            consumerProguardFiles("consumer-proguard-rules.pro")
        }

        getByName("release") {
            isMinifyEnabled = false
            consumerProguardFiles("consumer-proguard-rules.pro")
        }
    }

    compileOptions {
        targetCompatibility = JavaVersion.VERSION_1_8
        sourceCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res")
        }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

//publishing {
//    repositories {
//        maven {
//            name = "mavenCentral"
//            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//            credentials {
//                username = project.findProperty("com.fsryan.ossrh.release.username")?.toString().orEmpty()
//                password = project.findProperty("com.fsryan.ossrh.release.password")?.toString().orEmpty()
//            }
//        }
//    }
//}
