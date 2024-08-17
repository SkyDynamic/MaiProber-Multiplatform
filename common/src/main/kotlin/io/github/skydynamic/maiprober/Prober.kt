package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.proxy.ProxyServer
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import kotlinx.coroutines.runBlocking

class Prober(private val context:ProberContext) {

    private val config = context.readConfig()
    private var proxyServer = ProxyServer(config.proxyPort)
    private lateinit var prob:ProberUtil

    fun validateAccount():Boolean {
        prob = config.platform.factory()
        return runBlocking{
            return@runBlocking prob.validateProberAccount(config.username, config.password)
        }
    }

    fun stopProxy() {
        proxyServer.interrupt()
        proxyServer.join()
        proxyServer = ProxyServer(config.proxyPort)
    }

    fun startProxy(): Thread {
        proxyServer.start()
        return proxyServer
    }

    fun join() {
        proxyServer.join()
    }
}