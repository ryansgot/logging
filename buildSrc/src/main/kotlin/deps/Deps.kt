package deps

object Deps {
    private val versions = mapOf(
        "global" to mapOf(
            "jetbrains" to mapOf(
                "kotlin" to "1.3.50"
            ),
            "android" to mapOf(
                "minSdk" to "16",
                "targetSdk" to "29",
                "compileSdk" to "29"
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
                "core" to "1.0.2",
                "constraint-layout" to "1.1.3"
            ),
            "google" to mapOf(
                "firebase-core" to "17.1.0",
                "firebase-crashlytics-jdk" to "2.10.1",
                "gms-base" to "17.0.0",
                "jsr305" to "3.0.2"
            ),
            "microsoft" to mapOf(
                "appcenter" to "2.4.0"
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
                "appcompat" to "androidx.appcompat:appcompat:${ver("main", "androidx", "core")}",
                "core-ktx" to "androidx.core:core-ktx:${ver("main", "androidx", "core")}",
                "constraint-layout" to "androidx.constraintlayout:constraintlayout:${ver("main", "androidx", "constraint-layout")}"
            ),
            "google" to mapOf(
                "firebase-analytics" to "com.google.firebase:firebase-analytics:${ver("main", "google", "firebase-core")}",
                "firebase-core" to "com.google.firebase:firebase-core:${ver("main", "google", "firebase-core")}",
                "firebase-crashlytics-jdk" to "com.crashlytics.sdk.android:crashlytics:${ver("main", "google", "firebase-crashlytics-jdk")}",
                "gms-tagmanager" to "com.google.android.gms:play-services-tagmanager:${ver("main", "google", "gms-base")}",
                "jsr305" to "com.google.code.findbugs:jsr305:${ver("main", "google", "jsr305")}"
            ),
            "jetbrains" to mapOf(
                "kotlin-stdlib" to "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${ver("global", "jetbrains", "kotlin")}"
            ),
            "microsoft" to mapOf(
                "appcenter-analytics" to "com.microsoft.appcenter:appcenter-analytics:${ver("main", "microsoft", "appcenter")}",
                "appcenter-crashes" to "com.microsoft.appcenter:appcenter-crashes:${ver("main", "microsoft", "appcenter")}"
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