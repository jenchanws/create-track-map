package com.littlechasiu.trackmap

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.nio.file.Files
import kotlin.time.Duration.Companion.seconds

object TrackMap {
  const val MODID: String = "create-track-map"

  private const val configFileName = "$MODID.json"
  private val GSON: Gson =
    GsonBuilder().setPrettyPrinting().disableHtmlEscaping().setLenient()
      .create()

  val LOGGER: Logger = LogManager.getLogger(MODID)

  private var config = Config()
  private var watcher = TrackWatcher()
  private var server = Server()

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

  private fun loadConfig() {
    try {
      val configFile =
        FabricLoader.getInstance().configDir.resolve(configFileName)

      if (Files.exists(configFile)) {
        config = GSON.fromJson(
          Files.newBufferedReader(configFile, Charsets.UTF_8),
          Config::class.java
        )
      } else {
        LOGGER.warn("Create Track Map config does not exist, writing defaults to $configFileName")
        config = Config()
        val newConfigFile = Files.newBufferedWriter(configFile, Charsets.UTF_8)
        GSON.toJson(config, newConfigFile)
        newConfigFile.flush()
        newConfigFile.close()
      }
    } catch (e: Exception) {
      LOGGER.error("Error loading Create Track Map config")
      e.printStackTrace()
    }

    watcher.watchInterval = config.watchIntervalSeconds.seconds
    server.port = config.serverPort
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

    CommandRegistrationCallback.EVENT.register { disp, _, env ->
      disp.register(Commands.literal("ctm").then(Commands.literal("reload")
        .requires { src -> src.hasPermission(4) }.executes { _ ->
          reload()
          1
        }))
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