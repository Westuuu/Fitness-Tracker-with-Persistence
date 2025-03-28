plugins {
    id("java")
    kotlin("jvm") version "1.9.10"
}

group = "com.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.hibernate:hibernate-core:6.1.5.Final")
    implementation("javax.persistence:javax.persistence-api:2.2")
    implementation("javax.transaction:javax.transaction-api:1.3")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
    implementation("org.slf4j:slf4j-api:2.0.0-alpha1")
    implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
}

tasks.test {
    useJUnitPlatform()
}

