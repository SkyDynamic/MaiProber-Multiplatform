package io.github.skydynamic.maiprober.util.platform.windows;

import com.sun.jna.platform.win32.*;
import io.github.skydynamic.maiprober.util.platform.Platform;
import org.jetbrains.annotations.NotNull;

public class WindowsPlatformImpl implements Platform {
    private static final int DEFAULT_SAM_DESIRED = WinNT.KEY_READ | WinNT.KEY_WRITE;
    private static final String KEY_PATH = "Software\\Microsoft\\Windows\\CurrentVersion\\Internet Settings";
    private String previousProxyUrl;
    private boolean previousProxyEnabled = false;

    public WindowsPlatformImpl() {

    }

    public static int getOrCreateDWORDValue(String lpKey, int dwDefault) {
        try {
            return Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey);
        } catch (Win32Exception ex) {
            Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, dwDefault);
            return dwDefault;
        }
    }

    public static void setDWORDValue(String lpKey, int dwDefault) {
        Advapi32Util.registrySetIntValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, dwDefault);
    }

    public static String getOrCreateStringValue(String lpKey, String lpDefault) {
        try {
            return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey);
        } catch (Win32Exception ex) {
            Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, lpDefault);
            return lpDefault;
        }
    }

    public static void deleteValue(String lpKey) {
        Advapi32Util.registryDeleteValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey);
    }

    public static void setStringValue(String lpKey, String lpValue) {
        Advapi32Util.registrySetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, lpKey, lpValue);
    }

    @Override
    public void setupSystemProxy(@NotNull String proxyUrl) {
        previousProxyEnabled = getOrCreateDWORDValue("ProxyEnable", 0) != 0;
        try {
            previousProxyUrl = Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, KEY_PATH, "ProxyServer");
        } catch (Win32Exception e) {
            previousProxyUrl = null;
        }
        setStringValue("ProxyServer", proxyUrl);
        setDWORDValue("ProxyEnable", 1);
        System.out.println("previousProxyUrl = " + previousProxyUrl);
        System.out.println("previousProxyEnabled = " + previousProxyEnabled);
    }

    @Override
    public void rollbackSystemProxy() {
        setDWORDValue("ProxyEnable", previousProxyEnabled ? 1 : 0);
        if (previousProxyUrl != null) {
            setStringValue("ProxyServer", previousProxyUrl);
        }
    }
}
