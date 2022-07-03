package deps

object Deps {

    object Versions {
        object Global {
            object Android {
                const val compileSdk = 31
                const val minSdk = 21
                const val targetSdk = 31
            }
            object FSRyan {
                const val publication = "0.4.0"
            }
            object JetBrains {
                const val kotlin = "1.6.21"
            }
            object NewRelic {
                const val agent = "6.4.1"
            }
        }
        object Main {
            object Airship {
                const val core = "14.4.4"
            }
            object AndroidX {
                const val annotation = "1.2.0"
                const val appCompat = "1.3.0"
                const val core = "1.5.0"
                const val constraintLayout = "2.0.4"
            }
            object Google {
                const val analytics = "20.1.2"
                const val crashlytics = "18.2.9"
                const val jsr305 = "3.0.2"
            }
            object JetBrains {
                const val coroutines = "1.6.3"
                const val datetime = "0.3.2"
            }
            object Microsoft {
                const val appCenter3 = "3.3.1"
                const val appCenter4 = "4.4.5"
            }
        }
        object Plugin {
            object Android {
                const val gradle = "7.0.4"
            }
            object Eclemma {
                const val jacoco = "0.8.8"
            }
            object FSRyan {
                const val androidJavaCoverageMerger = "0.2.0"
                const val gradlePublishing = "0.3.0"
            }
            object Google {
                const val crashlytics = "2.8.1"
                const val gms = "4.3.10"
            }
            object JetBrains {
                const val dokka = "1.6.21"
            }
        }
        object Test {
            object AndroidX {
                const val espresso = "3.3.0"
                const val core = "1.3.0"
                const val junit = "1.1.2"
            }
            object JUnit {
                const val lib = "4.12"
            }
            object JUnit5 {
                const val jupiter = "5.8.1"
                const val platform = "1.8.1"
            }
            object MockK {
                const val core = "1.10.6"
            }
            object Objenesis {
                const val lib = "2.6"
            }
        }
    }

    object Main {
        object Airship {
            private val version = Versions.Main.Airship
            const val core = "com.urbanairship.android:urbanairship-core:${version.core}"
        }
        object AndroidX {
            private val version = Versions.Main.AndroidX
            const val annotation = "androidx.annotation:annotation:${version.annotation}"
            const val appCompat = "androidx.appcompat:appcompat:${version.appCompat}"
            const val coreKtx = "androidx.core:core-ktx:${version.core}"
            const val constraintLayout = "androidx.constraintlayout:constraintlayout:${version.constraintLayout}"
        }
        object Google {
            private val version = Versions.Main.Google
            const val analytics = "com.google.firebase:firebase-analytics-ktx:${version.analytics}"
            const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx:${version.crashlytics}"
            const val jsr305 = "com.google.code.findbugs:jsr305:${version.jsr305}"
        }
        object JetBrains {
            private val globalVersion = Versions.Global.JetBrains
            private val version = Versions.Main.JetBrains
            const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${version.coroutines}"
            const val coroutinesJvm = "org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:${version.coroutines}"
            const val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${version.coroutines}"
            const val dateTime = "org.jetbrains.kotlinx:kotlinx-datetime:${version.datetime}"
            const val kotlinSTDLibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:${globalVersion.kotlin}"
            const val kotlinSTDLib = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${globalVersion.kotlin}"
        }
        object Microsoft {
            private val version = Versions.Main.Microsoft
            const val analytics3 = "com.microsoft.appcenter:appcenter-analytics:${version.appCenter3}"
            const val analytics4 = "com.microsoft.appcenter:appcenter-analytics:${version.appCenter4}"
            const val crashes3 = "com.microsoft.appcenter:appcenter-crashes:${version.appCenter3}"
            const val crashes4 = "com.microsoft.appcenter:appcenter-crashes:${version.appCenter4}"
        }
        object NewRelic {
            private val globalVersion = Versions.Global.NewRelic
            const val agent = "com.newrelic.agent.android:android-agent:${globalVersion.agent}"
        }
    }
    object Plugin {
        object Android {
            private val version = Versions.Plugin.Android
            const val gradle = "com.android.tools.build:gradle:${version.gradle}"
        }
        object FSRyan {
            private val version = Versions.Plugin.FSRyan
            const val androidJavaCoverageMerger = "com.fsryan.gradle.coverage:android-java-coverage-merger:${version.androidJavaCoverageMerger}"
            const val gradlePublishing = "com.fsryan.gradle:fsryan-gradle-publishing:${version.gradlePublishing}"
        }
        object Google {
            private val version = Versions.Plugin.Google
            const val crashltyics = "com.google.firebase:firebase-crashlytics-gradle:${version.crashlytics}"
            const val gms = "com.google.gms:google-services:${version.gms}"
        }
        object JetBrains {
            private val globalVersion = Versions.Global.JetBrains
            private val version = Versions.Plugin.JetBrains
            const val dokka = "org.jetbrains.dokka:org.jetbrains.dokka.gradle.plugin:${version.dokka}"
            const val gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${globalVersion.kotlin}"
        }
        object NewRelic {
            private val globalVersion = Versions.Global.NewRelic
            const val gradle = "com.newrelic.agent.android:agent-gradle-plugin:${globalVersion.agent}"
        }
    }
    object Test {
        object AndroidX {
            private val version = Versions.Test.AndroidX
            const val coreKtx = "androidx.test:core-ktx:${version.core}"
            const val junitKtx = "androidx.test.ext:junit-ktx:${version.junit}"
            const val rules = "androidx.test:rules:${version.core}"
            const val runner = "androidx.test:runner:${version.core}"
        }
        object JetBrains {
            private val globalVersion = Versions.Global.JetBrains
            const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${globalVersion.kotlin}"
            const val testAnnotations = "org.jetbrains.kotlin:kotlin-test-annotations-common:${globalVersion.kotlin}"
            const val test = "org.jetbrains.kotlin:kotlin-test-annotations-common:${globalVersion.kotlin}"
        }
        object JUnit {
            private val version = Versions.Test.JUnit
            const val lib = "junit:junit:${version.lib}"
        }
        object JUnit5 {
            private val version = Versions.Test.JUnit5
            const val jupiterApi = "org.junit.jupiter:junit-jupiter-api:${version.jupiter}"
            const val engine = "org.junit.jupiter:junit-jupiter-engine:${version.jupiter}"
            const val params = "org.junit.jupiter:junit-jupiter-params:${version.jupiter}"
            const val platformLauncher = "org.junit.platform:junit-platform-launcher:${version.platform}"
        }
        object MockK {
            private val version = Versions.Test.MockK
            const val android = "io.mockk:mockk-android:${version.core}"
            const val jvm = "io.mockk:mockk:${version.core}"
        }
        object Objenesis {
            private val version = Versions.Test.Objenesis
            const val lib = "org.objenesis:objenesis:${version.lib}"
        }
    }
}