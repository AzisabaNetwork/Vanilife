dependencies {
    api(kotlin("stdlib"))
    api(project(":plugins:common"))
    api(libs.entitylib) {
        exclude(group = "com.github.retrooper")
    }
    api(libs.exposed.core)
    api(libs.exposed.jdbc)
    api(libs.exposed.kotlin.datetime)
    api(libs.hikaricp)
    api(libs.kaml)
    api(libs.kotlinx.coroutines.core)
    api(libs.mccoroutine.folia.api)
    api(libs.mccoroutine.folia.core)
    api(libs.minecraftexposed.adventure)
    api(libs.minecraftexposed.paper)
    api(libs.minecraftserialization.adventure)
    api(libs.minecraftserialization.joml)
    api(libs.minecraftserialization.paper)
    api(libs.minecraftserialization.valueproviders)
    api(libs.packed.core)
    api(libs.packed.resource)
    api(libs.packed.server)
    // Provided by the packetevents plugin at runtime; bundling a second copy leaves its API uninitialized
    compileOnlyApi(libs.packetevents)
    api(libs.postgresql)
}
