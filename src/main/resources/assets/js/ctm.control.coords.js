L.Control.Coords = L.Control.extend({
  options: {
    position: "bottomright",
  },

  initialize(opts) {
    L.Util.setOptions(this, opts)
  },

  _createElement() {
    let el = document.createElement("div")
    el.classList.add("coords-control")

    let curEl = document.createElement("div")
    curEl.classList.add("cursor-coords")
    let curIcon = document.createElement("object")
    curIcon.classList.add("icon")
    curIcon.data = "assets/icons/cursor.svg"
    let curX = document.createElement("span")
    let curZ = document.createElement("span")
    curX.classList.add("cursor-x")
    curZ.classList.add("cursor-z")
    curEl.appendChild(curIcon)
    curEl.appendChild(curX)
    curEl.appendChild(curZ)
    el.appendChild(curEl)

    let ctrEl = document.createElement("div")
    let ctrIcon = document.createElement("object")
    ctrIcon.classList.add("icon")
    ctrIcon.data = "assets/icons/center.svg"
    let ctrX = document.createElement("span")
    let ctrZ = document.createElement("span")
    ctrX.classList.add("center-x")
    ctrZ.classList.add("center-z")
    ctrEl.appendChild(ctrIcon)
    ctrEl.appendChild(ctrX)
    ctrEl.appendChild(ctrZ)
    el.appendChild(ctrEl)

    return el
  },

  onAdd(map) {
    let el = this._createElement()

    this.centerX = el.getElementsByClassName("center-x")[0]
    this.centerZ = el.getElementsByClassName("center-z")[0]
    this.cursorX = el.getElementsByClassName("cursor-x")[0]
    this.cursorZ = el.getElementsByClassName("cursor-z")[0]

    this.cursor = el.getElementsByClassName("cursor-coords")[0]

    map.on("zoom", this._updateCenterCoords, this)
    map.on("move", this._updateCenterCoords, this)
    map.on("mouseover", this._showCursorCoords, this)
    map.on("mousemove", this._updateCursorCoords, this)
    map.on("mouseout", this._clearCursorCoords, this)

    this._updateCenterCoords()
    this._clearCursorCoords()

    return el
  },

  onRemove(map) {
    document.getElementById("ctm-coords-control").remove()

    map.off("zoom", this._updateCenterCoords, this)
    map.off("move", this._updateCenterCoords, this)
    map.off("mouseover", this._showCursorCoords, this)
    map.off("mousemove", this._updateCursorCoords, this)
    map.off("mouseout", this._clearCursorCoords, this)
  },

  _updateCenterCoords() {
    const coords = map.getCenter()
    const x = Math.round(coords.lng)
    const z = Math.round(coords.lat)

    this.centerX.textContent = x.toString()
    this.centerZ.textContent = z.toString()
  },

  _updateCursorCoords(event) {
    const coords = event.latlng
    const x = Math.round(coords.lng)
    const z = Math.round(coords.lat)

    this.cursor.style.display = "block"
    this.cursorX.textContent = x.toString()
    this.cursorZ.textContent = z.toString()
  },

  _showCursorCoords() {
    this.cursor.style.display = "block"
  },

  _clearCursorCoords() {
    this.cursor.style.display = "none"
    this.cursorX.textContent = "--"
    this.cursorZ.textContent = "--"
  },
})

L.control.coords = (opts) => new L.Control.Coords(opts)
