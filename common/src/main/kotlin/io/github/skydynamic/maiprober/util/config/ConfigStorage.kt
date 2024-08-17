package io.github.skydynamic.maiprober.util.config

import io.github.skydynamic.maiprober.util.prober.ProberPlatform

@kotlinx.serialization.Serializable
data class ConfigStorage(
    val webPort : Int = 8081,
    val proxyPort: Int = 2560,
    val useSsl: Boolean = false,
    val platform: ProberPlatform = ProberPlatform.DIVING_FISH,
    val userName: String = "",
    val password: String = ""
)
