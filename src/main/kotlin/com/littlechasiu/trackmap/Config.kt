package com.littlechasiu.trackmap

import com.google.gson.annotations.SerializedName

data class Config(
  @SerializedName("watch_interval_seconds")
  val watchIntervalSeconds: Double = 0.5,
  @SerializedName("server_port")
  val serverPort: Int = 3876,
)