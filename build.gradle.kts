buildscript {
    repositories {
        if (rootProject.hasProperty("fsryan.useMavenLocal")) {
            mavenLocal()
        }
        maven { url = uri("https://plugins.gradle.org/m2/") }
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
        classpath(deps.Deps.Plugin.Android.gradle)
        classpath(deps.Deps.Plugin.JetBrains.gradle)
        classpath(deps.Deps.Plugin.Google.gms)
        classpath(deps.Deps.Plugin.Google.crashltyics)
        classpath(deps.Deps.Plugin.FSRyan.gradlePublishing)
        classpath(deps.Deps.Plugin.Dcendents.androidMavenGradle)
        classpath(deps.Deps.Plugin.NewRelic.gradle)
    }
}

allprojects {
    repositories {
        if (rootProject.hasProperty("fsryan.useMavenLocal")) {
            mavenLocal()
        }
        mavenCentral()
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

    fun com.android.build.gradle.LibraryExtension.applyConfig() {
        buildFeatures.buildConfig = false
    }

    plugins.findPlugin(com.android.build.gradle.LibraryPlugin::class)?.let {
        extensions.getByType(com.android.build.gradle.LibraryExtension::class).applyConfig()
    } ?: plugins.whenPluginAdded {
        if (this is com.android.build.gradle.LibraryPlugin) {
            extensions.getByType(com.android.build.gradle.LibraryExtension::class).applyConfig()
        }
    }

    fun configureSigningIfPossible() {
        extensions.findByType(PublishingExtension::class)?.let { publishingExtension ->
            extensions.findByType(SigningExtension::class)?.let { signingExtension ->
                if (project.hasProperty("signing.keyId")) {
                    if (project.hasProperty("signing.password")) {
                        if (project.hasProperty("signing.secretKeyRingFile")) {
                            signingExtension.sign(publishingExtension.publications)
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
        }
    }

    plugins.findPlugin(SigningPlugin::class)?.let {
        plugins.findPlugin(PublishingPlugin::class)?.let {
            configureSigningIfPossible()
        }
    } ?: plugins.whenPluginAdded {
        if (this is SigningPlugin) {
            plugins.findPlugin(PublishingPlugin::class)?.let {
                configureSigningIfPossible()
            }
        }
        if (this is PublishingPlugin) {
            plugins.findPlugin(SigningPlugin::class)?.let {
                configureSigningIfPossible()
            }
        }
    }
}