plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.8"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.hibernate.orm") version "6.6.36.Final"
    id("org.graalvm.buildtools.native") version "0.10.6"
    kotlin("plugin.jpa") version "1.9.25"
    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
    kotlin("kapt") version "1.9.25"
}

group = "me.inory"
version = "0.0.1-SNAPSHOT"
description = "rainyadmin-handmade"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}
configurations {
    all {
        exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
    }
}
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    runtimeOnly("com.mysql:mysql-connector-j")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("cn.dev33:sa-token-spring-boot3-starter:1.44.0")
    implementation("cn.hutool:hutool-all:5.8.10")
    // https://mvnrepository.com/artifact/io.github.openfeign.querydsl/querydsl-jpa
    implementation("io.github.openfeign.querydsl:querydsl-jpa:7.1")
    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:7.1")
    implementation("org.mapstruct:mapstruct:1.6.3")
    kapt("org.mapstruct:mapstruct-processor:1.6.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        freeCompilerArgs.addAll("-Xjvm-default=all")
    }
}

hibernate {
    enhancement {
        enableAssociationManagement = true
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
springBoot {
    mainClass = "me.inory.rainyadmin.RainyAdminApplicationKt"
}
kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "ignore")
    }
}