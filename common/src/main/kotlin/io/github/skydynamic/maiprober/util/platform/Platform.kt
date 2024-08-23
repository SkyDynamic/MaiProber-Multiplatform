package io.github.skydynamic.maiprober.util.platform

interface Platform {
    fun setupSystemProxy(proxyUrl: String)

    fun rollbackSystemProxy()

    fun openWechat()
}