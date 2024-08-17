package io.github.skydynamic.maiprober.proxy

import io.github.skydynamic.maiprober.util.config.Config
import io.github.skydynamic.maiprober.util.prober.DivingFishProberUtil
import io.github.skydynamic.maiprober.util.prober.InterceptHandler
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import org.slf4j.Logger
import java.net.URI

val logger: Logger = LoggerFactory.getLogger("MaiProber-Proxy")

class ProxyServer(port: Int):Thread() {

    init {
        name = "ProxyServer"
    }

    private val server: NettyApplicationEngine = embeddedServer(
        Netty,
        port = port,
        host = "0.0.0.0",
        module = Application::module
    )

    override fun start() {
        try{
            server.start(true)
        }catch (e: Exception) {
            if (e !is InterruptedException)
                e.printStackTrace()
        }
    }
}


fun Application.module(){
    configureRouting()
}

fun Application.configureRouting() {
    intercept(ApplicationCallPipeline.Call) {
        val requestUrl = call.request.uri
        try {
            val uri = URI(requestUrl)
            if (uri.scheme.equals("http")) {
                if (uri.host.equals("tgk-wcaime.wahlap.com"))
                    call.respondRedirect("http://127.0.0.1:${Config.INSTANCE.proxyPort}/success")
                    InterceptHandler.onAuthHook(uri)
            } else
                call.respond(HttpStatusCode.BadRequest, "Invalid URL")
                return@intercept
        } catch (_: Exception) { }
    }
    routing {
        get("/success") {
            call.respond(HttpStatusCode.OK, "查询完成，请返回查分器查看")
        }
    }
}
