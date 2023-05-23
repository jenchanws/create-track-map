class LayerManager {
  constructor(map) {
    this.layers = new Map()
    this.contentLayers = {
      tracks: L.layerGroup([]),
      blocks: L.layerGroup([]),
      signals: L.layerGroup([]),
      portals: L.layerGroup([]),
      stations: L.layerGroup([]),
      trains: L.layerGroup([]),
    }
    this.map = map
    this.labels = new Map()
    this.layerConfigs = null
    this.control = L.control.layers([], []).addTo(map)
  }

  setLayerConfig(cfg) {
    this.layerConfigs = cfg

    Object.keys(cfg).forEach((key) => {
      this.contentLayers[key].min_zoom = cfg[key].minZoom
      this.contentLayers[key].max_zoom = cfg[key].maxZoom
      this.control.addOverlay(this.contentLayers[key], cfg[key].label)
    })
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
      let layerGroup = {
        layer,
        tracks: L.layerGroup([]).addTo(layer),
        blocks: L.layerGroup([]).addTo(layer),
        signals: L.layerGroup([]).addTo(layer),
        portals: L.layerGroup([]).addTo(layer),
        stations: L.layerGroup([]).addTo(layer),
        trains: L.layerGroup([]).addTo(layer),
      }
      this.layers.set(name, layerGroup)
      this.control.addBaseLayer(layer, this.labels.get(name) || name)
    }
    return this.layers.get(name)
  }

  _hideDimension(dim) {
    this.map.removeLayer(this.dimension(dim).layer)

    // Object.keys(this.contentLayers).forEach((key) => {
    //   this.dimension(dim)[key].addTo(this.dimension(dim).layer)
    // })
  }

  _showDimension(dim) {
    this.map.addLayer(this.dimension(dim).layer)

    // Object.keys(this.contentLayers).forEach((key) => {
    //   if (!this.map.hasLayer(this.contentLayers[key])) {
    //     this.dimension(dim).layer.removeLayer(this.dimension(dim)[key])
    //   }
    // })
  }

  switchToDimension(dim) {
    this.layers.forEach((l) => this._hideDimension(l))
    this._showDimension(dim)
  }

  switchDimensions(from, to) {
    this._hideDimension(from)
    this._showDimension(to)
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
