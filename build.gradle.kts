import org.gradle.api.artifacts.maven.MavenDeployment
import org.gradle.api.tasks.bundling.Jar

buildscript {
    fun createBuildVersion(projectVersion: String): String {
        var derivedVersion = projectVersion
        val versionWithSnapshot = projectVersion.replace("-SNAPSHOT", "")
        val buildNumber = System.getenv("TRAVIS_BUILD_NUMBER") ?: "0"
        if (project.extra["release"] == "true") {
            derivedVersion = "${versionWithSnapshot}.${buildNumber}"
        } else {
            derivedVersion = "${versionWithSnapshot}.${buildNumber}-SNAPSHOT"
        }
        println("effective project version: ${derivedVersion}")
        return derivedVersion
    }
    project.version = createBuildVersion("${project.version}")
}

plugins {
    idea
    java
    id("scala")
}

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

configurations {
    implementation {
        resolutionStrategy.failOnVersionConflict()
    }
}

dependencies {
    implementation ("org.scala-lang:scala-library:2.12.10")
    implementation ("org.scala-lang:scala-compiler:2.12.10")
    implementation ("org.apache.spark:spark-core_2.12:3.0.0-preview2")
    implementation ("org.apache.spark:spark-sql_2.12:3.0.0-preview2")
    implementation ("org.apache.spark:spark-csv_2.12:3.0.0-preview2")

    testImplementation("org.mockito:mockito-core:3.3.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.1")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.6.1")

    testImplementation("org.junit.platform:junit-platform-commons:1.6.1")
    testImplementation("org.junit.platform:junit-platform-runner:1.6.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.6.1")
    testRuntimeOnly("org.junit.platform:junit-platform-engine:1.6.1")
}

