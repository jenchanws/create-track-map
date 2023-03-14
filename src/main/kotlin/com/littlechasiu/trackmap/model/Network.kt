package com.littlechasiu.trackmap.model

import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalBlock.SignalType
import com.simibubi.create.content.logistics.trains.management.edgePoint.signal.SignalTileEntity.SignalState
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import java.util.*

object UUIDSerializer : KSerializer<UUID> {
  override val descriptor =
    PrimitiveSerialDescriptor("UUID", PrimitiveKind.STRING)

  override fun deserialize(decoder: Decoder): UUID {
    return UUID.fromString(decoder.decodeString())
  }

  override fun serialize(encoder: Encoder, value: UUID) {
    encoder.encodeString(value.toString())
  }
}

enum class Dimension {
  OVERWORLD,
  NETHER,
  END;

  companion object {
    fun from(loc: ResourceKey<Level>) = when (loc.location().path) {
      "overworld" -> OVERWORLD
      "the_nether" -> NETHER
      "the_end" -> END
      else -> null
    }
  }
}

@Serializable
data class Point(
  val x: Double,
  val y: Double,
  val z: Double,
)

@Serializable
data class Node(
  val id: Int,
  val dimension: Dimension,
  val location: Point,
)

@Serializable
data class Path(
  val start: Point,
  val firstControlPoint: Point,
  val secondControlPoint: Point,
  val end: Point,
)

@Serializable
data class Edge(
  val fromNode: Int,
  val toNode: Int,
  val interdimensional: Boolean,
  val path: List<Point>?,
)

@Serializable
data class Station(
  @Serializable(with = UUIDSerializer::class)
  val id: UUID,
  val name: String,
  val dimension: Dimension,
  val location: Point,
  val angle: Double,
  val assembling: Boolean,
)

@Serializable
data class SignalSide(
  val type: SignalType,
  val state: SignalState,
  val angle: Double,
  @Serializable(with = UUIDSerializer::class)
  val block: UUID?,
)

@Serializable
data class Network(
  val nodes: List<Node>,
  val edges: List<Edge>,
  val stations: List<Station>,
)

@Serializable
data class Signal(
  @Serializable(with = UUIDSerializer::class)
  val id: UUID,
  val dimension: Dimension,
  val location: Point,
  val forward: SignalSide?,
  val reverse: SignalSide?,
)

@Serializable
data class SignalStatus(
  val signals: List<Signal>,
)

@Serializable
data class Block(
  @Serializable(with = UUIDSerializer::class)
  val id: UUID,
  val occupied: Boolean,
  val reserved: Boolean,
  val segments: List<List<Point>>,
)

@Serializable
data class BlockStatus(
  val blocks: List<Block>
)

@Serializable
data class TrainCar(
  val id: Int,
  val leading: Point,
  val trailing: Point,
)

@Serializable
data class CreateTrain(
  @Serializable(with = UUIDSerializer::class)
  val id: UUID,
  val name: String,
  val owner: String?,
  val cars: List<TrainCar>,
)

@Serializable
data class TrainStatus(
  val trains: List<CreateTrain>,
)