package com.littlechasiu.trackmap

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Config @OptIn(ExperimentalSerializationApi::class) constructor(
  @SerialName("watch_interval_seconds")
  @EncodeDefault
  val watchIntervalSeconds: Double = 0.5,
  @SerialName("server_port")
  @EncodeDefault
  val serverPort: Int = 3876,
)