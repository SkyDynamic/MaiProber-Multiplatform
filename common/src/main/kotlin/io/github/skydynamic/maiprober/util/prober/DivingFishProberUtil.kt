package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.Constant
import io.github.skydynamic.maiprober.util.prober.interfact.IProberUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

suspend fun delayRandomTime(diff: Int) {
    val duration = 1000L * (diff + 1) + 1000L * 5 * Random.nextDouble()
    withContext(Dispatchers.IO) {
        delay(duration.toLong())
    }
}

class DivingFishProberUtil : IProberUtil {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    private val logger: Logger = LoggerFactory.getLogger("MaiProber-DivingFish")

    @Serializable
    data class LoginResponse(val errcode: Int? = null, val message: String)

    override suspend fun verifyProberAccount(username: String, password: String) : Boolean {
        val resp: HttpResponse = client.post("https://www.diving-fish.com/api/maimaidxprober/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json;charset=UTF-8")
                append(HttpHeaders.Referrer, "https://www.diving-fish.com/maimaidx/prober/")
                append(HttpHeaders.Origin, "https://www.diving-fish.com")
            }
            contentType(ContentType.Application.Json)
            setBody("""{"username":"$username","password":"$password"}""")
        }
        val body: LoginResponse = resp.body()
        return body.errcode == null
    }

    override suspend fun uploadMaimaiProberData(
        username: String,
        password: String,
        authUrl: String
    ) {
        logger.info("开始更新Maimai成绩")

        logger.info("登录公众号")
        client.get(authUrl) {
            headers {
                append(HttpHeaders.Host, "tgk-wcaime.wahlap.com")
                append(HttpHeaders.Connection, "keep-alive")
                append("Upgrade-Insecure-Requests", "1")
                append(HttpHeaders.UserAgent, Constant.WX_WINDOWS_UA)
                append(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9," +
                        "image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                append("Sec-Fetch-Site", "none")
                append("Sec-Fetch-Mode", "navigate")
                append("Sec-Fetch-User", "?1")
                append("Sec-Fetch-Dest", "document")
                append(HttpHeaders.AcceptEncoding, "gzip, deflate, br")
                append(HttpHeaders.AcceptLanguage, "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7")
            }
        }

        val result = client.get("https://maimai.wahlap.com/maimai-mobile/home/")

        if (result.bodyAsText().contains("错误")) {
            throw RuntimeException("登录公众号失败")
        }

        val diffNameList = listOf(
            "Basic",
            "Advanced",
            "Expert",
            "Master",
            "Re:Master"
        )

        var diff = 0
        for (diffName in diffNameList) {
            logger.info("获取 $diffName 难度成绩数据")
            delayRandomTime(diff)

            val scoreResp: HttpResponse = client.get(
                "https://maimai.wahlap.com/maimai-mobile/record/musicGenre/search/?genre=99&diff=$diff"
            )
            val body = scoreResp.bodyAsText()

            val data = Regex("<html.*>([\\s\\S]*)</html>")
                .find(body)?.groupValues?.get(1)?.replace("\\s+/g", " ")

            logger.info("上传 $diffName 难度成绩到 Diving-Fish 查分器数据库")

            val resp: HttpResponse = client.post("https://www.diving-fish.com/api/pageparser/page") {
                headers {
                    append(HttpHeaders.ContentType, "text/plain")
                }
                contentType(ContentType.Text.Plain)
                setBody("""<login><u>$username</u><p>$password</p></login>$data""")
            }
            val respData: String = resp.bodyAsText()

            logger.info("Diving-Fish 上传 $diffName 分数接口返回信息: $respData")
            diff += 1
        }
        logger.info("Maimai 成绩上传到 Diving-Fish 查分器数据库完毕")
    }
}
