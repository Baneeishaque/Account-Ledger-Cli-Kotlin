plugins {

    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)

    id("application")
    id("jacoco")
}

dependencies {

    implementation(libs.kotlinx.cli)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.coroutines.core)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.logging)
    implementation(libs.logback.classic)

    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)

    implementation(libs.dotenv.kotlin)

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
