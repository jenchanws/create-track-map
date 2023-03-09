package com.littlechasiu.trackmap

import com.littlechasiu.trackmap.model.*
import com.simibubi.create.content.logistics.trains.BezierConnection
import com.simibubi.create.content.logistics.trains.TrackEdge
import com.simibubi.create.content.logistics.trains.TrackNode
import com.simibubi.create.content.logistics.trains.TrackNodeLocation
import com.simibubi.create.content.logistics.trains.entity.Carriage
import com.simibubi.create.content.logistics.trains.entity.Train
import com.simibubi.create.foundation.utility.Couple
import net.minecraft.server.level.ServerPlayer
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

val TrackNodeLocation.sendable get() = location.sendable

val TrackNode.sendable
  get() =
    Node(
      id = netId,
      dimension = Dimension.from(location.dimension)!!,
      location = location.sendable,
    )

val BezierConnection.sendable get() = path.sendable

val TrackEdge.path: Track
  get() =
    if (isTurn) turn.path
    else Line(node1.location.location, node2.location.location)

val TrackEdge.sendable
  get() =
    Edge(
      fromNode = node1.netId,
      toNode = node2.netId,
      interdimensional = isInterDimensional,
      path = path.sendable
    )

val Carriage.sendable
  get() =
    TrainCar(
      id = id,
      leading = this.leadingPoint.getPosition().sendable,
      trailing = this.trailingPoint.getPosition().sendable,
    )

val Train.ownedBy: ServerPlayer?
  get() = TrackMap.minecraft?.playerList?.getPlayer(owner)

val Train.sendable
  get() =
    CreateTrain(
      id = id,
      name = name.string,
      owner = ownedBy?.displayName?.string,
      cars = carriages.map { it.sendable }.toList(),
    )