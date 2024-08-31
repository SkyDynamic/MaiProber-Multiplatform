package io.github.skydynamic.maiprober

import io.github.skydynamic.maiprober.util.platform.Platform
import io.github.skydynamic.maiprober.util.platform.macos.MacOSPlatformImpl
import io.github.skydynamic.maiprober.util.platform.windows.WindowsPlatformImpl
import java.lang.management.ManagementFactory

object AppPlatform : Platform {

    private val delegate= run {
        val os = ManagementFactory.getOperatingSystemMXBean()
        val osName = os.name
        if (os.name.lowercase().contains("windows")) {
            val plat = WindowsPlatformImpl()
            return@run plat
        }
        if (os.name.lowercase().contains("mac")) {
            val plat = MacOSPlatformImpl()
            return@run plat
        }
        throw RuntimeException("Unsupported Platform: $osName")
    }
    private var proxySetup = false

    override fun setupSystemProxy(proxyUrl: String, proxyPort: Int) {
        delegate.setupSystemProxy(proxyUrl, proxyPort)
        proxySetup = true
    }

    override fun rollbackSystemProxy() {
        delegate.rollbackSystemProxy()
    }

    override fun openWechat() {
        delegate.openWechat()
    }
}