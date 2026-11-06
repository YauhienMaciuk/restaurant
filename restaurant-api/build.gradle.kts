plugins {
    `java-library`
}

group = "com.ingroup"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api("jakarta.validation:jakarta.validation-api:3.0.2")

    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.hibernate.validator:hibernate-validator:8.0.6.Final")
    testRuntimeOnly("org.glassfish.expressly:expressly:5.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
