import com.fsryan.gradle.coverage.ClassFilter
import deps.Deps
import deps.Deps.Versions
import tools.Info

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("signing")
    jacoco
    `maven-publish`
    id("org.jetbrains.dokka")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    if (Info.canBuildMacIos) {
        listOf(
            iosArm32(),
            iosArm64(),
            iosSimulatorArm64(),
            iosX64(),
            macosX64(),
            macosArm64(),
            tvosArm64(),
            tvosSimulatorArm64(),
            tvosX64(),
            watchosX86(),
            watchosArm32(),
            watchosArm64(),
            watchosSimulatorArm64()
        ).forEach {
            it.binaries.framework {
                baseName = "fslogging"
            }
        }
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

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":logging"))
                with(Deps.Main.JetBrains) {
                    implementation(kotlinSTDLibCommon)
                    implementation(dateTime)
                    implementation(coroutines)
                }

                with(Deps.Test.JetBrains) {
                    implementation(test)
                }

                implementation(Deps.Main.Touchlab.statelyIsolate)
            }
        }
        val sharedAndroidJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Test.JUnit5.jupiterApi)
            }
        }
        val androidMain by getting {
            dependsOn(sharedAndroidJvmMain)
            dependencies {
                with(Deps.Main.AndroidX) {
                    implementation(annotation)
                    implementation(appCompat)
                    implementation(coreKtx)
                }

                implementation(Deps.Main.JetBrains.coroutinesAndroid)
            }
        }
        val jvmMain by getting {
            dependsOn(sharedAndroidJvmMain)
        }
        val nonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Main.Autodesk.coroutineWorker)
            }
        }
        val jsMain by getting {
            dependsOn(nonJvmMain)
            dependencies {
            }
        }
        val nativeMain by creating {
            dependsOn(nonJvmMain)
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
        }
        if (Info.canBuildMacIos) {
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
            val macosArm64Main by getting {
                dependsOn(nativeMain)
            }
            val macosX64Main by getting {
                dependsOn(nativeMain)
            }
            val tvosArm64Main by getting {
                dependsOn(iosMain)
            }
            val tvosSimulatorArm64Main by getting {
                dependsOn(iosMain)
            }
            val tvosX64Main by getting {
                dependsOn(iosMain)
            }
            val watchosX86Main by getting {
                dependsOn(iosMain)
            }
            val watchosArm32Main by getting {
                dependsOn(iosMain)
            }
            val watchosArm64Main by getting {
                dependsOn(iosMain)
            }
            val watchosSimulatorArm64Main by getting {
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
}