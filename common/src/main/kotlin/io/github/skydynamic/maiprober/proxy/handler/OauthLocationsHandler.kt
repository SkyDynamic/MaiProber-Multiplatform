package io.github.skydynamic.maiprober.proxy.handler

import io.github.skydynamic.maiprober.ProberContext
import io.github.skydynamic.maiprober.util.OauthTokenUtil
import io.github.skydynamic.maiprober.util.prober.WechatRequestUtil
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class OauthLocationsHandler {
    companion object {
        fun handle(context: ProberContext, call : ApplicationCall, type: String, token: String?) = runBlocking {
            if (token == null) {
                call.respond(HttpStatusCode.BadRequest, "Token is null")
            } else {
                if (OauthTokenUtil.validateToken(token, context.requireConfig().secretKey)) {
                    when (type) {
                        "maimai-dx" -> {
                            val url = WechatRequestUtil.getAuthUrl("maimai-dx")
                            println(url)
                            call.respondRedirect(url)
                        }
                        "chunithm" -> {
                            call.respondRedirect(WechatRequestUtil.getAuthUrl("chunithm"))
                        }
                    }
                } else {
                    val now = LocalDateTime.now(ZoneId.of("GMT")).atZone(ZoneId.of("GMT"))
                    call.respond(HttpStatusCode.Forbidden,
                        "HTTP/1.1 403 Forbidden\n" +
                                "Data: ${now.format(DateTimeFormatter
                                    .ofPattern("EEE, dd MMM yyyy HH:mm:ss z"))}")
                }
            }
        }
    }
}