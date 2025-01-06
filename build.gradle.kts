plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

version = "1.0.0"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    annotationProcessor(libs.lombok)
    compileOnly(libs.lombok)
    implementation(libs.aws.sdk.dynamodb)
    implementation(libs.bundles.logging)

    testAnnotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.jupiter.params)
    testImplementation(libs.assertj.core)
    testImplementation(libs.resilience4j.retry)
    testRuntimeOnly(libs.junit.jupiter.engine)
}

tasks.withType<Test> {
    outputs.upToDateWhen { false }

    useJUnitPlatform()

    testLogging {
        showStandardStreams = true
    }
}
