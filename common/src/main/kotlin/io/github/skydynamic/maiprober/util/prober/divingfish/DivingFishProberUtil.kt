package io.github.skydynamic.maiprober.util.prober.divingfish

import io.github.skydynamic.maiprober.Constant
import io.github.skydynamic.maiprober.util.ParseScorePageUtil
import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.config.ProberUserInfo
import io.github.skydynamic.maiprober.util.prober.LoginData
import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import io.github.skydynamic.maiprober.util.score.ChunithmMusicDetailList
import io.github.skydynamic.maiprober.util.score.MaimaiDan
import io.github.skydynamic.maiprober.util.score.MaimaiDifficulty
import io.github.skydynamic.maiprober.util.score.MaimaiMusicDetailList
import io.github.skydynamic.maiprober.util.singal.MaiproberSignal
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.random.Random

suspend fun delayRandomTime(diff: Int) {
    val duration = 500L * (diff + 1) + 500L * 2 * Random.nextDouble()
    withContext(Dispatchers.IO) {
        delay(duration.toLong())
    }
}

val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30000
        connectTimeoutMillis = 30000
    }
    install(HttpCookies) {
        storage = AcceptAllCookiesStorage()
    }
}

class DivingFishProberUtil : ProberUtil {
    private val loginUrl = "https://www.diving-fish.com/api/maimaidxprober/login"

    private val logger: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")
    private lateinit var jwtToken: Cookie

    @Serializable
    data class LoginResponse(val errcode: Int? = null, val message: String)

    @Serializable
    data class PlayerProfile(
        val username: String,
        val nickname: String,
        @SerialName("additional_rating") val additionalRating: Int,
        @SerialName("bind_qq") val bindQQ: Int,
        @SerialName("qq_channel_uid") val qqChannelUid: String,
        val privacy: Boolean,
        val mask: Boolean,
        @SerialName("accept_agreement") val acceptAgreement: Boolean,
        val plate: String,
        @SerialName("user_general_data") val userGeneralData: String? = null,
        @SerialName("import_token") val importToken: String,
    )

    private val updateStartedSignalBuilder = MaiproberSignal<String>()
    private val updateFinishedSignalBuilder = MaiproberSignal<String>()

    override val updateStartedSignal
        get() = updateStartedSignalBuilder
    override val updateFinishedSignal
        get() = updateFinishedSignalBuilder

    override suspend fun validateProberAccount(username: String, password: String): Boolean {
        val loginResp = client.post(loginUrl) {
            contentType(ContentType.Application.Json)
            setBody(LoginData(username, password))
        }
        val response = loginResp.body<LoginResponse>()
        if (loginResp.status != HttpStatusCode.OK || response.errcode != null) {
            return false
        }

        val cookies = loginResp.setCookie()
        jwtToken = cookies.find { it.name == "jwt_token" } ?: return false
        return true
    }

    override suspend fun updateAccountInfo(username: String, password: String) {
        if (validateProberAccount(username, password)) {
            val resp = client.get("https://www.diving-fish.com/api/maimaidxprober/player/profile")
            val data: PlayerProfile = resp.body()
            Config.configStorage.personalInfo = ProberUserInfo(
                name = data.nickname,
                maimaiDan = MaimaiDan.getDanByIndex(data.additionalRating)
            )
            Config.write()
        }
    }

    override suspend fun uploadMaimaiProberData(
        username: String,
        password: String,
        authUrl: String,
        isCache: Boolean
    ) {
        logger.info("开始更新Maimai成绩")

        val musicDetailList = MaimaiMusicDetailList(timestamp = System.currentTimeMillis())

        updateStartedSignal.emit("开始更新Maimai成绩")

        logger.info("登录MaimaiDX主页...")
        client.get(authUrl) {
            getDefaultWahlapRequestBuilder()
        }

        val result = client.get("https://maimai.wahlap.com/maimai-mobile/home/")

        if (result.bodyAsText().contains("错误")) {
            throw RuntimeException("登录公众号失败")
        }

        for (diff in MaimaiDifficulty.entries) {
            logger.info("获取 Maimai-DX ${diff.diffName} 难度成绩数据")

            with(client) {
                val scoreResp: HttpResponse = get(
                    "https://maimai.wahlap.com/maimai-mobile/record/" +
                            "musicGenre/search/?genre=99&diff=${diff.diffIndex}"
                )
                val body = scoreResp.bodyAsText()

                val data = Regex("<html.*>([\\s\\S]*)</html>")
                    .find(body)?.groupValues?.get(1)?.replace("\\s+/g", " ")

                logger.info("上传 Maimai-DX ${diff.diffName} 难度成绩到 Diving-Fish 查分器数据库")

                if (Config.settings.useCache) {
                    val parseResult = ParseScorePageUtil.parseMaimai(data ?: "", diff)
                    musicDetailList.songs.addAll(parseResult)
                }

                val resp: HttpResponse = post("https://www.diving-fish.com/api/pageparser/page") {
                    headers {
                        append(HttpHeaders.ContentType, "text/plain")
                    }
                    contentType(ContentType.Text.Plain)
                    setBody("""<login><u>$username</u><p>$password</p></login>$data""")
                }
                val respData: String = resp.bodyAsText()

                logger.info("Diving-Fish 上传 Maimai-DX ${diff.diffName} 分数接口返回信息: $respData")
                delayRandomTime(diff.diffIndex)
            }
        }
        logger.info("Maimai 成绩上传到 Diving-Fish 查分器数据库完毕")
        if (Config.settings.useCache) {
            musicDetailList.save()
        }
        updateFinishedSignal.emit("Maimai 成绩上传到 Diving-Fish 查分器数据库完毕")
    }

