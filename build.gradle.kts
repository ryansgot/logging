buildscript {
    repositories {
        if (rootProject.hasProperty("fsryan.useMavenLocal")) {
            mavenLocal()
        }
        maven { url = uri("https://plugins.gradle.org/m2/") }
        mavenCentral()
        google()
        maven { url = uri("https://maven.fabric.io/public") }
    }
    dependencies {
        classpath(deps.Deps.Plugin.Android.gradle)
        with(deps.Deps.Plugin.JetBrains) {
            classpath(dokka)
            classpath(gradle)
        }
        classpath(deps.Deps.Plugin.Google.gms)
        classpath(deps.Deps.Plugin.Google.crashltyics)
        classpath(deps.Deps.Plugin.FSRyan.androidJavaCoverageMerger)
        classpath(deps.Deps.Plugin.FSRyan.gradlePublishing)
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
        jcenter()
    }

    group = "com.fsryan.tools"
    version = "${deps.Deps.Versions.Global.FSRyan.publication}${if (project.hasProperty("postfixDate")) ".${tools.Info.timestamp}" else ""}"

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

    var configuredPublishing = false
    var configuredSigning = false


    if (!tools.Info.canBuildMacIos) {
        println("Creating dummy implementation of mac/ios publication task")
        tools.Publications.iosMacPublicationTasks().forEach { taskName ->
            tasks.create(name = taskName) {
                doLast {
                    println("Cannot perform $taskName because this platform is not Mac")
                }
            }
        }
    }

    if (plugins.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
        tasks.create(name = "release") {
            dependsOn(tools.Publications.allPublicationTasks().toTypedArray())
        }
    }

    fun configurePublishingIfPossible() {
        extensions.findByType(PublishingExtension::class)?.let { publishing ->
            publishing.repositories {
                maven {
                    name = "mavenCentral"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = project.findProperty("com.fsryan.ossrh.release.username")?.toString().orEmpty()
                        password = project.findProperty("com.fsryan.ossrh.release.password")?.toString().orEmpty()
                    }
                }
            }
            configuredPublishing = true
        }
    }

    fun configureSigningIfPossible() {
        extensions.findByType(PublishingExtension::class)?.let { publishing ->
            extensions.findByType(SigningExtension::class)?.let { signingExtension ->
                if (project.hasProperty("signing.keyId")) {
                    println("signing.keyId FOUND!!!")
                    if (project.hasProperty("signing.password")) {
                        println("signing.password FOUND!!!")
                        if (project.hasProperty("signing.secretKeyRingFile")) {
                            println("signing.secretKeyRingFile FOUND!!!")
                            signingExtension.sign(publishing.publications)
                            configuredSigning = true
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

    fun configurePublishingAndSigningIfPossible() {
        if (!configuredPublishing) {
            configurePublishingIfPossible()
        }
        if (!configuredSigning) {
            configureSigningIfPossible()
        }
    }

    plugins.findPlugin(SigningPlugin::class)?.let {
        plugins.findPlugin(PublishingPlugin::class)?.let {
            configurePublishingAndSigningIfPossible()
        }
    } ?: plugins.whenPluginAdded {
        if (this is SigningPlugin) {
            plugins.findPlugin(PublishingPlugin::class)?.let {
                configurePublishingAndSigningIfPossible()
            }
        }
        if (this is PublishingPlugin) {
            plugins.findPlugin(SigningPlugin::class)?.let {
                configurePublishingAndSigningIfPossible()
            }
        }
    }
}