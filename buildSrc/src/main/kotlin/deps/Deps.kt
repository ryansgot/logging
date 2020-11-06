package deps

object Deps {
    private val versions = mapOf(
        "global" to mapOf(
            "jetbrains" to mapOf(
                "kotlin" to "1.4.10"
            ),
            "android" to mapOf(
                "minSdk" to "16",
                "targetSdk" to "30",
                "compileSdk" to "30"
            ),
            "fsryan" to mapOf(
                "publication" to "0.2.0"
            )
        ),
        "plugins" to mapOf(),
        "test" to mapOf(
            "junit" to mapOf(
                "legacy" to "4.12"
            ),
            "junit5" to mapOf(
                "jupiter" to "5.3.1",
                "platform" to "1.3.1"
            ),
            "mockk" to mapOf(
                "core" to "1.9.3"
            )
        ),
        "main" to mapOf(
            "androidx" to mapOf(
                "annotation" to "1.1.0",
                "core" to "1.0.2",
                "constraint-layout" to "1.1.3"
            ),
            "datadog" to mapOf(
                "ddsdk" to "1.4.3"
            ),
            "google" to mapOf(
                "firebase-crashlytics-ktx" to "17.2.2",
                "firebase-analytics-ktx" to "17.6.0",
                "jsr305" to "3.0.2"
            ),
            "microsoft" to mapOf(
                "appcenter" to "2.4.0",
                "appcenter3" to "3.2.2"
            ),
            "newrelic" to mapOf(
                "android-agent" to "5.28.0"
            ),
            "urbanairship" to mapOf(
                "core" to "14.0.0"
            )
        )
    )
    private val deps = mapOf(
        "plugins" to mapOf(),
        "test" to mapOf(
            "junit" to mapOf(
                "legacy" to "junit:junit:${ver("test", "junit", "legacy")}"
            ),
            "junit5" to mapOf(
                "api" to "org.junit.jupiter:junit-jupiter-api:${ver("test", "junit5", "jupiter")}",
                "engine" to "org.junit.jupiter:junit-jupiter-engine:${ver("test", "junit5", "jupiter")}",
                "params" to "org.junit.jupiter:junit-jupiter-params:${ver("test", "junit5", "jupiter")}"
            ),
            "mockk" to mapOf(
                "core" to "io.mockk:mockk:${ver("test", "mockk", "core")}",
                "android" to "io.mockk:mockk-android:${ver("test", "mockk", "core")}"
            )
        ),
        "main" to mapOf(
            "androidx" to mapOf(
                "annotation" to "androidx.annotation:annotation:${ver("main", "androidx", "annotation")}",
                "appcompat" to "androidx.appcompat:appcompat:${ver("main", "androidx", "core")}",
                "core-ktx" to "androidx.core:core-ktx:${ver("main", "androidx", "core")}",
                "constraint-layout" to "androidx.constraintlayout:constraintlayout:${ver("main", "androidx", "constraint-layout")}"
            ),
            "datadog" to mapOf(
                "ddsdk" to "com.datadoghq:dd-sdk-android:${ver("main", "datadog", "ddsdk")}"
            ),
            "google" to mapOf(
                "firebase-analytics-ktx" to "com.google.firebase:firebase-analytics-ktx:${ver("main", "google", "firebase-analytics-ktx")}",
                "firebase-crashlytics-ktx" to "com.google.firebase:firebase-crashlytics-ktx:${ver("main", "google", "firebase-crashlytics-ktx")}",
                "jsr305" to "com.google.code.findbugs:jsr305:${ver("main", "google", "jsr305")}"
            ),
            "jetbrains" to mapOf(
                "kotlin-stdlib" to "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${ver("global", "jetbrains", "kotlin")}"
            ),
            "microsoft" to mapOf(
                "appcenter-analytics" to "com.microsoft.appcenter:appcenter-analytics:${ver("main", "microsoft", "appcenter")}",
                "appcenter-crashes" to "com.microsoft.appcenter:appcenter-crashes:${ver("main", "microsoft", "appcenter")}",
                "appcenter-analytics3" to "com.microsoft.appcenter:appcenter-analytics:${ver("main", "microsoft", "appcenter3")}",
                "appcenter-crashes3" to "com.microsoft.appcenter:appcenter-crashes:${ver("main", "microsoft", "appcenter3")}"
            ),
            "newrelic" to mapOf(
                "android-agent" to "com.newrelic.agent.android:android-agent:${ver("main", "newrelic", "android-agent")}"
            ),
            "urbanairship" to mapOf(
                "core" to "com.urbanairship.android:urbanairship-core:${ver("main", "urbanairship", "core")}"
            )
        )
    )

    fun ver(domain: String, producer: String, name: String): String = findObj(versions, "versions", domain, producer, name)
    fun dep(domain: String, producer: String, name: String): String  = findObj(deps, "deps", domain, producer, name)
    fun pluginDep(producer: String, name: String) = dep("plugin", producer, name)
    fun testDep(producer: String, name: String) = dep("test", producer, name)
    fun mainDep(producer: String, name: String) = dep("main", producer, name)
    fun findObj(src: Map<String, Map<String, Map<String, String>>>, srcName: String, domain: String, producer: String, name: String): String = src[domain]?.get(producer)?.get(name)
        ?: throw IllegalArgumentException("did not find $srcName.$domain.$producer.$name")
}