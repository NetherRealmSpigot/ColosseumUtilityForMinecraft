plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.20"
    id("org.jetbrains.dokka") version "1.8.10"
    id("colosseum.gradle") version "0.1-SNAPSHOT"
    `maven-publish`
}

group = "colosseum.minecraft"
version = "0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(17)
}

repositories {
    colosseum {
        colosseumContent(listOf(
            maven("\u0068\u0074\u0074\u0070\u0073\u003A\u002F\u002F\u0063\u006F\u0066\u0066\u0065\u0065\u0077\u0061\u0072\u0065\u0068\u006F\u0075\u0073\u0065\u002E\u006E\u006F\u0072\u0074\u0068\u0072\u0065\u0061\u006C\u006D\u002E\u0074\u006F\u0070\u002F\u0073\u006E\u0061\u0070\u0073\u0068\u006F\u0074\u0073\u002F")
        ), listOf("\u0063\u006F\u006D\u002E\u0068\u0070\u0066\u0078\u0064\u002E\u0070\u0061\u006E\u0064\u0061\u0073\u0070\u0069\u0067\u006F\u0074"))
    }
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
}

dependencies {
    compileOnly("\u0063\u006F\u006D\u002E\u0068\u0070\u0066\u0078\u0064\u002E\u0070\u0061\u006E\u0064\u0061\u0073\u0070\u0069\u0067\u006F\u0074\u003A\u0070\u0061\u006E\u0064\u0061\u0073\u0070\u0069\u0067\u006F\u0074\u002D\u0061\u0070\u0069:${rootProject.findProperty("jar_version")}")
}

tasks.jar {
    archiveClassifier.set("original")
}

val javadocJar = tasks.named<Jar>("javadocJar") {
    from(tasks.named("dokkaJavadoc"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }

    System.getenv("COLOSSEUM_MAVEN_URL").let { mavenUrl ->
        if (mavenUrl == null) {
            return@let
        }
        println("Configuring publishing")
        repositories {
            maven {
                name = "ColosseumMaven"
                url = uri(mavenUrl)
                credentials {
                    username = System.getenv("COLOSSEUM_MAVEN_USERNAME")
                    password = System.getenv("COLOSSEUM_MAVEN_PASSWORD")
                }
            }
        }
    }
}
