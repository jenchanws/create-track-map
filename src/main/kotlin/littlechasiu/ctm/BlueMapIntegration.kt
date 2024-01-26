package littlechasiu.ctm

import com.flowpowered.math.vector.Vector3d
import com.flowpowered.math.vector.Vector3l
import com.simibubi.create.content.trains.signal.SignalBlockEntity
import de.bluecolored.bluemap.api.BlueMapAPI
import de.bluecolored.bluemap.api.BlueMapMap
import de.bluecolored.bluemap.api.markers.LineMarker
import de.bluecolored.bluemap.api.markers.MarkerSet
import de.bluecolored.bluemap.api.markers.POIMarker
import de.bluecolored.bluemap.api.markers.ShapeMarker
import de.bluecolored.bluemap.api.math.Color
import de.bluecolored.bluemap.api.math.Line
import de.bluecolored.bluemap.api.math.Shape
import littlechasiu.ctm.model.*

object BlueMapIntegration {
  var mapStyle = MapStyle()

  private const val BLUEMAP_TRACK_CURVE_POINTS = 10

  private const val BLUEMAP_TRACK_LABEL = "Create Track"
  private const val BLUEMAP_TRACK_ID = "create-track"
  private const val BLUEMAP_STATION_LABEL = "Create Station"
  private const val BLUEMAP_STATION_ID = "create-station"
  private const val BLUEMAP_TRAIN_LABEL = "Create Train"
  private const val BLUEMAP_TRAIN_ID = "create-train"
  private const val BLUEMAP_SIGNAL_LABEL = "Create Signal"
  private const val BLUEMAP_SIGNAL_ID = "create-signal"

