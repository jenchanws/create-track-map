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
import kotlinx.css.Color
import kotlinx.css.CssBuilder
import kotlinx.css.CssValue
import kotlinx.css.quoted
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.Writer

class Server {
  @OptIn(ExperimentalSerializationApi::class)
  val jsonPretty = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
  }

  var port: Int = 3876
  var mapStyle: MapStyle = MapStyle()

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

  private suspend inline fun <reified T> ApplicationCall.respondJSON(obj: T) {
    if (request.queryParameters.contains("pretty"))
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

  private suspend inline fun ApplicationCall.respondCSS(builder: CssBuilder.() -> Unit) {
    respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
  }

  private fun CssBuilder.variables(vararg pairs: Pair<String, CssValue>) {
    pairs.forEach { (name, value) ->
      setCustomProperty(name, value)
    }
  }

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

      get("/api/style.css") {
        call.respondCSS {
          root {
            variables(
              "ui-font" to mapStyle.font.quoted,
              "map-background" to Color(mapStyle.colors.background),
              "track-occupied" to Color(mapStyle.colors.track.occupied),
              "track-reserved" to Color(mapStyle.colors.track.reserved),
              "track-free" to Color(mapStyle.colors.track.free),
              "signal-green" to Color(mapStyle.colors.signal.green),
              "signal-yellow" to Color(mapStyle.colors.signal.yellow),
              "signal-red" to Color(mapStyle.colors.signal.red),
              "signal-outline" to Color(mapStyle.colors.signal.outline),
              "portal-color" to Color(mapStyle.colors.portal.primary),
              "portal-outline" to Color(mapStyle.colors.portal.outline),
              "station-color" to Color(mapStyle.colors.station.primary),
              "station-outline" to Color(mapStyle.colors.station.outline),
              "train-color" to Color(mapStyle.colors.train),
            )
          }
        }
      }

      get("/api/network") { call.respondJSON(TrackMap.network) }
      get("/api/signals") { call.respondJSON(TrackMap.signals) }
      get("/api/blocks") { call.respondJSON(TrackMap.blocks) }
      get("/api/trains") { call.respondJSON(TrackMap.trains) }

      get("/api/network.rt") {
        call.respondSSE(TrackMap.network, TrackMap.networkFlow)
      }
      get("/api/signals.rt") {
        call.respondSSE(TrackMap.signals, TrackMap.signalFlow)
      }
      get("/api/blocks.rt") {
        call.respondSSE(TrackMap.blocks, TrackMap.blockFlow)
      }
      get("/api/trains.rt") {
        call.respondSSE(TrackMap.trains, TrackMap.trainFlow)
      }
    }
  }
}