    override suspend fun uploadChunithmProberData(
        username: String,
        password: String,
        authUrl: String,
        isCache: Boolean
    ) {
        logger.info("开始更新Chunithm成绩")

        val musicDetailList = ChunithmMusicDetailList()

        updateStartedSignalBuilder.emit("开始更新Chunithm成绩")

        logger.info("登录Chunithm主页...")
        val result = client.get(authUrl) {
            getDefaultWahlapRequestBuilder()
        }
        if (result.bodyAsText().contains("错误")) {
            throw RuntimeException("登录公众号失败")
        }

        val urls = listOf(
            listOf("record/musicGenre/sendBasic", "record/musicGenre/basic"),
            listOf("record/musicGenre/sendAdvanced", "record/musicGenre/advanced"),
            listOf("record/musicGenre/sendExpert", "record/musicGenre/expert"),
            listOf("record/musicGenre/sendMaster", "record/musicGenre/master"),
            listOf("record/musicGenre/sendUltima", "record/musicGenre/ultima"),
            listOf(null, "record/worldsEndList/"),
            listOf(null, "home/playerData/ratingDetailRecent/")
        )

        val diffNameList = listOf(
            "Basic",
            "Advanced",
            "Expert",
            "Master",
            "Ultima",
            "WorldsEnd",
            "Recent",
        )

        val token = result.setCookie()["_t"]?.value

        var diff = 0
        for (diffName in diffNameList) {
            val url = urls[diff]

            logger.info("获取 Chunithm $diffName 难度成绩数据")
            delayRandomTime(diff)

            with (client) {
                if (url[0] != null) {
                    post("https://chunithm.wahlap.com/mobile/${url[0]}") {
                        headers {
                            append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                        }
                        contentType((ContentType.Application.FormUrlEncoded))
                        setBody("genre=99&token=$token")
                    }
                }

                val resp: HttpResponse = get("https://chunithm.wahlap.com/mobile/${url[1]}")

                logger.info("上传 Chunithm $diffName 难度成绩到 Diving-Fish 查分器数据库")

                val uploadResp: HttpResponse = post("https://www.diving-fish.com/api/chunithmprober/player" +
                        "/update_records_html" + (if (url[1]?.contains("Recent") == true) "?recent=1" else "")
                ) {
                    headers {
                        append(HttpHeaders.ContentType, "text/plain")
                    }
                    contentType(ContentType.Text.Plain)
                    setBody(resp.bodyAsText())
                }
                val respData: String = uploadResp.bodyAsText()

                logger.info("Diving-Fish 上传 Chunithm $diffName 分数接口返回信息: $respData")
            }
            diff += 1
        }
        logger.info("Chunithm 成绩上传到 Diving-Fish 查分器数据库完毕")
        updateFinishedSignal.emit("Maimai 成绩上传到 Diving-Fish 查分器数据库完毕")
    }

    companion object {
        @JvmStatic
        suspend fun getChunithmMusicData(): List<DivingFishChunithmMusicData> {
            val resp = client.get("https://www.diving-fish.com/api/maimaidxprober/music/data")
            val data: List<DivingFishChunithmMusicData> = json.decodeFromString(resp.bodyAsText())
            return data
        }
    }
}

private fun HttpRequestBuilder.getDefaultWahlapRequestBuilder() {
    headers {
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
private fun HttpMessageBuilder.cookie(
    cookie: Cookie
): Unit { // ktlint-disable no-unit-return
    val renderedCookie = cookie.let(::renderCookieHeader)

    if (HttpHeaders.Cookie !in headers) {
        headers.append(HttpHeaders.Cookie, renderedCookie)
        return
    }
    // Client cookies are stored in a single header "Cookies" and multiple values are separated with ";"
    headers[HttpHeaders.Cookie] = headers[HttpHeaders.Cookie] + "; " + renderedCookie
}