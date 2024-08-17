package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.util.ClipDataUtil
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.system.exitProcess
import io.github.skydynamic.maiprober.util.config.Config.configStorage as config


val LOGGER: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")

fun main() {
    Config.read()
    LOGGER.info("Initialize http proxy server...")
    val ctx = object :ProberContext{
        override fun sendNotification(title: String, content: String) {
            LOGGER.info(content)
        }

        override fun requireConfig(): ConfigStorage {
            return config
        }

        override fun pasteToClipboard(content: String) {
            ClipDataUtil.copyToClipboard(content)
        }

        override fun getProberPlatform(): ProberPlatform {
            return config.platform
        }

    }
    val instance = Prober(ctx)
    if(!instance.validateAccount()){
        LOGGER.error("无效的登录凭据")
        exitProcess(1)
    }
    instance.startProxy()
    instance.join()
}
