import deps.Deps
import deps.Deps.Versions

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("signing")
    jacoco
    `maven-publish`
    id("android-java-coverage-merger")
    id("org.jetbrains.dokka")
}
version = "${Versions.Global.FSRyan.publication}${if (project.hasProperty("postfixDate")) ".${tools.Info.timestamp}" else ""}"
val canBuildMacOSX64 = System.getProperty("os.name") == "Mac OS X"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}



kotlin {
    android {
        publishLibraryVariants("release", "debug")
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
            }
        }
        val sharedAndroidJvmMain by creating {
            dependsOn(commonMain)
            dependencies {
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
            kotlin.srcDir("src/sharedAndroidJvmMain/kotlin")
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
            kotlin.srcDir("src/sharedAndroidJvmTest/kotlin")
            dependsOn(commonTest)
            dependsOn(androidMain)
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
            kotlin.srcDir("src/sharedAndroidJvmMain/kotlin")
            dependsOn(sharedAndroidJvmMain)
            dependencies {

            }
        }
        val jvmTest by getting {
            kotlin.srcDir("src/sharedAndroidJvmTest/kotlin")
            dependsOn(jvmMain)
            dependsOn(sharedAndroidJvmTest)
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
        "publishAndroidDebugPublicationToMavenCentralRepository",
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

jacoco {
    toolVersion = Versions.Plugin.Eclemma.jacoco
}

mergedReportConfig {
    classFilters {
        add(com.fsryan.gradle.coverage.ClassFilter("debug").apply {
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