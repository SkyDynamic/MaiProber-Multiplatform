package io.github.skydynamic.maiprober.util

import io.github.skydynamic.maiprober.Prober
import io.github.skydynamic.maiprober.ProberContext
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.prober.divingfish.DivingFishProberUtil
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
        fun start(ctx: ProberContext, instance: Prober) {
            CoroutineScope(Dispatchers.Default).launch {
                while (!shouldStop) {
                    val input = scanner.nextLine() // 每次循环都读取新的输入
                    val parts = input.split(" ")

                    when (parts.firstOrNull()) {
                        "geturl" -> {
                            handleGetUrlCommand(ctx, parts)
                        }
                        "login" -> {
                            handleLoginCommand(ctx, parts)
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
        private fun handleLoginCommand(ctx: ProberContext, parts: List<String>) {
            if (parts.size < 3) {
                println("Usage: login <username> <password>")
                return
            }

            val userName = parts[1]
            val password = parts[2]
            runBlocking {
                if (DivingFishProberUtil().validateProberAccount(userName, password)) {
                    logger.info("登录成功")
                    Config.configStorage.userName = userName
                    Config.configStorage.password = password
                    Config.write()
                    Config.read()
                } else {
                    logger.info("登录失败")
                }
            }
        }

        @JvmStatic
        private fun handleGetUrlCommand(ctx: ProberContext, parts: List<String>) {
            val config = ctx.requireConfig()

            if (parts.size < 2) {
                println("Usage: geturl <type>")
                return
            }
            val type = parts[1]
            if (listOf("maimai-dx", "chunithm").contains(type)) {
                val token = OauthTokenUtil.generateToken(config.userName, config.secretKey, 60 * 15)
                val url = "http://127.0.0.1:${config.proxyPort}/oauth/$type?token=$token"
                logger.info("oauthURL: $url")
                ClipDataUtil.copyToClipboard(url)
                logger.info("已复制到剪切板, 请尽快复制到微信打开")
            }
        }
    }
}
