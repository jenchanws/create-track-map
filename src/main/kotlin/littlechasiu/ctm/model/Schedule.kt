package littlechasiu.ctm.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateScheduleInstruction(
  val destination: String?,
  val newTitle: String?,
  val newThrottle: String?,
)

@Serializable
data class CreateScheduleCondition(
  val scheduledDelay: String?,
  val timeOfDay: String?,
  val fluidCargoCondition: String?,
  val itemCargoCondition: String?,
  val redstoneLink: String?,
  val playersSeated: String?,
  val cargoInactivity: String?,
  val chunkUnloaded: String?,
  val stationPowered: String?,
)

@Serializable
data class CreateScheduleEntry(
  val instruction: CreateScheduleInstruction,
  val conditions: List<List<CreateScheduleCondition>>,
)

@Serializable
data class CreateSchedule(
  val currentEntry: Int,
  val loops: Boolean,
  val entries: List<CreateScheduleEntry>,
)
