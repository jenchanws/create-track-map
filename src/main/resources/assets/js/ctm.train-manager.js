class TrainManager {
  constructor(map, layerManager) {
    this.trains = new Set()
    this.map = map
    this.control = L.control.trainList(layerManager).addTo(map)
  }

  update(trains) {
    const thisTrains = new Set()

    trains.forEach((t) => {
      thisTrains.add(t.id)
      if (this.trains.has(t.id)) {
        this.control.update(t.id, t)
      } else {
        this.trains.add(t.id)
        this.control.add(t.id, t)
      }
    })

    this.trains.forEach((t) => {
      if (!thisTrains.has(t)) {
        this.trains.delete(t)
        this.control.remove(t.id)
      }
    })

    this.control.reorder()
  }
}
