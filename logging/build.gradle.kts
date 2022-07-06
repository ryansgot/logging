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
    id("android-java-coverage-merger")
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
            macosX64()
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
                with(Deps.Main.JetBrains) {
                    implementation(kotlinSTDLibCommon)
                    implementation(dateTime)
                    api(coroutines)
                }
            }
        }
        val commonTest by getting {
            dependencies {
//                implementation(Deps.Test.JetBrains.testAnnotationsCommon)
//                implementation(Deps.Test.JetBrains.testCommon)
                implementation(Deps.Test.JetBrains.coroutinesTest)
            }
        }
        val sharedAndroidJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                compileOnly(Deps.Main.JetBrains.coroutinesJvm)
            }
        }
        val sharedAndroidJvmTest by creating {
            dependsOn(commonMain)
            dependsOn(commonTest)
            dependencies {
                with(Deps.Test.JUnit5) {
                    implementation(jupiterApi)
                    implementation(params)
                    runtimeOnly(engine)
                    runtimeOnly(platformLauncher)
                }

                implementation(Deps.Test.MockK.jvm)
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
        val androidTest by getting {
            dependsOn(sharedAndroidJvmTest)
            dependencies {
            }
        }
        val androidAndroidTest by getting {
            dependsOn(androidMain)
            dependencies {

                implementation(Deps.Test.MockK.android) {
                    exclude(module = "objenesis")
                }

                implementation(Deps.Test.Objenesis.lib)

                with(Deps.Test.AndroidX) {
                    implementation(coreKtx)
                    implementation(junitKtx)
                    implementation(rules)
                    implementation(runner)
                }
            }
        }
        val jvmMain by getting {
            dependsOn(sharedAndroidJvmMain)
            dependencies {

            }
        }
        val jvmTest by getting {
            dependsOn(jvmMain)
            dependsOn(sharedAndroidJvmTest)
            dependencies {

            }
        }
        val nonJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Main.Autodesk.coroutineWorker)

                // we use stately-isolate for confining access to mutable state
                // to a single thread, allowing all other
                // code to run on any thread. This confines access to our
                // loggers and metric maps to a single thread, while allowing
                // any thread to be a caller of the logger functions
                implementation(Deps.Main.Touchlab.statelyIsolate)
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
        if (Info.canBuildMacIos) {
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

afterEvaluate {
    tasks.withType(Test::class.java).forEach {
        it.useJUnitPlatform()
    }
}

val TaskContainer.jvmTest
    get() = withType(Test::class).firstOrNull {
        it.name == "jvmTest"
    } ?: error("cannot find jvmTest task")

jacoco {
    toolVersion = Versions.Plugin.Eclemma.jacoco
}

mergedReportConfig {
    classFilters {
        add(ClassFilter("debug").apply {
            includes.add("**/com/fsryan/tools/logging/android/**")
            excludes.addAll(
                listOf(
                    "**/R\$*.class",                                // generated R inner classes
                    "**/R.class",                                   // generated R classes
                    "**/*Test.class",                               // filter test classes
                    "**/BuildConfig*",                              // generated BuildConfig classes

                    "**/*_*.class"                                  // Butterknife/AutoValue/Dagger-generated classes
                )
            )
        })
    }
}