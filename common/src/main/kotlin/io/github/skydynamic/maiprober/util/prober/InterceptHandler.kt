package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.URI

val logger: Logger = LoggerFactory.getLogger("MaiProber-InterceptHandler")

class InterceptHandler {
    companion object {
        @JvmStatic
        suspend fun onAuthHook(authUrl: URI) {
            val urlString = authUrl.toString()
            val target = urlString.replace("http", "https")
            if (Config.INSTANCE.platform == "diving-fish") {
                val proberUtil = DivingFishProberUtil()
                if (proberUtil.verifyProberAccount(Config.INSTANCE.userName, Config.INSTANCE.password)) {
                    proberUtil.uploadMaimaiProberData(Config.INSTANCE.userName, Config.INSTANCE.password, target)
                } else {
                    logger.error("Prober账号密码错误")
                }
            } else {
                logger.error("暂不支持的Prober平台")
            }
        }
    }
}
