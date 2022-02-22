package deps

object Deps {

    object Versions {
        object Global {
            object Android {
                const val compileSdk = 30
                const val minSdk = 16
                const val targetSdk = 30
            }
            object FSRyan {
                const val publication = "0.3.1"
            }
            object JetBrains {
                const val kotlin = "1.5.10"
            }
            object NewRelic {
                const val agent = "5.28.1"
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
                const val analytics = "19.0.0"
                const val crashlytics = "18.0.0"
                const val jsr305 = "3.0.2"
            }
            object Microsoft {
                const val appCenter3 = "3.3.1"
                const val appCenter4 = "4.1.1"
            }
        }
        object Plugin {
            object Android {
                const val gradle = "4.2.1"
            }
            object Dcendents {
                const val androidMavenGradle = "2.1"
            }
            object FSRyan {
                const val gradlePublishing = "0.1.3"
            }
            object Google {
                const val crashlytics = "2.6.1"
                const val gms = "4.3.4"
            }
        }
        object Test {

            object JUnit {
                const val lib = "4.12"
            }
            object JUnit5 {
                const val jupiter = "5.7.1"
                const val platform = "1.7.1"
            }
            object MockK {
                const val core = "1.10.6"
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
        object Dcendents {
            private val version = Versions.Plugin.Dcendents
            const val androidMavenGradle = "com.github.dcendents:android-maven-gradle-plugin:${version.androidMavenGradle}"
        }
        object FSRyan {
            private val version = Versions.Plugin.FSRyan
            const val gradlePublishing = "com.fsryan.gradle:fsryan-gradle-publishing:${version.gradlePublishing}"
        }
        object Google {
            private val version = Versions.Plugin.Google
            const val crashltyics = "com.google.firebase:firebase-crashlytics-gradle:${version.crashlytics}"
            const val gms = "com.google.gms:google-services:${version.gms}"
        }
        object JetBrains {
            private val globalVersion = Versions.Global.JetBrains
            const val gradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${globalVersion.kotlin}"
        }
        object NewRelic {
            private val globalVersion = Versions.Global.NewRelic
            const val gradle = "com.newrelic.agent.android:agent-gradle-plugin:${globalVersion.agent}"
        }
    }
    object Test {
        object JUnit {
            private val version = Versions.Test.JUnit
            const val lib = "junit:junit:${version.lib}"
        }
        object JUnit5 {
            private val version = Versions.Test.JUnit5
            const val jupiterApi = "org.junit.jupiter:junit-jupiter-api:${version.jupiter}"
            const val engine = "org.junit.jupiter:junit-jupiter-engine:${version.jupiter}"
            const val params = "org.junit.jupiter:junit-jupiter-params:${version.jupiter}"
        }
        object MockK {
            private val version = Versions.Test.MockK
            const val android = "io.mockk:mockk-android:${version.core}"
            const val jvm = "io.mockk:mockk:${version.core}"
        }
    }
}