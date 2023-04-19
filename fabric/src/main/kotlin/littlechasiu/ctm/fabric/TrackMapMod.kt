package littlechasiu.ctm.fabric

import littlechasiu.ctm.Platform
import littlechasiu.ctm.TrackMap
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.commands.Commands
import java.nio.file.Path

object TrackMapFabricPlatform : Platform {
  override val configFile: Path
    get() = FabricLoader.getInstance().configDir.resolve(
      TrackMap.configFileName
    )
}

fun init() {
  TrackMap.platform = TrackMapFabricPlatform

  CommandRegistrationCallback.EVENT.register { disp, _, _ ->
    disp.register(
      Commands.literal("ctm").then(
        Commands.literal("reload")
          .requires { src -> src.hasPermission(4) }.executes { _ ->
            TrackMap.reload()
            1
          })
    )
  }

  ServerLifecycleEvents.SERVER_STARTED.register {
    TrackMap.start()
  }

  ServerLifecycleEvents.SERVER_STOPPING.register {
    TrackMap.stop()
  }
}
