package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.proxy.ProxyServer
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.prober.WechatRequestUtil
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")

fun main() = runBlocking {
    val config = Config.INSTANCE
    config.load()

    LOGGER.info("Initialize http proxy server...")
    val server = ProxyServer(config.proxyPort)
    server.start()
    server.join()

    LOGGER.info("1")
}
