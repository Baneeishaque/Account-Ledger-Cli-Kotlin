plugins {

    // TODO : Use BoM
    val kotlinVersion = "2.0.0-Beta1-16"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("multiplatform") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}
