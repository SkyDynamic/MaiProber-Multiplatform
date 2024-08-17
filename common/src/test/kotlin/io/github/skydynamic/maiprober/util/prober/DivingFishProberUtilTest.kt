package io.github.skydynamic.maiprober.util.prober

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.File

class DivingFishProberUtilTest {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @Serializable
    data class LoginResponse(val errcode: Int? = null, val message: String)

    @Test
    fun verifyProberAccount() = runBlocking {
        val resp: HttpResponse = client.post("https://www.diving-fish.com/api/maimaidxprober/login") {
            headers {
                append(HttpHeaders.ContentType, "application/json;charset=UTF-8")
                append(HttpHeaders.Referrer, "https://www.diving-fish.com/maimaidx/prober/")
                append(HttpHeaders.Origin, "https://www.diving-fish.com")
            }
            contentType(ContentType.Application.Json)
            setBody("""{"username":"","password":""}""")
        }
        val body: LoginResponse = resp.body()
        assert(body.errcode == null)
    }

    @Test
    fun uploadMaimaiProberData() = runBlocking {
        val username = ""
        val password = ""
        val data = ""

        val resp: HttpResponse = client.post("https://www.diving-fish.com/api/pageparser/page") {
            headers {
                append(HttpHeaders.ContentType, "text/plain")
            }
            contentType(ContentType.Text.Plain)
            setBody("""<login><u>$username</u><p>$password</p></login>$data""")
        }
        val respData: String = resp.body()
        println(respData)
    }
}