package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.proxy.ProxyServer
import io.github.skydynamic.maiprober.util.config.Config
import org.slf4j.Logger
import org.slf4j.LoggerFactory

val LOGGER: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")

fun main() {
    val config = Config.INSTANCE
    config.load()

    LOGGER.info("Initialize http proxy server...")
    val server = ProxyServer(config.proxyPort)
    server.start()
    server.join()
}
