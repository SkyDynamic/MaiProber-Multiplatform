package io.github.skydynamic.maiprober.util.platform.macos

import io.github.skydynamic.maiprober.util.platform.Platform
import io.github.skydynamic.maiprober.util.runCommand
import io.github.skydynamic.maiprober.util.runCommandForResult
import java.util.regex.Pattern

class MacOSPlatformImpl : Platform {
    private val interfaceServicePattern = Pattern.compile("Hardware Port: (.+)\\nDevice: (.+)\\n")
    private val proxyStatusPattern = Pattern.compile("Enabled: (Yes|No)")
    private val serverPortPattern = Pattern.compile("Server: (.+)\\nPort: (\\d+)")
    private val interfacePattern = Pattern.compile("Network interfaces: (.+)")

    private var previousProxyEnabled = false
    private var previousProxyServer: String? = null
    private var previousProxyPort: String? = null

    private fun findActiveService(): String? {
        val activeInterface = findActiveInterface() ?: return null
        val interfaces = buildInterface2ServiceMap()
        val activeService = interfaces[activeInterface] ?: return null
        return activeService
    }

    override fun setupSystemProxy(proxyUrl: String, proxyPort: Int) {
        val activeService = findActiveService() ?: throw IllegalStateException("No active service are present")
        val proxyStatusResult = runCommandForResult("/usr/sbin/networksetup", "-getwebproxy", activeService)
        proxyStatusPattern.toRegex().find(proxyStatusResult)?.run {
            try {
                previousProxyEnabled = this.groupValues[1] == "Yes"
            } catch (_: IndexOutOfBoundsException) {
            }
        }
        serverPortPattern.toRegex().find(proxyStatusResult)?.run {
            try {
                previousProxyServer = this.groupValues[1]
                previousProxyPort = this.groupValues[2]
            } catch (_: IndexOutOfBoundsException) {
            }
        }
        updateWebProxyState(activeService, true)
        updateWebProxy(activeService, proxyUrl, proxyPort.toString())
    }

    private fun updateWebProxyState(service: String, enabled: Boolean) {
        runCommand("/usr/sbin/networksetup", "-setwebproxystate", service, if (enabled) "on" else "off")
    }

    private fun updateWebProxy(service: String, proxyServer: String, proxyPort: String) {
        runCommand("/usr/sbin/networksetup", "-setwebproxy", service, proxyServer, proxyPort)
    }

    override fun rollbackSystemProxy() {
        val activeService = findActiveService() ?: throw IllegalStateException("No active service are present")
        updateWebProxyState(activeService, previousProxyEnabled)
        if (previousProxyPort != null && previousProxyServer != null) {
            updateWebProxy(activeService, previousProxyServer!!, previousProxyPort!!)
        }
    }

    private fun buildInterface2ServiceMap(): Map<String, String> {
        val result = runCommandForResult("/usr/sbin/networksetup", "-listallhardwareports")
        return buildMap {
            var match = interfaceServicePattern.toRegex().find(result)
            while (match != null) {
                try {
                    this[match.groupValues[2]] = match.groupValues[1]
                } catch (_: IndexOutOfBoundsException) {
                }
                match = match.next()
            }
        }
    }

    private fun findActiveInterface(): String? {
        val result = runCommandForResult("/usr/sbin/scutil", "--nwi")
        return interfacePattern.toRegex().find(result)?.groupValues?.get(1)
    }

    override fun openWechat() {
        runCommand("open", "weixin://")
    }
}