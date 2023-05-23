package littlechasiu.ctm.model

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class SignalColors(
  @EncodeDefault
  val green: String = "#71db51",
  @EncodeDefault
  val yellow: String = "#ffd15c",
  @EncodeDefault
  val red: String = "#ff5f5c",
  @EncodeDefault
  val outline: String = "black",
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class TrackColors(
  @EncodeDefault
  val occupied: String = "red",
  @EncodeDefault
  val reserved: String = "pink",
  @EncodeDefault
  val free: String = "white",
)

@Serializable
data class IconColors(
  val primary: String,
  val outline: String,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class Colors(
  @EncodeDefault
  val background: String = "#888",
  @EncodeDefault
  val track: TrackColors = TrackColors(),
  @EncodeDefault
  val signal: SignalColors = SignalColors(),
  @EncodeDefault
  val portal: IconColors = IconColors(primary = "purple", outline = "white"),
  @EncodeDefault
  val station: IconColors = IconColors(primary = "white", outline = "black"),
  @EncodeDefault
  val train: String = "cyan",
  @SerialName("lead_car")
  @EncodeDefault
  val leadCar: String = "plum",
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class MapStyle(
  @EncodeDefault
  val font: String = "ui-monospace, \"JetBrains Mono\", monospace",
  @EncodeDefault
  val colors: Colors = Colors(),
)

@Serializable
data class Coordinates(
  val x: Int,
  val z: Int,
)

@Serializable
enum class TrackSide {
  LEFT,
  RIGHT,
}

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class MapView(
  @SerialName("initial_dimension")
  @EncodeDefault
  val initialDimension: String = "minecraft:overworld",
  @SerialName("initial_position")
  @EncodeDefault
  val initialPosition: Coordinates = Coordinates(0, 0),
  @SerialName("initial_zoom")
  @EncodeDefault
  val initialZoom: Int = 3,

  @SerialName("min_zoom")
  @EncodeDefault
  val minZoom: Int = 0,
  @SerialName("max_zoom")
  @EncodeDefault
  val maxZoom: Int = 4,

  @SerialName("zoom_controls")
  @EncodeDefault
  val zoomControls: Boolean = false,

  @SerialName("signals_on")
  @EncodeDefault
  val signalSide: TrackSide = TrackSide.RIGHT,
)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class LayerConfig(
  val label: String,
  @SerialName("min_zoom")
  @EncodeDefault
  val minZoom: Int = 0,
  @SerialName("max_zoom")
  @EncodeDefault
  val maxZoom: Int = 4,
)

@Serializable
data class DimensionConfig(
  val label: String,
)

@Serializable
data class MapConfig(
  val view: MapView,
  val dimensions: Map<String, DimensionConfig>,
  val layers: Map<String, LayerConfig>,
)

@Serializable
data class Config @OptIn(ExperimentalSerializationApi::class) constructor(
  @EncodeDefault
  val enable: Boolean = true,

  @SerialName("watch_interval_seconds")
  @EncodeDefault
  val watchIntervalSeconds: Double = 0.5,
  @SerialName("server_port")
  @EncodeDefault
  val serverPort: Int = 3876,

  @SerialName("map_style")
  @EncodeDefault
  val mapStyle: MapStyle = MapStyle(),
  @SerialName("map_view")
  @EncodeDefault
  val mapView: MapView = MapView(),
  @EncodeDefault
  val dimensions: Map<String, DimensionConfig> = mapOf(
    "minecraft:overworld" to DimensionConfig(label = "Overworld"),
    "minecraft:the_nether" to DimensionConfig(label = "Nether"),
    "minecraft:the_end" to DimensionConfig(label = "End"),
  ),
  @EncodeDefault
  val layers: Map<String, LayerConfig> = mapOf(
    "tracks" to LayerConfig(label = "Tracks"),
    "blocks" to LayerConfig(label = "Track Occupancy"),
    "signals" to LayerConfig(label = "Signals"),
    "portals" to LayerConfig(label = "Portals"),
    "stations" to LayerConfig(label = "Stations"),
    "trains" to LayerConfig(label = "Trains"),
  ),
)
