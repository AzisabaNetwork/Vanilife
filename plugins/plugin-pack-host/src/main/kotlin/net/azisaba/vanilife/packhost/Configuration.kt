package net.azisaba.vanilife.packhost

import kotlinx.serialization.Serializable

@Serializable
internal data class Configuration(
    val port: Int = 8085,
    // URL that clients use to download the pack; must be reachable from outside the cluster
    val publicUrl: String = "http://localhost:8085",
)
