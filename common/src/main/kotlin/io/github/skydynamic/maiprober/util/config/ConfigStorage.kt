package io.github.skydynamic.maiprober.util.config

import io.github.skydynamic.maiprober.util.OauthTokenUtil
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import io.github.skydynamic.maiprober.util.score.MaimaiDan
import kotlinx.serialization.Serializable
import net.mamoe.yamlkt.Comment

@Serializable
data class ConfigStorage(
    @Comment("代理端口")
    val proxyPort: Int = 2560,
    @Comment("查分平台")
    var platform: ProberPlatform = ProberPlatform.DIVING_FISH,
    @Comment("SecretKey, 用于生成 OAuth Token, 请勿随意改动")
    val secretKey: String = OauthTokenUtil.generateRandomSecretKey(),
    @Comment("水鱼查分器用户名")
    var userName: String = "",
    @Comment("水鱼查分器密码")
    var password: String = "",
    @Comment("查分器 Token, 用于获取个人信息 / 游玩记录")
    var token: ProberToken = ProberToken(),
    @Comment("个人信息")
    var personalInfo: ProberUserInfo = ProberUserInfo(),
    @Comment("WindowApp设置")
    var settings: Settings = Settings()
)

@Serializable
data class ProberToken(
    var divingFish: String = "",
    var lxns: String = ""
)

@Serializable
data class ProberUserInfo(
    var name: String = "",
    var maimaiDan: MaimaiDan = MaimaiDan.DAN0,
    var maimaiIcon: Int = 1,
    var maimaiPlate: Int = 1,
    var maimaiTitle: String = ""
)

@Serializable
data class Settings(
    @Comment("使用数据缓存")
    var useCache: Boolean = false,
    @Comment("""
        线性背景颜色色调(可多个, 使用' : '分隔)
        例如: '#d3edfa;#cc9c2'
    """)
    var maimaiB50BackgroundColor: String = "#d3edfa",
    @Comment("""
        线性背景相近颜色过度级别(min = 1, max = 20)
        例如: '12'
    """)
    var maimaiB50BackgroundLinearLayerCount: Int = 12
)