  private val CSS_NAMED_COLORS = mapOf(
    "black" to "#000000",
    "silver" to "#c0c0c0",
    "gray" to "#808080",
    "white" to "#ffffff",
    "maroon" to "#800000",
    "red" to "#ff0000",
    "purple" to "#800080",
    "fuchsia" to "#ff00ff",
    "green" to "#008000",
    "lime" to "#00ff00",
    "olive" to "#808000",
    "yellow" to "#ffff00",
    "navy" to "#000080",
    "blue" to "#0000ff",
    "teal" to "#008080",
    "aqua" to "#00ffff",
    "aliceblue" to "#f0f8ff",
    "antiquewhite" to "#faebd7",
    "aquamarine" to "#7fffd4",
    "azure" to "#f0ffff",
    "beige" to "#f5f5dc",
    "bisque" to "#ffe4c4",
    "blanchedalmond" to "#ffebcd",
    "blueviolet" to "#8a2be2",
    "brown" to "#a52a2a",
    "burlywood" to "#deb887",
    "cadetblue" to "#5f9ea0",
    "chartreuse" to "#7fff00",
    "chocolate" to "#d2691e",
    "coral" to "#ff7f50",
    "cornflowerblue" to "#6495ed",
    "cornsilk" to "#fff8dc",
    "crimson" to "#dc143c",
    "cyan" to "#00ffff",
    "darkblue" to "#00008b",
    "darkcyan" to "#008b8b",
    "darkgoldenrod" to "#b8860b",
    "darkgray" to "#a9a9a9",
    "darkgreen" to "#006400",
    "darkgrey" to "#a9a9a9",
    "darkkhaki" to "#bdb76b",
    "darkmagenta" to "#8b008b",
    "darkolivegreen" to "#556b2f",
    "darkorange" to "#ff8c00",
    "darkorchid" to "#9932cc",
    "darkred" to "#8b0000",
    "darksalmon" to "#e9967a",
    "darkseagreen" to "#8fbc8f",
    "darkslateblue" to "#483d8b",
    "darkslategray" to "#2f4f4f",
    "darkslategrey" to "#2f4f4f",
    "darkturquoise" to "#00ced1",
    "darkviolet" to "#9400d3",
    "deeppink" to "#ff1493",
    "deepskyblue" to "#00bfff",
    "dimgray" to "#696969",
    "dimgrey" to "#696969",
    "dodgerblue" to "#1e90ff",
    "firebrick" to "#b22222",
    "floralwhite" to "#fffaf0",
    "forestgreen" to "#228b22",
    "gainsboro" to "#dcdcdc",
    "ghostwhite" to "#f8f8ff",
    "gold" to "#ffd700",
    "goldenrod" to "#daa520",
    "greenyellow" to "#adff2f",
    "grey" to "#808080",
    "honeydew" to "#f0fff0",
    "hotpink" to "#ff69b4",
    "indianred" to "#cd5c5c",
    "indigo" to "#4b0082",
    "ivory" to "#fffff0",
    "khaki" to "#f0e68c",
    "lavender" to "#e6e6fa",
    "lavenderblush" to "#fff0f5",
    "lawngreen" to "#7cfc00",
    "lemonchiffon" to "#fffacd",
    "lightblue" to "#add8e6",
    "lightcoral" to "#f08080",
    "lightcyan" to "#e0ffff",
    "lightgoldenrodyellow" to "#fafad2",
    "lightgray" to "#d3d3d3",
    "lightgreen" to "#90ee90",
    "lightgrey" to "#d3d3d3",
    "lightpink" to "#ffb6c1",
    "lightsalmon" to "#ffa07a",
    "lightseagreen" to "#20b2aa",
    "lightskyblue" to "#87cefa",
    "lightslategray" to "#778899",
    "lightslategrey" to "#778899",
    "lightsteelblue" to "#b0c4de",
    "lightyellow" to "#ffffe0",
    "limegreen" to "#32cd32",
    "linen" to "#faf0e6",
    "magenta" to "#ff00ff",
    "mediumaquamarine" to "#66cdaa",
    "mediumblue" to "#0000cd",
    "mediumorchid" to "#ba55d3",
    "mediumpurple" to "#9370db",
    "mediumseagreen" to "#3cb371",
    "mediumslateblue" to "#7b68ee",
    "mediumspringgreen" to "#00fa9a",
    "mediumturquoise" to "#48d1cc",
    "mediumvioletred" to "#c71585",
    "midnightblue" to "#191970",
    "mintcream" to "#f5fffa",
    "mistyrose" to "#ffe4e1",
    "moccasin" to "#ffe4b5",
    "navajowhite" to "#ffdead",
    "oldlace" to "#fdf5e6",
    "olivedrab" to "#6b8e23",
    "orange" to "#ffa500",
    "orangered" to "#ff4500",
    "orchid" to "#da70d6",
    "palegoldenrod" to "#eee8aa",
    "palegreen" to "#98fb98",
    "paleturquoise" to "#afeeee",
    "palevioletred" to "#db7093",
    "papayawhip" to "#ffefd5",
    "peachpuff" to "#ffdab9",
    "peru" to "#cd853f",
    "pink" to "#ffc0cb",
    "plum" to "#dda0dd",
    "powderblue" to "#b0e0e6",
    "rebeccapurple" to "#663399",
    "rosybrown" to "#bc8f8f",
    "royalblue" to "#4169e1",
    "saddlebrown" to "#8b4513",
    "salmon" to "#fa8072",
    "sandybrown" to "#f4a460",
    "seagreen" to "#2e8b57",
    "seashell" to "#fff5ee",
    "sienna" to "#a0522d",
    "skyblue" to "#87ceeb",
    "slateblue" to "#6a5acd",
    "slategray" to "#708090",
    "slategrey" to "#708090",
    "snow" to "#fffafa",
    "springgreen" to "#00ff7f",
    "steelblue" to "#4682b4",
    "tan" to "#d2b48c",
    "thistle" to "#d8bfd8",
    "tomato" to "#ff6347",
    "transparent" to "#0000",
    "turquoise" to "#40e0d0",
    "violet" to "#ee82ee",
    "wheat" to "#f5deb3",
    "whitesmoke" to "#f5f5f5",
    "yellowgreen" to "#9acd32",
  )

  private fun getMarkerSet(map: BlueMapMap, id: String, label: String): MarkerSet {
    val mapMarkerSet = map.markerSets[id]
    val markerSet: MarkerSet

    if (mapMarkerSet != null) {
      markerSet = mapMarkerSet
    } else {
      markerSet = MarkerSet
        .builder()
        .label(label)
        .build()
      map.markerSets[id] = markerSet
    }

    return markerSet
  }

  private fun htmlEscape(str: String): String {
    return str
      .replace("&", "&amp;")
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
      .replace("'", "&#x27;")
  }

  private fun <T> getDefault(value: T?, default: T): T {
    if (value == null) return default
    return value
  }

  private fun getCssColor(cssColor: String): String {
    if (cssColor[0] == '#') {
      return cssColor
    }

    return CSS_NAMED_COLORS.getOrDefault(cssColor, "#000")
  }

