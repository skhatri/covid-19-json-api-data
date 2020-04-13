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

val sparkV by extra("3.0.0-preview2")
val scalaV by extra("2.12.10")

dependencies {
    implementation("org.scala-lang:scala-library:$scalaV")
    implementation("org.scala-lang:scala-compiler:$scalaV")
    implementation("org.apache.spark:spark-core_2.12:$sparkV")
    implementation("org.apache.spark:spark-sql_2.12:$sparkV")
}

task("extract", JavaExec::class) {
    main = project.extra["app"] as String
    classpath = sourceSets["main"].runtimeClasspath
    jvmArgs = listOf("-Xms512m", "-Xmx512m")
}

tasks.wrapper {
    gradleVersion = "6.2.2"
}
