package io.github.skydynamic.maiprober.proxy.handler

import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

val logger: Logger = LoggerFactory.getLogger("MaiProber-InterceptHandler")

object InterceptHandler {
    val config:ConfigStorage by Config

    suspend fun onAuthHook(authUrl: URI, config: ConfigStorage) {
        val urlString = authUrl.toString()
        val target = urlString.replace("http", "https")
        val proberUtil = config.platform.factory
        if (config.platform == ProberPlatform.DIVING_FISH) {
            if (proberUtil.validateProberAccount(config.userName, config.password)) {
                if (target.contains("maimai-dx")) {
                    proberUtil.uploadMaimaiProberData(
                        config.userName, config.password, target, config.settings.useCache)
                } else if (target.contains("chunithm")) {
                    proberUtil.uploadChunithmProberData(
                        config.userName, config.password, target, config.settings.useCache)
                }
            } else {
                logger.error("Prober账号密码错误")
            }
        } else if (config.platform == ProberPlatform.LXNS){
            if (target.contains("maimai-dx")) {
                proberUtil.uploadMaimaiProberData(config.token.lxns, target, config.settings.useCache)
            } else if (target.contains("chunithm")) {
                proberUtil.uploadChunithmProberData(config.token.lxns, target, config.settings.useCache)
            }
        } else {
            logger.error("暂不支持的Prober平台")
        }
    }
}
