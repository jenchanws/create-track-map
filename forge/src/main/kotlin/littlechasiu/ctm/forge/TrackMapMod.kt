package littlechasiu.ctm.forge

import littlechasiu.ctm.Platform
import littlechasiu.ctm.TrackMap
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.server.ServerStartedEvent
import net.minecraftforge.event.server.ServerStoppingEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLConfig
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import java.nio.file.Path

object TrackMapForgePlatform : Platform {
  override val configFile: Path
    get() = Path.of(FMLConfig.defaultConfigPath())
}

@Mod(TrackMapMod.MODID)
object TrackMapMod {
  const val MODID = "createtrackmap"

  init {
    TrackMap.platform = TrackMapForgePlatform
    FORGE_BUS.addListener(::registerCommands)
    FORGE_BUS.addListener(::serverStarted)
    FORGE_BUS.addListener(::serverStopping)
  }

  private fun registerCommands(event: RegisterCommandsEvent) {
    event.dispatcher.register(
      Commands.literal("ctm").then(
        Commands.literal("reload")
          .requires { src -> src.hasPermission(4) }.executes { _ ->
            TrackMap.reload()
            1
          })
    )
  }

  private fun serverStarted(event: ServerStartedEvent) {
    TrackMap.start()
  }

  private fun serverStopping(event: ServerStoppingEvent) {
    TrackMap.stop()
  }
}
