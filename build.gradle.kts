import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
    kotlin("plugin.allopen") version "1.3.71"
    kotlin("plugin.noarg") version "1.3.71"
    kotlin("kapt") version "1.6.0" // QueryDsl
}

group = "dev.board"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val snippetsDir by extra { file("build/generated-snippets") }

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.sentry:sentry-spring-boot-starter:6.16.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("aws.sdk.kotlin:s3:0.16.0")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("mysql:mysql-connector-java")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

noArg {
    annotation("javax.persistence.Entity")
    annotation("javax.persistence.MappedSuperclass")
    annotation("javax.persistence.Embeddable")
}

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.register("copyYml", Copy::class) {
    copy {
        from("./backend-config")
        include("*.yml", "*.xml")
        into("src/main/resources")
    }
}

tasks.asciidoctor {
    dependsOn(tasks.getByName("copyYml"))
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
    doFirst { // 2
        delete("src/main/resources/static/docs")
        delete("BOOT-INF/classes/static/docs")
    }
}

tasks.register("copyHTML", Copy::class) { // 3
    dependsOn(tasks.asciidoctor)
    destinationDir = file(".")
    from(tasks.asciidoctor.get().outputDir) {
        into("src/main/resources/static/docs")
    }
}

tasks.bootRun {
    dependsOn(tasks.getByName("copyYml"))
}

tasks.build {
    dependsOn(tasks.getByName("copyYml"))
    dependsOn(tasks.getByName("copyHTML"))
}

tasks.bootJar { // 5
    dependsOn(tasks.asciidoctor)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    from(tasks.asciidoctor.get().outputDir) {
        into("BOOT-INF/classes/static/docs")
    }
}

val jar: Jar by tasks

jar.enabled = false
