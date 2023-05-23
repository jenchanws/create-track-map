class LayerManager {
  constructor(map) {
    this.map = map

    this.layerConfigs = {}
    this.labels = {}
    this.currentDimension = null
    this.dimensionLayers = {}
    this.contentLayers = {
      tracks: L.layerGroup([]).addTo(map),
      blocks: L.layerGroup([]).addTo(map),
      signals: L.layerGroup([]).addTo(map),
      portals: L.layerGroup([]).addTo(map),
      stations: L.layerGroup([]).addTo(map),
      trains: L.layerGroup([]).addTo(map),
    }

    this.actualLayers = {}

    this.control = L.control.layers([], []).addTo(map)

    map.on("baselayerchange", this._onDimensionChange, this)
    map.on("overlayadd", this._onOverlayAdd, this)
    map.on("overlayremove", this._onOverlayRemove, this)
    map.on("zoomend", this._onZoomLevelChange, this)
  }

  setLayerConfig(cfg) {
    Object.keys(cfg).forEach((key) => {
      this.layerConfigs[key] = {
        minZoom: cfg[key].min_zoom,
        maxZoom: cfg[key].max_zoom,
      }
      let typeLayer = this.contentLayers[key]
      typeLayer.name = key
      this.control.addOverlay(typeLayer, cfg[key].label)
    })
  }

  setDimensionLabels(obj) {
    Object.keys(obj).forEach((dim) => {
      let label = obj[dim].label
      if (!!label) {
        this.labels[dim] = label
      }
    })
  }

  dimension(name) {
    if (!this.dimensionLayers.hasOwnProperty(name)) {
      let layerGroup = {
        tracks: L.layerGroup([]),
        blocks: L.layerGroup([]),
        signals: L.layerGroup([]),
        portals: L.layerGroup([]),
        stations: L.layerGroup([]),
        trains: L.layerGroup([]),
      }
      let layer = (this.dimensionLayers[name] = L.layerGroup([]))
      layer.name = name
      this.control.addBaseLayer(layer, this.labels[name] || name)
      this.actualLayers[name] = layerGroup
    }
    return this.actualLayers[name]
  }

  layer(dim, type) {
    return this.dimension(dim)[type]
  }

  _hideDimension(dim) {
    let layers = this.dimension(dim)
    this.map.removeLayer(this.dimensionLayers[dim])
    Object.values(layers).forEach((layer) => {
      this.map.removeLayer(layer)
    })
  }

  _showDimension(dim) {
    let layers = this.dimension(dim)
    Object.entries(layers).forEach(([key, layer]) => {
      if (this.map.hasLayer(this.contentLayers[key])) {
        this.map.addLayer(layer)
      }
    })
    this.currentDimension = dim
  }

  switchToDimension(dim) {
    Object.keys(this.dimensionLayers).forEach((l) => this._hideDimension(l))
    this._showDimension(dim)
    this.dimensionLayers[dim].addTo(map)
  }

  switchDimensions(from, to) {
    this._hideDimension(from)
    this._showDimension(to)
    this.dimensionLayers[dim].addTo(map)
  }

  _onDimensionChange({ layer }) {
    Object.keys(this.dimensionLayers).forEach((l) => this._hideDimension(l))
    this._showDimension(layer.name)
  }

  _onOverlayAdd({ layer }) {
    let zoom = this.map.getZoom()
    let layerConfig = this.layerConfigs[layer.name]
    let dimLayer = this.dimension(this.currentDimension)[layer.name]
    if (zoom >= layerConfig.minZoom && zoom <= layerConfig.maxZoom) {
      dimLayer.addTo(this.map)
    }
  }

  _onOverlayRemove({ layer }) {
    this.map.removeLayer(this.dimension(this.currentDimension)[layer.name])
  }

  _onZoomLevelChange() {
    let zoom = this.map.getZoom()

    Object.entries(this.contentLayers).forEach(([name, layer]) => {
      let layerConfig = this.layerConfigs[name]
      let dimLayer = this.dimension(this.currentDimension)[name]

      if (zoom < layerConfig.minZoom || zoom > layerConfig.maxZoom) {
        this.map.removeLayer(dimLayer)
      } else if (this.map.hasLayer(layer) && !this.map.hasLayer(dimLayer)) {
        this.map.addLayer(dimLayer)
      }
    })
  }

  _clearLayers(key) {
    Array.from(Object.values(this.actualLayers)).forEach((obj) => obj[key].clearLayers())
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
