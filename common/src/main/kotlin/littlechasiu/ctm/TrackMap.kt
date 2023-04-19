package littlechasiu.ctm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import littlechasiu.ctm.model.Config
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.time.Duration.Companion.seconds

object TrackMap {
  const val MODID: String = "create-track-map"
  const val configFileName = "$MODID.json"

  @OptIn(ExperimentalSerializationApi::class)
  private val JSON = Json {
    isLenient = true
    ignoreUnknownKeys = true
    prettyPrint = true
    prettyPrintIndent = "  "
  }

  val LOGGER: Logger = LogManager.getLogger(MODID)

  private var config = Config()
  val watcher = TrackWatcher()
  private val server = Server()

  val network get() = watcher.network
  val signals get() = watcher.signalStatus
  val blocks get() = watcher.blockStatus
  val trains get() = watcher.trainStatus

  private val scope = CoroutineScope(context = Dispatchers.IO)
  private val <T> Channel<T>.flow: SharedFlow<T>
    get() = consumeAsFlow().distinctUntilChanged()
      .shareIn(scope, SharingStarted.Eagerly)
  val networkFlow = watcher.networkChannel.flow
  val signalFlow = watcher.signalChannel.flow
  val blockFlow = watcher.blockChannel.flow
  val trainFlow = watcher.trainChannel.flow

  var platform: Platform? = null

  @OptIn(ExperimentalSerializationApi::class)
  private fun loadConfig() {
    try {
      val configFile = platform?.configFile

      if (Files.exists(configFile)) {
        config = JSON.decodeFromStream(Files.newInputStream(configFile))
      } else {
        LOGGER.warn("Create Track Map config does not exist, writing defaults to $configFileName")
        config = Config()
        JSON.encodeToStream(
          config,
          Files.newOutputStream(configFile, StandardOpenOption.CREATE)
        )
      }
    } catch (e: Exception) {
      LOGGER.error("Error loading Create Track Map config, using defaults")
      e.printStackTrace()
      config = Config()
    }

    watcher.enable = config.enable
    server.enable = config.enable
    watcher.watchInterval = config.watchIntervalSeconds.seconds
    server.port = config.serverPort
    server.mapStyle = config.mapStyle
    server.mapView = config.mapView
    server.dimensions = config.dimensions
  }

  fun reload() {
    stop()
    start()
  }

  fun start() {
    loadConfig()
    watcher.start()
    server.start()
  }

  fun stop() {
    watcher.stop()
    server.stop()
  }
}
