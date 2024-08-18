package io.github.skydynamic.maiprober.util.config

import io.github.skydynamic.maiprober.util.OauthTokenUtil
import io.github.skydynamic.maiprober.util.prober.ProberPlatform

@kotlinx.serialization.Serializable
data class ConfigStorage(
    val proxyPort: Int = 2560,
    val platform: ProberPlatform = ProberPlatform.DIVING_FISH,
    val secretKey: String = OauthTokenUtil.generateRandomSecretKey(),
    var userName: String = "",
    var password: String = ""
)
