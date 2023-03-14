package com.littlechasiu.trackmap

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import kotlin.time.Duration.Companion.seconds

object TrackMap {
  val LOGGER: Logger = LogManager.getLogger("trackmap")

  private val watchInterval = 0.5.seconds
  private const val serverPort = 3876

  var minecraft: MinecraftServer? = null
    private set
  private var overworld: ServerLevel? = null
  private var nether: ServerLevel? = null

  private val watcher = TrackWatcher(watchInterval)
  private val server = Server(serverPort)

  val network get() = watcher.network
  val signals get() = watcher.signalStatus
  val blocks get() = watcher.blockStatus
  val trains get() = watcher.trainStatus

  private val scope = CoroutineScope(context = Dispatchers.IO)
  private val <T> Channel<T>.flow: SharedFlow<T>
    get() = consumeAsFlow()
      .distinctUntilChanged()
      .shareIn(scope, SharingStarted.Eagerly)
  val networkFlow = watcher.networkChannel.flow
  val signalFlow = watcher.signalChannel.flow
  val blockFlow = watcher.blockChannel.flow
  val trainFlow = watcher.trainChannel.flow

  private fun MinecraftServer.dimensionNamed(name: String): ServerLevel? =
    this.getLevel(
      ResourceKey.create(
        ResourceLocation("minecraft", "dimension"),
        ResourceLocation("minecraft", name)
      )
    )

  fun init() {
    ServerLifecycleEvents.SERVER_STARTED.register {
      minecraft = it
      overworld = it.dimensionNamed("overworld")
      nether = it.dimensionNamed("the_nether")
      watcher.start()
      server.start()
    }
    ServerLifecycleEvents.SERVER_STOPPING.register {
      watcher.stop()
      server.stop()
    }
  }
}