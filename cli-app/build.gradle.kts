//TODO : Use Ktor BoM
val ktorVersion: String = "2.1.2"

plugins {

    kotlin("jvm")
    kotlin("plugin.serialization")

    id("application")
    id("jacoco")
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.14")

    implementation("io.ktor:ktor-client-auth:$ktorVersion")

    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    implementation(project(":account-ledger-lib-multi-platform:lib"))
    implementation(project(":account-ledger-lib:account-ledger-lib"))
    implementation(project(":common-lib:common-lib"))
}

application {
    mainClass.set("accountLedgerCli.cli.App")
}

val jar by tasks.getting(Jar::class) {

//    TODO : Use Include pattern instead of blank exclude strategy
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = "accountLedgerCli.cli.App"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }) {
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }

    dependsOn(":account-ledger-lib:account-ledger-lib:jar")
    dependsOn(":common-lib:common-lib:jar")
}

tasks.jacocoTestReport {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}
