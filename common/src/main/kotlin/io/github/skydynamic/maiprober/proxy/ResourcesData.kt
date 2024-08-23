package io.github.skydynamic.maiprober.proxy

import io.ktor.resources.*

class ResourcesData {
    @Resource("/oauth/{type}")
    data class Oauth(val type: String, val token: String? = null)
}