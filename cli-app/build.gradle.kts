import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {

    alias(notation = libs.plugins.kotlin.jvm)
    alias(notation = libs.plugins.kotlin.serialization)

    id(id = "application")
    id(id = "jacoco")
}

dependencies {

    implementation(dependencyNotation = libs.kotlinx.cli)
    implementation(dependencyNotation = libs.kotlinx.serialization.json)
    implementation(dependencyNotation = libs.kotlinx.coroutines.core)

    implementation(dependencyNotation = libs.ktor.client.core)
    implementation(dependencyNotation = libs.ktor.client.cio)
    implementation(dependencyNotation = libs.ktor.client.logging)
    implementation(dependencyNotation = libs.logback.classic)

    implementation(dependencyNotation = libs.ktor.client.auth)
    implementation(dependencyNotation = libs.ktor.client.content.negotiation)
    implementation(dependencyNotation = libs.ktor.serialization.kotlinx.json)

    implementation(dependencyNotation = libs.dotenv.kotlin)

    implementation(dependencyNotation = project(path = ":account-ledger-lib:account-ledger-lib"))
    implementation(dependencyNotation = project(path = ":account-ledger-lib-multi-platform:lib"))
    implementation(dependencyNotation = project(path = ":common-lib:common-lib"))
}

application {

    mainClass.set(/* value = */ "accountLedgerCli.cli.App")
}

testing {

    suites {

        val test: JvmTestSuite by getting(type = JvmTestSuite::class) {

            useKotlinTest()
        }
    }
}

val jar: Jar by tasks.getting(Jar::class) {

//    TODO : Use Include pattern instead of blank exclude strategy
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {

        attributes["Main-Class"] = "accountLedgerCli.cli.App"
    }

    from(
        configurations.runtimeClasspath.get().map { file: File ->

            if (file.isDirectory) {

                file

            } else {

                zipTree(file)
            }
        }
    ) {

        exclude(/* ...excludes = */ "META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }

    dependsOn(/* ...paths = */ ":account-ledger-lib:account-ledger-lib:jar")
    dependsOn(/* ...paths = */ ":common-lib:common-lib:jar")
}

tasks.jacocoTestReport {

    reports {

        xml.required.set(/* value = */ true)
        html.required.set(/* value = */ true)
    }
}

tasks.check {

    dependsOn(/* ...paths = */ tasks.jacocoTestReport)
}

tasks.jacocoTestReport {

    dependsOn(/* ...paths = */ tasks.test)
}

tasks.test {

    finalizedBy(/* ...paths = */ tasks.jacocoTestReport)
}

kotlin {

    compilerOptions {

//        allWarningsAsErrors = true
        verbose = true

        apiVersion = KotlinVersion.KOTLIN_2_2
        languageVersion = KotlinVersion.KOTLIN_2_2

        javaParameters = true
        jvmTarget = JvmTarget.JVM_21
    }

    sourceSets.all {

        languageSettings.apply {

            languageVersion = KotlinVersion.KOTLIN_2_2.version
            apiVersion = KotlinVersion.KOTLIN_2_2.version
        }
    }
}
