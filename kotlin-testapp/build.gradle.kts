import deps.Deps.mainDep

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

    implementation(mainDep(producer = "jetbrains", name = "kotlin-stdlib"))

    testImplementation(deps.Deps.testDep(producer = "junit5", name = "api"))
    testImplementation(deps.Deps.testDep(producer = "junit5", name = "params"))
    testRuntimeOnly(deps.Deps.testDep(producer = "junit5", name = "engine"))

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