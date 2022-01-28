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
    jvm("jvm") {
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
//    mingwX64()
    if (canBuildMacOSX64) {
        macosX64()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                with(Deps.Test.JetBrains) {
                    implementation(project(":logging"))
                    implementation(test)
                    implementation(testAnnotations)
                }
            }
        }
        val commonTest by getting {
            dependencies {
            }
        }
        val sharedAndroidJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
            }
        }
        val androidMain by getting {
            kotlin.srcDir("src/sharedAndroidJvmMain/kotlin")
            dependsOn(sharedAndroidJvmMain)
            dependencies {
                with(Deps.Main.AndroidX) {
                }
            }
        }
        val jvmMain by getting {
            kotlin.srcDir("src/sharedAndroidJvmMain/kotlin")
            dependsOn(sharedAndroidJvmMain)
            dependencies {
                with(Deps.Test.JUnit5) {
                    implementation(jupiterApi)
                    implementation(params)
                    runtimeOnly(engine)
                    runtimeOnly(platformLauncher)
                }
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
            dependsOn(commonMain)
            dependencies {

            }
        }
        val linuxX64Main by getting {
            dependsOn(nativeMain)
            dependencies {
            }
        }
        // Cannot build this on Mac
//        val mingwX64Main by getting {
//            dependsOn(nonJvmMain)
//            dependencies {
//            }
//        }
        if (canBuildMacOSX64) {
            val macosX64Main by getting {
                dependsOn(nativeMain)
                dependencies {
                }
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

publishing {
    repositories {
        maven {
            name = "mavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.findProperty("com.fsryan.ossrh.release.username")?.toString().orEmpty()
                password = project.findProperty("com.fsryan.ossrh.release.password")?.toString().orEmpty()
            }
        }
    }
}

if (!canBuildMacOSX64) {
    println("SUPPLYING DEFAULT VERSION OF THE MAC OSX PUBLISHING TASK")
    tasks.create(name = "publishMacosX64PublicationToMavenCentralRepository") {
        doLast {
            println("Cannot publish MacosX64Publication because this platform is not Mac")
        }
    }
}

tasks.create(name = "release") {
    dependsOn(
        "publishKotlinMultiplatformPublicationToMavenCentralRepository",
        "publishAndroidReleasePublicationToMavenCentralRepository",
        "publishJsPublicationToMavenCentralRepository",
        "publishJvmPublicationToMavenCentralRepository",
        "publishMacosX64PublicationToMavenCentralRepository",
        "publishLinuxX64PublicationToMavenCentralRepository"
    )
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
