import deps.Deps

plugins {
    java
    application
}

application {
    mainClassName = "com.fsryan.tools.logging.testappjava.Main"
}

java.sourceCompatibility = JavaVersion.VERSION_1_8
java.targetCompatibility = JavaVersion.VERSION_1_8

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))

    implementation(project(":logging"))

    compileOnly(Deps.Main.Google.jsr305)

    testImplementation(project(":logging-test"))
}
