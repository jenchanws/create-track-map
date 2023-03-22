package com.littlechasiu.trackmap

import com.littlechasiu.trackmap.model.*
import com.simibubi.create.content.logistics.trains.TrackEdge
import com.simibubi.create.content.logistics.trains.TrackNode
import com.simibubi.create.content.logistics.trains.TrackNodeLocation
import com.simibubi.create.content.logistics.trains.entity.Carriage
import com.simibubi.create.content.logistics.trains.entity.Train
import com.simibubi.create.content.logistics.trains.entity.TravellingPoint
import com.simibubi.create.foundation.utility.Couple
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

val TrackNode.sendable
  get() =
    Node(
      id = netId,
      dimension = location.dimension.string,
      location = location.sendable,
    )

val TrackEdge.path: Track
  get() =
    if (isTurn) turn.path
    else Line(node1.location.location, node2.location.location)

val TrackNode.dimensionLocation: DimensionLocation get() =
  DimensionLocation(location.dimension.string, location.sendable)

val TrackEdge.sendable
  get() =
    if (isInterDimensional)
      Portal(
        from = node1.dimensionLocation,
        to = node2.dimensionLocation)
    else
      Edge(
        fromNode = node1.netId,
        toNode = node2.netId,
        dimension = node1.location.dimension.string,
        path = path.sendable
      )

val TravellingPoint.sendable
  get() =
    DimensionLocation(
      dimension = edge.node1.location.dimension.string,
      location = getPosition().sendable,
    )

val Carriage.sendable
  get() =
    TrainCar(
      id = id,
      leading = leadingPoint.sendable,
      trailing = trailingPoint.sendable,
      portal = this.train.occupiedSignalBlocks.keys.map {
        TrackMap.watcher.portalsInBlock(it)
      }.flatten().firstOrNull {
        it.from.dimension == leadingPoint.node1.location.dimension.string &&
          it.to.dimension == trailingPoint.node1.location.dimension.string
      },
    )

val Train.sendable
  get() =
    CreateTrain(
      id = id,
      name = name.string,
      owner = null,
      cars = carriages.map { it.sendable }.toList(),
    )
