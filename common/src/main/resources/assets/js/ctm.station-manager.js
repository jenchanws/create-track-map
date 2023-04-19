class StationManager {
  constructor(map, layerManager) {
    this.stations = new Set()
    this.map = map
    this.control = L.control.stationList(layerManager).addTo(map)
  }

  update(stations) {
    const thisStns = new Set()

    stations.forEach((s) => {
      thisStns.add(s.id)
      if (this.stations.has(s.id)) {
        this.control.update(s.id, s)
      } else {
        this.stations.add(s.id)
        this.control.add(s.id, s)
      }
    })

    this.stations.forEach((s) => {
      if (!thisStns.has(s)) {
        this.stations.delete(s)
        this.control.remove(s.id)
      }
    })

    this.control.reorder()
  }
}
