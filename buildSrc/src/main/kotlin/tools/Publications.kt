package tools

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.register

/**
 * Specifically, these publications deal with configuring multiplatform publications
 */
object Publications {

    fun allPublications(): Sequence<String> = sequenceOf(
        "AndroidRelease",
        "IosArm32",
        "IosArm64",
        "IosSimulatorArm64",
        "IosX64",
        "IosArm32",
        "IosArm64",
        "IosSimulatorArm64",
        "IosX64",
        "Jvm",
        "Js",
        "KotlinMultiplatform",
        "LinuxX64",
        "MacosX64",
        "MacosArm64",
        "TvosArm64",
        "TvosSimulatorArm64",
        "TvosX64",
        "WatchosX86",
        "WatchosArm32",
        "WatchosArm64",
        "WatchosSimulatorArm64"
    )

    fun iosMacTargetPublications(): Sequence<String> = allPublications().filter {
        it.startsWith("Ios") || it.startsWith("Macos") || it.startsWith("Tvos") || it.startsWith("Watchos")
    }

    fun jvmAndroidTargetPublications(): Sequence<String> = allPublications().filter {
        it == "AndroidRelease" || it == "Jvm"
    }

    fun iosMacPublicationTasks(): Set<String> {
        return iosMacTargetPublications().map(::publicationTaskFor).toSet()
    }

    fun allPublicationTasks(): Set<String> {
        return allPublications().map(::publicationTaskFor).toSet()
    }

    fun PublishingExtension.configureMultiplatformPublishingFor(project: Project) {
        publications.whenObjectAdded {
            (this as? MavenPublication)?.let { publication ->
                publication.pom.withXml {
                    val root = asNode()
                    root.appendNode("name", project.name)
                    root.appendNode("description", project.findProperty("com.fsryan.tools.logging.${project.name.replace(Regex("[^a-zA-Z0-9]"), "")}.description"))
                    root.appendNode("url", "https://github.com/ryansgot/logging")

                    root.appendNode("licenses").apply {
                        appendNode("license").apply {
                            appendNode("name", "The Apache Software License, Version 2.0")
                            appendNode("url", "https://www.apache.org/licenses/LICENSE-2.0.txt")
                            appendNode("distribution", "repo")
                        }
                    }

                    root.appendNode("developers").apply {
                        appendNode("developer").apply {
                            appendNode("id", "fsryan")
                            appendNode("name", "Ryan Scott")
                            appendNode("organization", "fsryan")
                            appendNode("organizationUrl", "https://github.com/ryansgot/logging")    // <-- TODO: better url
                        }
                    }

                    root.appendNode("scm").apply {
                        appendNode("url", "https://github.com/ryansgot/logging.git")
                    }
                }
                if (publication.name == "jvm") {
                    publication.artifact(project.getOrCreateEmptyJarTask()) {
                        classifier = "javadoc"
                    }
                } else {
                    publication.artifact(project.getOrCreateEmptyJarTask()) {
                        classifier = "javadoc"
                    }
                    publication.artifact(project.getOrCreateEmptyJarTask()) {
                        classifier = "kdoc"
                    }
                }
                if (publication.name.contains("linux") || publication.name.contains("macos")) {
                    publication.artifact(project.getOrCreateEmptyJarTask())
                }
            }
        }
    }

    private fun Project.getOrCreateEmptyJarTask(): Task {
        return tasks.findByName("emptyJar") ?: tasks.register<Jar>("emptyJar") {
            archiveAppendix.set("empty")
        }.get()
    }

    private fun publicationTaskFor(name: String): String = "publish${name}PublicationToMavenCentralRepository"
}