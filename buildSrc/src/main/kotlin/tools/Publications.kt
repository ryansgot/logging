package tools

object Publications {

    fun allPublications(): Sequence<String> = sequenceOf(
        "KotlinMultiplatform",
        "AndroidRelease",
        "IosArm32",
        "IosArm64",
        "IosSimulatorArm64",
        "IosX64",
        "Jvm",
        "Js",
        "LinuxX64",
        "MacosX64"
    )

    fun iosMacTargetPublications(): Sequence<String> = allPublications().filter {
        it.startsWith("Ios") || it.startsWith("Macos")
    }

    fun iosMacPublicationTasks(): Set<String> {
        return iosMacTargetPublications().map(::publicationTaskFor).toSet()
    }

    fun allPublicationTasks(): Set<String> {
        return allPublications().map(::publicationTaskFor).toSet()
    }

    private fun publicationTaskFor(name: String): String = "publish${name}PublicationToMavenCentralRepository"
}