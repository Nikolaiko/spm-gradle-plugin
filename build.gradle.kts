plugins {
    `kotlin-dsl`.version("2.3.3")
    id("java-gradle-plugin")
    id("org.gradle.maven-publish")

    kotlin("plugin.serialization") version "1.6.21"
}

group = "com.nikolai"
version = "1.0.0"

repositories {
    mavenCentral()
    google()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

gradlePlugin {
    plugins {
        create("iosSPMPlugin") {
            id = "org.nikolai.multiplatform.spm"
            displayName = "iosSPMPlugin"
            description = "Plugin to add Swift Packages to multiplatform projects"
            implementationClass = "gradle.multiplatform.spm.MultiplatformSPMPlugin"
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    //Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")

    //KGit
    implementation("com.github.sya-ri:kgit:1.0.5")

    //Test
    testImplementation(platform("org.junit:junit-bom:5.9.0-M1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(gradleTestKit())
}

tasks.test {
    useJUnitPlatform()
}