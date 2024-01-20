package littlechasiu.ctm

import com.simibubi.create.content.trains.display.GlobalTrainDisplayData
import com.simibubi.create.content.trains.entity.Carriage
import com.simibubi.create.content.trains.entity.Train
import com.simibubi.create.content.trains.entity.TravellingPoint
import com.simibubi.create.content.trains.graph.TrackEdge
import com.simibubi.create.content.trains.graph.TrackNode
import com.simibubi.create.content.trains.graph.TrackNodeLocation
import com.simibubi.create.content.trains.schedule.ScheduleEntry
import com.simibubi.create.content.trains.schedule.ScheduleRuntime
import com.simibubi.create.content.trains.schedule.condition.FluidThresholdCondition
import com.simibubi.create.content.trains.schedule.condition.IdleCargoCondition
import com.simibubi.create.content.trains.schedule.condition.ItemThresholdCondition
import com.simibubi.create.content.trains.schedule.condition.PlayerPassengerCondition
import com.simibubi.create.content.trains.schedule.condition.RedstoneLinkCondition
import com.simibubi.create.content.trains.schedule.condition.ScheduleWaitCondition
import com.simibubi.create.content.trains.schedule.condition.ScheduledDelay
import com.simibubi.create.content.trains.schedule.condition.StationPoweredCondition
import com.simibubi.create.content.trains.schedule.condition.StationUnloadedCondition
import com.simibubi.create.content.trains.schedule.condition.TimeOfDayCondition
import com.simibubi.create.content.trains.schedule.destination.ChangeThrottleInstruction
import com.simibubi.create.content.trains.schedule.destination.ChangeTitleInstruction
import com.simibubi.create.content.trains.schedule.destination.DestinationInstruction
import com.simibubi.create.content.trains.schedule.destination.ScheduleInstruction
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
      schedule = runtime.sendable
    )

val ScheduleInstruction.sendable
  get() =
    CreateScheduleInstruction(
      destination = if (this is DestinationInstruction) filter else null,
      newTitle = if (this is ChangeTitleInstruction) scheduleTitle else null,
      newThrottle = if (this is ChangeThrottleInstruction) (throttle * 100).toString() + "%" else null,
    )

val ScheduleWaitCondition.sendable
  get() =
    CreateScheduleCondition(
      scheduledDelay =
        if (this is ScheduledDelay) (value.toString() + unit.suffix)
        else null,
      timeOfDay =
        if (this is TimeOfDayCondition) summary.second.string + " every " + (rotation / 1000) + " hour(s)"
        else null,
      fluidCargoCondition =
        if (this is FluidThresholdCondition) getItem(0).displayName.string + " " + operator.ordinal + " " + threshold + " buckets"
        else null,
      itemCargoCondition =
        if (this is ItemThresholdCondition) getItem(0).displayName.string + " " + operator.ordinal + " " + threshold
        else null,
      redstoneLink =
        if (this is RedstoneLinkCondition)
          "Frequency: " + freq.get(true).stack.displayName.string + "; " + freq.get(false).stack.displayName.string +
              (if (lowActivation()) " is not powered" else " is powered")
        else null,
      playersSeated =
        if (this is PlayerPassengerCondition) target.toString() + (if (canOvershoot()) " or above" else " exactly")
        else null,
      cargoInactivity =
        if (this is IdleCargoCondition) (value.toString() + unit.suffix)
        else null,
      chunkUnloaded =
        if (this is StationUnloadedCondition) "No info"
        else null,
      stationPowered =
        if (this is StationPoweredCondition) "No info"
        else null,
    )

val ScheduleEntry.sendable
  get() =
    CreateScheduleEntry(
      instruction = instruction.sendable,
      conditions = conditions.map { a -> a.map { b -> b.sendable } },
    )

val ScheduleRuntime.sendable
  get() =
    if (schedule == null)
      null
    else
      CreateSchedule(
        currentEntry = currentEntry,
        loops = schedule.cyclic,
        entries = schedule.entries.map { it.sendable })

val GlobalTrainDisplayData.TrainDeparturePrediction.sendable
  get() =
    StationSummaryEntry(
      scheduleTitle = scheduleTitle.string,
      destination = destination,
      trainName = train.name.string,
      ticks = ticks,
    )
