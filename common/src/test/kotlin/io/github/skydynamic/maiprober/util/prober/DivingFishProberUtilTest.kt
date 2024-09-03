package io.github.skydynamic.maiprober.util.prober

import io.github.skydynamic.maiprober.util.prober.divingfish.DivingFishProberUtil.MaimaiRecordsData
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
    fun validateProberAccount() = runBlocking {
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

    @Test
    fun getData() = runBlocking {
        val data: HttpResponse = client.get("https://www.diving-fish.com/api/maimaidxprober/player/records") {
            headers {
                append("Import-Token", "3ba41cc21ba1c3ebe3574aa2c733e34430ca4ee0f3868b6ab26c04064710e1d6f1a8edd30531414fbd109d962a89f057ef86ccdb392440b7221abdd0ba50c630")
            }
        }

        if (data.status != HttpStatusCode.OK) {
            throw RuntimeException("Import-Token 无效")
        }

        val resp: MaimaiRecordsData = data.body()
        print(resp.records)
    }
}