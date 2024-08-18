package io.github.skydynamic.maiprober.proxy

import io.ktor.server.locations.*

class LocationsData {
    @OptIn(KtorExperimentalLocationsAPI::class)
    @Location("/oauth/{type}")
    data class Oauth(val type: String, val token: String? = null)
}