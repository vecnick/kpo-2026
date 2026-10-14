plugins {
    java
    jacoco
    checkstyle
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "vyshkat"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = "0.8.13"
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/*Builder*.class",
                    "**/*Builder$*.class",
                    "vyshkat/console/**"
                )
            }
        })
    )
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val sunChecks = configurations.detachedConfiguration(
    dependencies.create("com.puppycrawl.tools:checkstyle:14.1.0")
).apply {
    isTransitive = false
}

checkstyle {
    toolVersion = "14.1.0"
    config = resources.text.fromArchiveEntry(
        sunChecks,
        "sun_checks.xml"
    )
    isShowViolations = true
    isIgnoreFailures = false
    maxWarnings = 0
}

tasks.withType<Checkstyle>().configureEach {
    reports {
        html.required = true
        xml.required = false
    }
}
tasks.named<org.springframework.boot.gradle.tasks.run.BootRun>("bootRun") {
    standardInput = System.`in`
}