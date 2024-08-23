package io.github.skydynamic.maiprober.proxy.handler

import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.DivingFishProberUtil
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

val logger: Logger = LoggerFactory.getLogger("MaiProber-InterceptHandler")

object InterceptHandler {
    suspend fun onAuthHook(authUrl: URI, config: ConfigStorage) {
        val urlString = authUrl.toString()
        val target = urlString.replace("http", "https")
        if (config.platform == ProberPlatform.DIVING_FISH) {
            val proberUtil = config.platform.factory
            if (proberUtil.validateProberAccount(config.userName, config.password)) {
                if (target.contains("maimai-dx")) {
                    proberUtil.uploadMaimaiProberData(config.userName, config.password, target)
                } else if (target.contains("chunithm")) {
                    proberUtil.uploadChunithmProberData(config.userName, config.password, target)
                }
            } else {
                logger.error("Prober账号密码错误")
            }
        } else {
            logger.error("暂不支持的Prober平台")
        }
    }
}
