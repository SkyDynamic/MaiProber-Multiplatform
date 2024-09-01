package io.github.skydynamic.maiprober.util.config

import io.github.skydynamic.maiprober.util.OauthTokenUtil
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import kotlinx.serialization.Serializable
import net.mamoe.yamlkt.Comment

@Serializable
data class ConfigStorage(
    @Comment("代理端口")
    val proxyPort: Int = 2560,
    @Comment("查分平台 (未来这个设置将会启用)")
    val platform: ProberPlatform = ProberPlatform.DIVING_FISH,
    @Comment("SecretKey, 用于生成 OAuth Token, 请勿随意改动")
    val secretKey: String = OauthTokenUtil.generateRandomSecretKey(),
    @Comment("水鱼查分器用户名")
    var userName: String = "",
    @Comment("水鱼查分器密码")
    var password: String = "",
    @Comment("WindowApp设置")
    var settings: Settings = Settings()
)

@Serializable
data class Settings(
    @Comment("使用数据缓存")
    var useCache: Boolean = false
)