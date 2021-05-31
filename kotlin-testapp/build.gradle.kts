import deps.Deps

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm")
}

application {
    mainClassName = "com.fsryan.tools.logging.testappkt.MainKt"
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(project(":logging"))

    implementation(Deps.Main.JetBrains.kotlinSTDLib)

    with(Deps.Test.JUnit5) {
        testImplementation(jupiterApi)
        testImplementation(params)
        testRuntimeOnly(engine)
    }

    testImplementation(project(":logging-test"))
}

tasks.test {
    useJUnitPlatform {}
}

tasks.compileKotlin {
    kotlinOptions {
        jvmTarget = "1.6"
    }
}

tasks.compileTestKotlin {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}