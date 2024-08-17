package io.github.skydynamic.maiprober.util.prober

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.Logger
import org.slf4j.LoggerFactory

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

    private val logger: Logger = LoggerFactory.getLogger("MaimaiDX-Prober-Kotlin")

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
        authUrl: String,
        data: String
    ) : String {
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
