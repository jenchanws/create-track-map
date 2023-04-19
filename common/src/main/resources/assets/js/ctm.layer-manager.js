class LayerManager {
  constructor(map) {
    this.layers = new Map()
    this.map = map
    this.labels = new Map()
    this.control = L.control.layers().addTo(map)
  }

  setDimensionLabels(obj) {
    Object.keys(obj).forEach((dim) => {
      let label = obj[dim].label
      if (!!label) {
        this.labels.set(dim, label)
      }
    })
  }

  dimension(name) {
    if (!this.layers.has(name)) {
      let layer = L.layerGroup([])
      this.layers.set(name, {
        layer,
        tracks: L.layerGroup([]).addTo(layer),
        blocks: L.layerGroup([]).addTo(layer),
        signals: L.layerGroup([]).addTo(layer),
        portals: L.layerGroup([]).addTo(layer),
        stations: L.layerGroup([]).addTo(layer),
        trains: L.layerGroup([]).addTo(layer),
      })
      this.control.addBaseLayer(layer, this.labels.get(name) || name)
    }
    return this.layers.get(name)
  }

  switchToDimension(dim) {
    this.layers.forEach((v) => this.map.removeLayer(v.layer))
    this.map.addLayer(this.dimension(dim).layer)
  }

  switchDimensions(from, to) {
    this.map.removeLayer(this.dimension(from).layer)
    this.map.addLayer(this.dimension(to).layer)
  }

  _clearLayers(key) {
    Array.from(this.layers.values()).forEach((obj) => obj[key].clearLayers())
  }

  clearTracks() {
    this._clearLayers("tracks")
  }
  clearBlocks() {
    this._clearLayers("blocks")
  }
  clearSignals() {
    this._clearLayers("signals")
  }
  clearPortals() {
    this._clearLayers("portals")
  }
  clearStations() {
    this._clearLayers("stations")
  }
  clearTrains() {
    this._clearLayers("trains")
  }
}
