plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.0"

    application
    jacoco
}

repositories {
    jcenter()
    maven("https://kotlin.bintray.com/kotlinx")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.google.guava:guava:31.0.1-jre")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")
    implementation("com.massisframework:j-text-utils:0.3.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
}

application {
    mainClass.set("accountLedgerCli.cli.AppKt")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "accountLedgerCli.cli.AppKt"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }
}

tasks.jacocoTestReport {
    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}
