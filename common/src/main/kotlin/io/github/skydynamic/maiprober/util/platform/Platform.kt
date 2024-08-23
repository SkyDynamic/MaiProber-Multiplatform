package io.github.skydynamic.maiprober.util.platform

interface Platform {
    fun setupSystemProxy(proxyUrl: String, proxyPort:Int)

    fun rollbackSystemProxy()

    fun openWechat()
}