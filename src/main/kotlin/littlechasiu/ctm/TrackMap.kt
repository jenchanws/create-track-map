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
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import kotlin.time.Duration.Companion.seconds

object TrackMap {
  const val MODID: String = "create-track-map"

  private const val configFileName = "$MODID.json"

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

  @OptIn(ExperimentalSerializationApi::class)
  private fun loadConfig() {
    try {
      val configFile =
        FabricLoader.getInstance().configDir.resolve(configFileName)

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
    server.layers = config.layers
    BlueMapIntegration.mapStyle = config.mapStyle
  }

  private fun reload() {
    watcher.stop()
    server.stop()

    loadConfig()

    watcher.start()
    server.start()
  }

  fun init() {
    loadConfig()

    CommandRegistrationCallback.EVENT.register { disp, _, _ ->
      disp.register(
        Commands.literal("ctm").then(
          Commands.literal("reload")
            .requires { src -> src.hasPermission(4) }.executes { _ ->
              reload()
              1
            })
      )
    }

    ServerLifecycleEvents.SERVER_STARTED.register {
      watcher.start()
      server.start()
    }
    ServerLifecycleEvents.SERVER_STOPPING.register {
      watcher.stop()
      server.stop()
    }
  }
}
