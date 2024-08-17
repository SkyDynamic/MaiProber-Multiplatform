package io.github.skydynamic.maiprober.proxy

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

import java.net.URI

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
    routing {
        get("/{path}") {
            val uri = call.request.uri
            val targetUri = URI(uri)

            if (targetUri.scheme == "http") {

            } else {
                call.respond(HttpStatusCode.BadRequest, "!Bad Request!")
            }
        }
    }
}
