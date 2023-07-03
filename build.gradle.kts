plugins {

    // TODO : Use BoM
    // val kotlinVersion = "1.9.20-mercury-653"
    val kotlinVersion = "1.9.20-station-601"
    kotlin("jvm") version kotlinVersion apply false
    kotlin("plugin.serialization") version kotlinVersion apply false
}