  private fun bezier(points: List<Point>): MutableList<Vector3d> {
    // https://denisrizov.com/2016/06/02/bezier-curves-unity-package-included/
    val vecPoints = points.map { Vector3d(it.x, it.y + 1, it.z) }
    return (0..BLUEMAP_TRACK_CURVE_POINTS)
            .asSequence()
            .map { it.toFloat() / BLUEMAP_TRACK_CURVE_POINTS }
            .map { t ->
              val u = 1.0 - t
              val t2 = t * t
              val u2 = u * u
              val t3 = t2 * t
              val u3 = u2 * u
              (vecPoints[0].mul(u3))
                      .add(vecPoints[1].mul(3.0 * u2 * t))
                      .add(vecPoints[2].mul(3.0 * u * t2))
                      .add(vecPoints[3].mul(t3))
                      .mul(1000.0).round().div(1000.0) // truncate to 3 decimal places
            }
            .toMutableList()
  }

  private fun getUnitVector(a: Vector3d, b: Vector3d): Vector3d {
    return a.sub(b).normalize()
  }

  private fun mergeLines(input: MutableList<MutableList<Vector3d>>) {
    val lineIterator = input.listIterator()
    var prevLine: MutableList<Vector3d>? = null
    while (lineIterator.hasNext()) {
      val currentLine = lineIterator.next()
      // lines are adjacent, try to merge
      if (prevLine != null && prevLine.last() == currentLine.first()) {
        // if both lines are heading to the same direction, we can omit the midpoint when joining
        if (getUnitVector(prevLine[prevLine.size - 2], prevLine[prevLine.size - 1]) == getUnitVector(currentLine[0], currentLine[1])) {
          prevLine.removeLast()
        }
        prevLine.addAll(currentLine)
        lineIterator.remove()
      } else {
        prevLine = currentLine
      }
    }
  }

  private fun updateTracks(blueMap: BlueMapAPI, tracks: List<Edge>) {
    val dimensions = tracks.groupBy { it.dimension }
    dimensions.forEach { (dimension, edges) ->
      blueMap.getWorld(dimension).ifPresent { world ->
        // CTM tracks has tiny, overlapping segments, sometimes in both directions, which slows down BlueMap
        // We normalize all the lines in one direction, filter out points due to precision issues, render
        // Bézier curves into straight lines, sort them based on starting / ending points and merge them.
        // FIXME: severe Z-fighting due to large amount of overlapping segments
        val lines = edges
                .asSequence()
                .map { edge ->
                  // normalize paths to point in +ve
                  val first = edge.path.first()
                  val last = edge.path.last()
                  if (first.x > last.x || first.y > last.y || first.z > last.z) {
                    edge.path.asReversed()
                  } else {
                    edge.path
                  }
                }
                .distinctBy { it.map { p -> Vector3l(p.x * 10000, p.y * 10000, p.z * 10000) } }
                .map {path ->
                  when (path.size) {
                    4 -> bezier(path)
                    2 -> path.asSequence().map { Vector3d(it.x, it.y + 1, it.z) }.toMutableList()
                    else -> throw IllegalStateException("unsupported edge type")
                  }
                }
                .toMutableList()

        // your mileage may vary
        lines.sortWith(compareBy({ it.last().x }, { it.last().y }, { it.last().z }))
        mergeLines(lines)
        lines.sortWith(compareBy({ it[0].x }, { it[0].y }, { it[0].z }))
        mergeLines(lines)

        for (line in lines) {
          val markerBuilder = LineMarker
                  .builder()
                  .label(BLUEMAP_TRACK_LABEL)
                  .lineWidth(2)
                  .line(Line(line))

          for (map in world.maps) {
            val markerSet = getMarkerSet(map, BLUEMAP_TRACK_ID, BLUEMAP_TRACK_LABEL)
            markerSet.markers[BLUEMAP_TRACK_ID + "-" + markerSet.markers.size] = markerBuilder.build()
          }
        }
      }
    }
  }

