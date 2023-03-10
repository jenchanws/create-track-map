package com.littlechasiu.trackmap

import com.littlechasiu.trackmap.model.Point
import com.simibubi.create.content.logistics.trains.BezierConnection
import com.simibubi.create.content.logistics.trains.TrackEdge
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.TrackEdgePoint
import net.minecraft.world.phys.Vec3
import kotlin.math.atan2
import kotlin.math.roundToInt

operator fun Vec3.plus(other: Vec3) =
  Vec3(this.x + other.x, this.y + other.y, this.z + other.z)

operator fun Vec3.times(scale: Double) =
  Vec3(this.x * scale, this.y * scale, this.z * scale)

operator fun Vec3.unaryMinus() = this * -1.0

interface Track {
  fun divideAt(position: Double): Pair<Track, Track>
  val sendable: List<Point>
}

data class Line(
  val start: Vec3,
  val end: Vec3,
): Track {
  override val sendable: List<Point>
    get() = listOf(
      start.sendable,
      end.sendable,
    )

  override fun divideAt(position: Double): Pair<Track, Track> {
    val point = start.lerp(end, position)
    return Pair(
      Line(start, point),
      Line(point, end),
    )
  }
}

fun List<Vec3>.multiLerp(position: Double): List<Vec3> {
  return windowed(2) { lst ->
    val a = lst[0]
    val b = lst[1]
    a.lerp(b, position)
  }.toList()
}

data class BezierCurve(
  val start: Vec3,
  val controlPoint1: Vec3,
  val controlPoint2: Vec3,
  val end: Vec3,
): Track {
  override val sendable: List<Point>
    get() = listOf(
      start.sendable,
      controlPoint1.sendable,
      controlPoint2.sendable,
      end.sendable,
    )

  private val points get() = listOf(start, controlPoint1, controlPoint2, end)

  override fun divideAt(position: Double): Pair<BezierCurve, BezierCurve> {
    val points1 = points.multiLerp(position)
    val points2 = points1.multiLerp(position)
    val points3 = points2.multiLerp(position)

    val cp11 = points1[0]
    val cp12 = points2[0]
    val midpoint = points3[0]
    val cp21 = points2[1]
    val cp22 = points1[2]

    return Pair(
      BezierCurve(start, cp11, cp12, midpoint),
      BezierCurve(midpoint, cp21, cp22, end),
    )
  }
}

val BezierConnection.path
  get(): BezierCurve {
    val (start, end) = starts
    val (startAxis, endAxis) = axes
    return BezierCurve(
      start,
      start + (startAxis * handleLength),
      end + (endAxis * handleLength),
      end
    )
  }

val Vec3.angle
  get() = (atan2(x, -z) * (180.0 / Math.PI)).roundToInt().toDouble()

fun TrackEdgePoint.locationOn(edge: TrackEdge): Vec3 {
  val basePos = if (isPrimary(edge.node1)) edge.length - position else position
  return edge.getPosition(basePos / edge.length)
}

fun TrackEdgePoint.angleOn(edge: TrackEdge): Double {
  val baseVec = edge.getDirectionAt(position / edge.length)
  val vec = if (isPrimary(edge.node1)) -baseVec else baseVec
  return vec.angle
}