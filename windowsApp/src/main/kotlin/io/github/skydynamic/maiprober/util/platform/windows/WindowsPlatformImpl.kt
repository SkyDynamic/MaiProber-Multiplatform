package io.github.skydynamic.maiprober.util.platform.windows

import com.sun.jna.platform.win32.Advapi32Util
import com.sun.jna.platform.win32.Win32Exception
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.WinReg
import io.github.skydynamic.maiprober.util.platform.Platform

class WindowsPlatformImpl : Platform {
    private var previousProxyUrl: String? = null
    private var previousProxyEnabled = false

    override fun setupSystemProxy(proxyUrl: String) {
        previousProxyEnabled = getOrCreateDWORDValue("ProxyEnable", 0) != 0
        previousProxyUrl = try {
            Advapi32Util.registryGetStringValue(
                WinReg.HKEY_CURRENT_USER,
                KEY_PATH,
                "ProxyServer"
            )
        } catch (e: Win32Exception) {
            null
        }
        setStringValue("ProxyServer", proxyUrl)
        setDWORDValue("ProxyEnable", 1)
        // System.out.println("previousProxyUrl = " + previousProxyUrl);
        // System.out.println("previousProxyEnabled = " + previousProxyEnabled);
    }

    override fun rollbackSystemProxy() {
        setDWORDValue("ProxyEnable", if (previousProxyEnabled) 1 else 0)
        if (previousProxyUrl != null) {
            setStringValue("ProxyServer", previousProxyUrl)
        }
    }

    override fun openWechat() {
        ProcessBuilder().command("cmd", "/c", "start", "weixin://").start()
    }

    companion object {
        private const val DEFAULT_SAM_DESIRED = WinNT.KEY_READ or WinNT.KEY_WRITE
        private const val KEY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings"
        fun getOrCreateDWORDValue(lpKey: String?, dwDefault: Int): Int {
            try {
                return Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey)
            } catch (ex: Win32Exception) {
                Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, dwDefault)
                return dwDefault
            }
        }

        fun setDWORDValue(lpKey: String?, dwDefault: Int) {
            Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, dwDefault)
        }

        fun getOrCreateStringValue(lpKey: String?, lpDefault: String): String {
            try {
                return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey)
            } catch (ex: Win32Exception) {
                Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, lpDefault)
                return lpDefault
            }
        }

        fun deleteValue(lpKey: String?) {
            Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey)
        }

        fun setStringValue(lpKey: String?, lpValue: String?) {
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, lpValue)
        }
    }
}
