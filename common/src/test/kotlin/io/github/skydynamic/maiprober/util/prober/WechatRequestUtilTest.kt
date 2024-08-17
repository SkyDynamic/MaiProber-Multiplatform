package io.github.skydynamic.maiprober.util.prober

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

class WechatRequestUtilTest {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    @Test
    fun getAuthUrl() = runBlocking {
        val resp: HttpResponse = client.get("https://tgk-wcaime.wahlap.com/wc_auth/oauth/authorize/maimai-dx")
        val url = resp.request.url
        println(url)
    }
}