package com.littlechasiu.trackmap

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Writer

class Server() {
  @OptIn(ExperimentalSerializationApi::class)
  val jsonPretty = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
  }

  var port: Int = 3876

  private var server: NettyApplicationEngine? = null

  fun start() {
    server = embeddedServer(Netty, port) {
      install(CORS) {
        anyHost()
      }
      module()
    }
    server?.start(false)
    TrackMap.LOGGER.info("Started Create Track Map server on port $port")
  }

  fun stop() {
    TrackMap.LOGGER.info("Stopping Create Track Map server")
    server?.stop(1000, 5000)
    server = null
  }

  private inline fun <reified T> Writer.writeSSE(obj: T) {
    val json = Json.encodeToString(obj)
    json.lines().forEach { line -> write("data: $line\n") }
    write("\n")
    flush()
  }

  private suspend inline fun <reified T> ApplicationCall.respondJSON(
    obj: T,
    pretty: Boolean = false
  ) {
    if (pretty)
      respondText(jsonPretty.encodeToString(obj))
    else
      respondText(Json.encodeToString(obj))
  }

  private suspend inline fun <reified T> ApplicationCall.respondSSE(
    initial: T,
    flow: Flow<T>
  ) {
    respondTextWriter(contentType = ContentType.Text.EventStream) {
      writeSSE(initial)
      flow.map { writeSSE(it) }.collect {}
    }
  }

  private val ApplicationCall.pretty
    get() = request.queryParameters.contains("pretty")

  private fun Application.module() {
    routing {
      static("/") {
        staticBasePackage = "assets"
        defaultResource("index.html")
        static("assets") {
          static("css") { resources("css") }
          static("js") { resources("js") }
        }
      }

      get("/static/network") {
        call.respondJSON(TrackMap.network, call.pretty)
      }

      get("/static/signals") {
        call.respondJSON(TrackMap.signals, call.pretty)
      }

      get("/static/blocks") {
        call.respondJSON(TrackMap.blocks, call.pretty)
      }

      get("/static/trains") {
        call.respondJSON(TrackMap.trains, call.pretty)
      }

      get("/rt/network") {
        call.respondSSE(TrackMap.network, TrackMap.networkFlow)
      }

      get("/rt/signals") {
        call.respondSSE(TrackMap.signals, TrackMap.signalFlow)
      }

      get("/rt/blocks") {
        call.respondSSE(TrackMap.blocks, TrackMap.blockFlow)
      }

      get("/rt/trains") {
        call.respondSSE(TrackMap.trains, TrackMap.trainFlow)
      }
    }
  }
}