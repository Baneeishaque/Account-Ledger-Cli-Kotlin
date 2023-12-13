plugins {

    // TODO : Use BoM
    val kotlinVersion = "2.0.0-test-888"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}
