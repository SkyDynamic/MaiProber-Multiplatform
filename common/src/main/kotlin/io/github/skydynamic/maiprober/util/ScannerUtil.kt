package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.Prober
import io.github.skydynamic.maiprober.util.prober.WechatRequestUtil
import kotlinx.coroutines.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.system.exitProcess

val logger: Logger = LoggerFactory.getLogger("ConsoleScanner")

class ScannerUtil {
    companion object {
        private val scanner = Scanner(System.`in`)
        private var shouldStop = false // 添加一个标志来控制循环的退出

        @JvmStatic
        fun start(instance: Prober) {
            CoroutineScope(Dispatchers.Default).launch {
                while (!shouldStop) {
                    val input = scanner.nextLine() // 每次循环都读取新的输入
                    val parts = input.split(" ")

                    when (parts.firstOrNull()) {
                        "geturl" -> {
                            handleGetUrlCommand(parts)
                        }
                        "stop" -> {
                            logger.info("Stopping server...")
                            shouldStop = true // 设置标志为 true 来退出循环
                        }
                        else -> logger.info("Unknown command: '$input'")
                    }
                }
                instance.stopProxy()
                scanner.close()
                exitProcess(0)
            }
        }

        @JvmStatic
        private suspend fun handleGetUrlCommand(parts: List<String>) {
            if (parts.size < 2) {
                println("Usage: geturl <type>")
                return
            }
            val type = parts[1]
            if (listOf("maimai-dx", "chunithm").contains(type)) {
                val url = withContext(Dispatchers.IO) {
                    WechatRequestUtil.getAuthUrl(type)
                }
                logger.info("oauthURL: $url")
                ClipDataUtil.copyToClipboard(url)
                logger.info("已复制到剪切板, 请尽快复制到微信打开")
            }
        }
    }
}
