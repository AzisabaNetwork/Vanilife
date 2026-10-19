package net.azisaba.vanilife.packhost

import kotlinx.serialization.Serializable

@Serializable
internal data class Configuration(
    val port: Int = 8085,
    val publicUrl: String = "http://localhost:8085",
)
