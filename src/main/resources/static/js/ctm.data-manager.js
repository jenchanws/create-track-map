class DataManager {
  constructor() {
    this.networkStream = new EventSource("api/network.rt")
    this.blockStatusStream = new EventSource("api/blocks.rt")
    this.signalStatusStream = new EventSource("api/signals.rt")
    this.trainStatusStream = new EventSource("api/trains.rt")
  }

  onTrackStatus(fn) {
    this.networkStream.onmessage = (e) => fn(JSON.parse(e.data))
  }
  onBlockStatus(fn) {
    this.blockStatusStream.onmessage = (e) => fn(JSON.parse(e.data))
  }
  onSignalStatus(fn) {
    this.signalStatusStream.onmessage = (e) => fn(JSON.parse(e.data))
  }
  onTrainStatus(fn) {
    this.trainStatusStream.onmessage = (e) => fn(JSON.parse(e.data))
  }
}
