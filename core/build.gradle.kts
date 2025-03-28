plugins {
    id("java")
}

group = "com.project"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":data"))
    implementation("jakarta.transaction:jakarta.transaction-api:2.0.0")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

}

tasks.test {
    useJUnitPlatform()
}
