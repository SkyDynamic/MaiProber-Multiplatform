package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.interfact.ProberUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DivingFishProberUtil : ProberUtil {
    private val LOGIN_URL = "https://www.diving-fish.com/api/maimaidxprober/login"
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(HttpCookies) {
            storage = AcceptAllCookiesStorage()
        }
    }

    private val logger: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")
    private lateinit var jwtToken: Cookie
    private lateinit var username: String
    private lateinit var password: String

    @Serializable
    data class LoginResponse(val errcode: Int? = null, val message: String)

    override suspend fun validateProberAccount(username: String, password: String): Boolean {
        val loginResp = client.post(LOGIN_URL) {
            contentType(ContentType.Application.Json)
            setBody(LoginData(username, password))
        }
        if (loginResp.status != HttpStatusCode.OK) {
            return false
        }
        val cookies = loginResp.setCookie()
        cookies.forEach {
            println(it.name to it.value)
        }
        jwtToken = cookies.find { it.name == "jwt_token" } ?: return false
        return true
    }

    private suspend fun parseMaiResult(content: String): String {
        val resp = client.post("http://www.diving-fish.com:8089/page") {
            cookie(jwtToken)
            setBody(content)
            contentType(ContentType.Text.Plain)
        }
        return resp.bodyAsText()
    }

    override suspend fun uploadMaimaiProberData(
        authUrl: String,
        data: String
    ): String {
        logger.info("开始更新Maimai成绩")

        //TODO 代理获取登录cookie

        val resp: HttpResponse = client.post("https://www.diving-fish.com/api/pageparser/page") {
            headers {
                append(HttpHeaders.ContentType, "text/plain")
            }
            contentType(ContentType.Text.Plain)
            setBody("""<login><u>$username</u><p>$password</p></login>$data""")
        }
        val respData: String = resp.body()
        return respData
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
