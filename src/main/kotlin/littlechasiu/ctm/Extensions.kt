package littlechasiu.ctm

import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.Train
import com.simibubi.create.content.trains.entity.TravellingPoint
import com.simibubi.create.content.trains.graph.TrackEdge
import com.simibubi.create.content.trains.graph.TrackNode
import com.simibubi.create.content.trains.graph.TrackNodeLocation
import com.simibubi.create.foundation.utility.Couple
import littlechasiu.ctm.model.*
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3

fun <T> MutableSet<T>.replaceWith(other: Collection<T>) {
  this.retainAll { other.contains(it) }
  this.addAll(other)
}

operator fun <T> Couple<T>.component1(): T = this.get(true)
operator fun <T> Couple<T>.component2(): T = this.get(false)

val Vec3.sendable: Point
  get() =
    Point(x = x, y = y, z = z)

val ResourceKey<Level>.string: String
  get() {
    val loc = location()
    return "${loc.namespace}:${loc.path}"
  }

val TrackNodeLocation.sendable get() = location.sendable

val TrackEdge.path: Track
  get() =
    if (isTurn) BezierCurve.from(turn, node1.location.dimension.string)
    else Line(
      node1.location.dimension.string,
      node1.location.location, node2.location.location
    )

val TrackNode.dimensionLocation: DimensionLocation
  get() =
    DimensionLocation(location.dimension.string, location.sendable)

val TrackEdge.sendable
  get() =
    if (isInterDimensional)
      Portal(
        from = node1.dimensionLocation,
        to = node2.dimensionLocation)
    else
      path.sendable

val TravellingPoint.sendable
  get() =
    if (node1 == null || edge == null) null else
    DimensionLocation(
      dimension = node1.location.dimension.string,
      location = getPosition(null).sendable,
    )

val Carriage.sendable
  get() =
    TrainCar(
      id = id,
      leading = leadingPoint?.sendable,
      trailing = trailingPoint?.sendable,
      portal = this.train.occupiedSignalBlocks.keys.map {
        TrackMap.watcher.portalsInBlock(it)
      }.flatten().firstOrNull {
        it.from.dimension == leadingPoint?.node1?.location?.dimension?.string &&
          it.to.dimension == trailingPoint?.node1?.location?.dimension?.string
      },
    )

val Train.sendable
  get() =
    CreateTrain(
      id = id,
      name = name.string,
      owner = null,
      cars = carriages.map { it.sendable }.toList(),
      backwards = speed < 0,
      stopped = speed == 0.0,
    )
