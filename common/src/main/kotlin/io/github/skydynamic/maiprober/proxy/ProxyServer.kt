package io.github.skydynamic.maiprober.proxy

import io.github.skydynamic.maiprober.ProberContext
import io.github.skydynamic.maiprober.proxy.handler.InterceptHandler
import io.github.skydynamic.maiprober.proxy.handler.OauthLocationsHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.locations.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.net.URI

class ProxyServer(private val context: ProberContext) : Thread() {

    init {
        name = "ProxyServer"
    }

    private val server: NettyApplicationEngine = embeddedServer(
        Netty,
        port = context.requireConfig().proxyPort,
        host = "0.0.0.0",
        module = {
            module(context)
        }
    )

    override fun start() {
        try {
            server.start(true)
        } catch (e: Exception) {
            if (e !is InterruptedException)
                e.printStackTrace()
        }
    }
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.module(context: ProberContext) {
    install(Locations)
    configureIntercept(context)
    configureRouting(context)
}

fun Application.configureIntercept(context: ProberContext) {
    intercept(ApplicationCallPipeline.Call) {
        val requestUrl = call.request.uri
        try {
            val uri = URI(requestUrl)
            if (uri.scheme.equals("http")) {
                if (uri.host.equals("tgk-wcaime.wahlap.com")) {
                    call.respondRedirect("http://127.0.0.1:${context.requireConfig().proxyPort}/success")
                    InterceptHandler.onAuthHook(uri, context.requireConfig())
                }
            } else
                call.respond(HttpStatusCode.BadRequest, "Invalid URL")
            return@intercept
        } catch (_: Exception) {
        }
    }
}

@OptIn(KtorExperimentalLocationsAPI::class)
fun Application.configureRouting(context: ProberContext) {
    routing {
        get<LocationsData.Oauth> { oauth ->
            OauthLocationsHandler.handle(context, call, oauth.type, oauth.token)
        }
        get("/success") {
            call.respond(HttpStatusCode.OK, "查询完成，请返回查分器查看")
        }
    }
}
