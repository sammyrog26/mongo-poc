import com.diffplug.spotless.LineEnding
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    eclipse
    jacoco
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    id("com.autonomousapps.dependency-analysis") version "1.31.0"
    id("com.diffplug.spotless") version "6.25.0"
}

group = "com.delasport"
version = "0.0.1-SNAPSHOT"
description = "Mongo Poc"
java.sourceCompatibility = JavaVersion.VERSION_21

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

spotless {
    lineEndings = LineEnding.PLATFORM_NATIVE
    java {
        target("src/*/java/**/*.java")

        // Palantir have an issue with JDK21 support https://github.com/palantir/palantir-java-format/pull/935
        // palantirJavaFormat()
        googleJavaFormat()

        removeUnusedImports()
        importOrder()
        formatAnnotations()

        custom("Refuse wildcard imports") {
            // Spotless can't resolve wildcard imports itself.
            // This will require the developers themselves to adhere to best practices.
            val importPattern = "import .*\\*;".toRegex()

            if (importPattern.containsMatchIn(it)) {
                throw AssertionError("Do not use wildcard imports. 'spotlessApply' cannot resolve this issue.")
            } else {
                it
            }
        }
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
}

val springBootAdminVersion = "3.3.0"

dependencies {

    implementation("de.codecentric", "spring-boot-admin-starter-client")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer", "micrometer-registry-prometheus")
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.kafka:spring-kafka")

    compileOnly("org.projectlombok:lombok")

    // Mongo
//    implementation(dep.mongoDBReactiveStream)

    // Reactor
    implementation("org.wso2.orbit.io.projectreactor:reactor-core:3.3.9.wso2v1")

    // internal lib dependencies
    implementation(dep.delasportJavaCommonsTools)
    implementation(dep.cacheStreaming)
    implementation(dep.sportsDataCommon)
    implementation(dep.openApi)
    implementation(libs.lombok)

    implementation(dep.mongoDB)
    implementation(dep.mongoDBCore)
    implementation(dep.springMongo)
    implementation("org.springframework.boot", "spring-boot-starter-data-mongodb", springBootAdminVersion)
    implementation(dep.dotEnv)

    // Processors
    annotationProcessor(libs.lombok)

    // Other libs
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.junitExtensions)
    testImplementation(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    testImplementation(libs.junitExtensions)
    testImplementation(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

dependencyManagement {
    imports {
        mavenBom("de.codecentric:spring-boot-admin-dependencies:$springBootAdminVersion")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(
            TestLogEvent.FAILED,
            TestLogEvent.PASSED,
            TestLogEvent.SKIPPED,
            TestLogEvent.STANDARD_ERROR,
            TestLogEvent.STANDARD_OUT,
        )
        exceptionFormat = TestExceptionFormat.FULL
        showCauses = true
        showExceptions = true
        showStackTraces = true
    }
    jvmArgs = listOf("-Xshare:off")
}

tasks.getByName<Jar>("jar") {
    enabled = false
}

tasks.withType<JavaCompile> {
    dependsOn("spotlessApply")
}

task<Exec>("createDockerImage") {
    commandLine("docker", "context", "use", "default")
    commandLine("docker", "build", "--no-cache", "-f", "Dockerfile", ".")
    dependsOn("assemble")
}

fun ignorePackagesInJacocoReport(classDirectories: ConfigurableFileCollection) {
    classDirectories.setFrom(
        files(
            classDirectories.files.map {
                fileTree(it).apply {
                    exclude(
                        "com/**/test/*",
                        "**/enums/*",
                        "**/config/*",
                        "**/constants/*",
                        "**/controller/**",
                        "**/*IntegrationTest.java",
                    )
                }
            },
        ),
    )
}

tasks.jacocoTestReport {
    sourceSets(sourceSets.main.get())
    executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

    reports {
        xml.required.set(true)
        html.required.set(true)
        xml.outputLocation.set(layout.buildDirectory.file("/reports/jacoco/jacocoTestReport/jacocoTestReport.xml"))
        html.outputLocation.set(layout.buildDirectory.dir("/reports/jacoco"))
    }

    ignorePackagesInJacocoReport(classDirectories)
}

tasks.processResources {
    // Add version value into application properties
    filesMatching("**/application.yml") {
        expand(mapOf("projectVersion" to project.version))
    }
}
