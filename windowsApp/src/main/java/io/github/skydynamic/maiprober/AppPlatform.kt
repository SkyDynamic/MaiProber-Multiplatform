package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.util.platform.Platform
import io.github.skydynamic.maiprober.util.platform.macos.MacOSPlatformImpl
import io.github.skydynamic.maiprober.util.platform.windows.WindowsPlatformImpl
import java.lang.management.ManagementFactory

object AppPlatform : Platform {

    private val delegate by lazy {
        val os = ManagementFactory.getOperatingSystemMXBean()
        val osName = os.name
//        println("Running on $osName")
        if (os.name.lowercase().contains("windows")) {
            val plat = WindowsPlatformImpl()
            return@lazy plat
        }
        if (os.name.lowercase().contains("mac")) {
            val plat = MacOSPlatformImpl()
            return@lazy plat
        }
        throw RuntimeException("Unsupported Platform: $osName")
    }
    private var proxySetup = false

    override fun setupSystemProxy(proxyUrl: String) {
        delegate.setupSystemProxy(proxyUrl)
        proxySetup = true
    }

    override fun rollbackSystemProxy() {
        delegate.rollbackSystemProxy()
    }
}