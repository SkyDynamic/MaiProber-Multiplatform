package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.util.ClipDataUtil
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ConfigStorage
import io.github.skydynamic.maiprober.util.prober.ProberPlatform
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.github.skydynamic.maiprober.util.config.Config.configStorage as config


val LOGGER: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")

class MaimaiProberMain {
    private val ctx: ProberContext
    private val instance: Prober

    init {
        Config.read()
        ctx = object :ProberContext {
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
        instance = Prober(ctx)
    }

    fun saveConfig() {
        Config.write()
    }

    fun getConfig(): ConfigStorage {
        return ctx.requireConfig()
    }

    fun start() {
        instance.startProxy()
        instance.join()
    }

    fun stop() {
        LOGGER.info("Stop Proxy Server")
        instance.stopProxy()
    }
}

fun main() {
    LOGGER.info("Initialize config")
    Config.read()
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
    // ScannerUtil.start(ctx, instance)

    LOGGER.info("Initialize http proxy server...")
    instance.startProxy()
    instance.join()
}
