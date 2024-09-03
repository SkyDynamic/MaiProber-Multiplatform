package io.github.skydynamic.maiprober.util.prober

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
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