  private fun updateStation(blueMap: BlueMapAPI, station: Station) {
    val marker = POIMarker
      .builder()
      .label(station.name)
      .detail(
        "<div style=\"width: max-content\">" +
          "<div>Name: " + htmlEscape(station.name) + "</div>" +
          "<div>Facing: <span style=\"transform: rotateZ(" + station.angle + "deg);display: inline-block;vertical-align: middle;\">" +
          "<svg xmlns=\"http://www.w3.org/2000/svg\" height=\"16\" width=\"12\" viewBox=\"0 0 384 512\"><path d=\"M214.6 41.4c-12.5-12.5-32.8-12.5-45.3 0l-160 160c-12.5 12.5-12.5 32.8 0 45.3s32.8 12.5 45.3 0L160 141.2V448c0 17.7 14.3 32 32 32s32-14.3 32-32V141.2L329.4 246.6c12.5 12.5 32.8 12.5 45.3 0s12.5-32.8 0-45.3l-160-160z\" fill=\"currentColor\"></svg>" +
          "</span></div>" +
          "</div>"
      )
      .position(station.location.x, station.location.y, station.location.z)
      .build()

    blueMap.getWorld(station.dimension).ifPresent { world ->
      for (map in world.maps) {
        val markerSet = getMarkerSet(map, BLUEMAP_STATION_ID, BLUEMAP_STATION_LABEL)
        markerSet.markers[BLUEMAP_STATION_ID + "-" + markerSet.markers.size] = marker
      }
    }
  }

  private fun updateTrain(blueMap: BlueMapAPI, train: CreateTrain) {
    for (car in train.cars) {
      val pt0 = Vector3d(
        getDefault(car.leading?.location?.x, 0.0),
        getDefault(car.leading?.location?.y, 0.0),
        getDefault(car.leading?.location?.z, 0.0)
      )
      val pt1 = Vector3d(
        getDefault(car.trailing?.location?.x, 0.0),
        getDefault(car.trailing?.location?.y, 0.0),
        getDefault(car.trailing?.location?.z, 0.0)
      )
      val marker = LineMarker
        .builder()
        .label(train.name)
        .lineColor(Color(getCssColor(mapStyle.colors.train)))
        .lineWidth(12)
        .line(Line(pt0, pt1))
        .build()

      blueMap.getWorld(getDefault(car.leading?.dimension, "minecraft:overworld")).ifPresent { world ->
        for (map in world.maps) {
          val markerSet = getMarkerSet(map, BLUEMAP_TRAIN_ID, BLUEMAP_TRAIN_LABEL)
          markerSet.markers[BLUEMAP_TRAIN_ID + "-" + markerSet.markers.size] = marker
        }
      }
    }
  }

  private fun updateSignal(blueMap: BlueMapAPI, signal: Signal) {
    val shape = Shape.createCircle(signal.location.x, signal.location.z, 1.0, 50)
    val markerBuilder = ShapeMarker
      .builder()
      .label(BLUEMAP_SIGNAL_LABEL)
      .shape(shape, signal.location.y.toFloat() + 1)
      .lineColor(Color(getCssColor(mapStyle.colors.signal.outline)))

    if (signal.forward != null) {
      when (signal.forward.state) {
        SignalBlockEntity.SignalState.RED ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.red)))

        SignalBlockEntity.SignalState.YELLOW ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.yellow)))

        SignalBlockEntity.SignalState.GREEN ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.green)))

        else -> {}
      }
    } else if (signal.reverse != null) {
      when (signal.reverse.state) {
        SignalBlockEntity.SignalState.RED ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.red)))

        SignalBlockEntity.SignalState.YELLOW ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.yellow)))

        SignalBlockEntity.SignalState.GREEN ->
          markerBuilder.fillColor(Color(getCssColor(mapStyle.colors.signal.green)))

        else -> {}
      }
    }

    blueMap.getWorld(signal.dimension).ifPresent { world ->
      for (map in world.maps) {
        val markerSet = getMarkerSet(map, BLUEMAP_SIGNAL_ID, BLUEMAP_SIGNAL_LABEL)
        markerSet.markers[BLUEMAP_SIGNAL_ID + "-" + markerSet.markers.size] = markerBuilder.build()
      }
    }
  }

  fun update() {
    BlueMapAPI.getInstance().ifPresent { blueMap ->
      blueMap.maps.forEach { map ->
        map.markerSets.remove(BLUEMAP_TRACK_ID)
        map.markerSets.remove(BLUEMAP_STATION_ID)
        map.markerSets.remove(BLUEMAP_TRAIN_ID)
        map.markerSets.remove(BLUEMAP_SIGNAL_ID)
      }

      updateTracks(blueMap, TrackMap.network.tracks)

      for (station in TrackMap.network.stations) {
        updateStation(blueMap, station)
      }

      for (train in TrackMap.trains.trains) {
        updateTrain(blueMap, train)
      }

      for (signal in TrackMap.signals.signals) {
        updateSignal(blueMap, signal)
      }
    }
  }
}