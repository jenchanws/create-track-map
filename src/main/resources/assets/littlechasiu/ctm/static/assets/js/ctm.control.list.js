L.Control.List = L.Control.extend({
  options: {
    position: "topright",
    toggleClassName: null,
    listClassName: null,
    itemClassName: null,
    tooltip: null,
    coordsFunction: null,
    layerManager: null,
  },

  initialize(opts) {
    L.Util.setOptions(this, opts)
  },

  onAdd(map) {
    this._map = map

    const container = (this._container = document.createElement("div"))
    container.classList.add("leaflet-control", "leaflet-control-ctm")
    container.setAttribute("aria-haspopup", true)

    L.DomEvent.disableClickPropagation(container)
    L.DomEvent.disableScrollPropagation(container)

    L.DomEvent.on(
      container,
      {
        mouseenter: this._expand,
        mouseleave: this._collapse,
      },
      this
    )

    const btn = (this._button = document.createElement("a"))
    btn.classList.add(this.options.toggleClassName, "leaflet-control-toggle")
    btn.href = "#"
    btn.title = this.options.tooltip
    btn.role = "button"
    container.appendChild(btn)

    L.DomEvent.on(
      btn,
      {
        keydown(e) {
          if (e.code === "Enter") this._expand()
        },
        click(e) {
          L.DomEvent.preventDefault(e)
          this._expand()
        },
      },
      this
    )

    const list = (this._body = document.createElement("section"))
    list.classList.add("leaflet-control-body")
    container.appendChild(list)

    const listDiv = (this._list = document.createElement("div"))
    listDiv.classList.add(this.options.listClassName)
    list.appendChild(listDiv)

    return container
  },

  add(id, info) {
    if (!this._list) {
      return
    }

    const el = document.createElement("div")
    el.classList.add(this.options.itemClassName)
    el.textContent = info.name
    el.dataset.id = id
    el.dataset.coords = this.options.coordsFunction(info).join(";")

    el.addEventListener("click", (e) => {
      let [dimension, x, _, z] = e.target.dataset.coords.split(";")
      this.options.layerManager.switchToDimension(dimension)
      this._map.panTo([parseFloat(z), parseFloat(x)])
    })

    this._list.appendChild(el)
  },

  update(id, info) {
    if (!this._list) {
      return
    }

    let el = Array.from(this._list.children).filter((e) => e.dataset.id === id)[0]
    if (!!el) {
      el.textContent = info.name
      el.dataset.coords = this.options.coordsFunction(info).join(";")
    }
  },

  remove(id) {
    if (!this._list) {
      return
    }

    let el = Array.from(this._list.children).filter((e) => e.dataset.id === id)[0]
    if (!!el) {
      el.remove()
    }
  },

  reorder() {
    Array.from(this._list.children)
      .sort((a, b) => (a.textContent > b.textContent ? 1 : -1))
      .forEach((node) => this._list.appendChild(node))
  },

  _expand() {
    L.DomEvent.on(this._body, "click", L.DomEvent.preventDefault)

    this._container.classList.add("leaflet-control-expanded")
    this._body.style.height = null

    const acceptableHeight = this._map.getSize().y - (this._container.offsetTop + 50)
    if (acceptableHeight < this._body.clientHeight) {
      this._body.classList.add("leaflet-control-scrollbar")
      this._body.style.height = `${acceptableHeight}px`
    } else {
      this._body.classList.remove("leaflet-control-scrollbar")
    }

    setTimeout(() => {
      L.DomEvent.off(this._body, "click", L.DomEvent.preventDefault)
    })
  },

  _collapse() {
    this._container.classList.remove("leaflet-control-expanded")
  },
})

L.control.list = (opts) => new L.Control.List(opts)

L.control.trainList = (layerManager) =>
  L.control.list({
    toggleClassName: "leaflet-control-train-list-toggle",
    listClassName: "ctm-train-list",
    itemClassName: "train",
    tooltip: "Trains",
    coordsFunction: (t) => {
      const c = t.cars[0].leading
      return [c.dimension, c.location.x, c.location.y, c.location.z]
    },
    layerManager,
  })

L.control.stationList = (layerManager) =>
  L.control.list({
    toggleClassName: "leaflet-control-station-list-toggle",
    listClassName: "ctm-station-list",
    itemClassName: "station",
    tooltip: "Stations",
    coordsFunction: (s) => [s.dimension, s.location.x, s.location.y, s.location.z],
    layerManager,
  })
