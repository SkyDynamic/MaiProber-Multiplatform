package io.github.skydynamic.maiprober

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object Constant {
    val gson: Gson = GsonBuilder().serializeNulls().setPrettyPrinting().create()
    val WX_WINDOWS_UA = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/81.0.4044.138 Safari/537.36 NetType/WIFI " +
            "MicroMessenger/7.0.20.1781(0x6700143B) WindowsWechat(0x6307001e)"
    val PROXY_WHITELIST: List<String> = listOf(
        "127.0.0.1",
        "localhost",
        "tgk-wcaime.wahlap.com",
        "maimai.wahlap.com",
        "chunithm.wahlap.com",
        "maimai.bakapiano.com",
        "www.diving-fish.com",
        "open.weixin.qq.com",
        "weixin110.qq.com",
        "res.wx.qq.com",
        "libs.baidu.com",
        "maibot.bakapiano.com"
    )
}
