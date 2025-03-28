plugins {
    java
    id("org.openjfx.javafxplugin") version "0.0.13"
    application
}

group = "com.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.openjfx:javafx-controls:21.0.2")
    implementation("org.openjfx:javafx-fxml:21.0.2")
    implementation("org.openjfx:javafx-base:21.0.2")
    implementation("org.openjfx:javafx-graphics:21.0.2")
    implementation(project(":core"))
    implementation(project(":data"))
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("io.github.cdimascio:dotenv-java:2.3.2")

}

application {
    mainClass.set("com.fitness.tracker.app.ui.Main")
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.fitness.tracker.app.ui.Main"
    }
    
    // Include dependencies in the JAR
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

javafx {
    version = "21.0.2"
    modules = listOf(
        "javafx.controls",
        "javafx.fxml",
        "javafx.base",
        "javafx.graphics"
    )
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "20"
    }
}





