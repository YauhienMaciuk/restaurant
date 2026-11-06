plugins {
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.ingroup"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

// Boot 3.5.16 manages log4j 2.24.3 (GHSA-qv9r-c865-cp47, fixed in 2.25.5)
extra["log4j2.version"] = property("log4j2Version")
// Boot 3.5.16 manages tomcat 10.1.55 and jackson 2.21.4, which have known advisories
extra["tomcat.version"] = property("tomcatVersion")
extra["jackson-bom.version"] = property("jacksonVersion")

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":restaurant-api"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.bootJar {
    archiveFileName = "restaurant.jar"
}
