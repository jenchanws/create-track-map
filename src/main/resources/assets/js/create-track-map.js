L.CRS.Minecraft = L.Util.extend(L.CRS.Simple, {
  transformation: new L.Transformation(1, 0, 1, 0),
})

let xz = (x, z) => {
  if (typeof x === "number") {
    return [z, x]
  } else {
    return [x.z, x.x]
  }
}

let map = L.map("map", {
  crs: L.CRS.Minecraft,
  zoomControl: true,
})

fetch("api/config.json")
  .then((resp) => resp.json())
  .then((cfg) => {
    const { view } = cfg
    const {
      initial_position,
      initial_zoom,
      max_zoom,
      min_zoom,
      zoom_controls,
    } = view

    map.options.minZoom = min_zoom
    map.options.maxZoom = max_zoom

    const { x: initialX, z: initialZ } = initial_position
    map.setView([initialZ, initialX], initial_zoom)

    if (!zoom_controls) {
      map.zoomControl.remove()
    }
  })

const signalIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="16"
      height="16"
      viewBox="0 0 64 64"
    >
      <path class="frame" d="M44 0c11.046 0 20 8.954 20 20 0 10.37-7.893 18.897-17.999 19.901L46 60h18v4H24v-4h18V39.901C31.893 38.898 24 30.371 24 20 24 8.954 32.954 0 44 0Zm0 4c-8.837 0-16 7.163-16 16s7.163 16 16 16 16-7.163 16-16S52.837 4 44 4Z"/>
      <path class="light" d="M44 4c-8.837 0-16 7.163-16 16s7.163 16 16 16 16-7.163 16-16S52.837 4 44 4Z"/>
    </svg>`,
  className: "signal-icon",
  iconSize: [16, 16],
  iconAnchor: [2, 16],
})

const stationIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="18"
      height="18"
      viewBox="0 0 64 64"
    >
      <path class="outline" d="M56 0a8 8 0 0 1 8 8v48a8 8 0 0 1-8 8H8a8 8 0 0 1-8-8V8a8 8 0 0 1 8-8h48Z"/>
      <path class="fill" d="M56 4H8a4 4 0 0 0-3.995 3.8L4 8v48a4 4 0 0 0 3.8 3.995L8 60h48a4 4 0 0 0 3.995-3.8L60 56V8a4 4 0 0 0-3.8-3.995L56 4Z"/>
      <path class="outline" d="m33.287 16.466.127.116 14 14 .117.128a2 2 0 0 1 0 2.574l-.117.127-.127.117a2 2 0 0 1-2.574 0l-.127-.117-10.587-10.586L34 49.997l-.005.149a2 2 0 0 1-3.99 0l-.005-.15V22.826L19.413 33.411l-.127.117a2 2 0 0 1-2.818-2.818l.117-.128 14-14 .127-.116a2 2 0 0 1 2.574 0ZM50.36 10l.149.005a2 2 0 0 1 .003 3.99l-.149.005-36 .033-.15-.005a2 2 0 0 1-.003-3.99l.15-.005 36-.033Z"/>
    </svg>`,
  className: "station-icon",
  iconSize: [18, 18],
  iconAnchor: [9, 9],
})

const portalIcon = L.divIcon({
  html: `
    <svg
      xmlns="http://www.w3.org/2000/svg"
      width="24"
      height="24"
      viewBox="0 0 64 64"
    >
      <path class="outline" d="M56 0a8 8 0 0 1 8 8v48a8 8 0 0 1-8 8H8a8 8 0 0 1-8-8V8a8 8 0 0 1 8-8h48Z"/>
      <path class="fill" d="M56 4H8a4 4 0 0 0-3.995 3.8L4 8v48a4 4 0 0 0 3.8 3.995L8 60h48a4 4 0 0 0 3.995-3.8L60 56V8a4 4 0 0 0-3.8-3.995L56 4Z"/>
      <path class="outline" d="m29.287 18.469.127.117 12 12a2 2 0 0 1 .17 2.635l-.114.136-12 13a2 2 0 0 1-3.051-2.582l.111-.132L35.431 34H10a2 2 0 0 1-.15-3.995L10 30h25.171l-8.585-8.586a2 2 0 0 1-.117-2.701l.117-.127a2 2 0 0 1 2.701-.117Z"/>
      <path class="outline" d="M49 9a6 6 0 0 1 5.996 5.775L55 15v34a6 6 0 0 1-5.775 5.996L49 55h-8a6 6 0 0 1-5.996-5.775L35 49v-3a2 2 0 0 1 3.995-.15L39 46v3a2 2 0 0 0 1.85 1.995L41 51h8a2 2 0 0 0 1.995-1.85L51 49V15a2 2 0 0 0-1.85-1.995L49 13h-8a2 2 0 0 0-1.995 1.85L39 15v4a2 2 0 0 1-3.995.15L35 19v-4a6 6 0 0 1 5.775-5.996L41 9h8Z"/>
    </svg>`,
  className: "portal-icon",
  iconSize: [24, 24],
})

map.createPane("tracks")
map.createPane("signal-blocks")
map.createPane("trains")
map.createPane("signals")
map.createPane("stations")
map.getPane("tracks").style.zIndex = 300
map.getPane("signal-blocks").style.zIndex = 500
map.getPane("trains").style.zIndex = 700
map.getPane("signals").style.zIndex = 800
map.getPane("stations").style.zIndex = 800

map.getPane("tooltipPane").style.zIndex = 1000

let edgeLayer = L.layerGroup([], { pane: "tracks" }).addTo(map)
let stationLayer = L.layerGroup([], { pane: "stations" }).addTo(map)
let portalLayer = L.layerGroup([], { pane: "stations" }).addTo(map)
let signalLayer = L.layerGroup([], { pane: "signals" }).addTo(map)
let blockLayer = L.layerGroup([], { pane: "signal-blocks" }).addTo(map)
let trainLayer = L.layerGroup([], { pane: "trains" }).addTo(map)

const networkStream = new EventSource("api/network.rt")
const blockStatusStream = new EventSource("api/blocks.rt")
const signalStatusStream = new EventSource("api/signals.rt")
const trainStatusStream = new EventSource("api/trains.rt")

networkStream.onmessage = (e) => {
  let { edges, stations } = JSON.parse(e.data)

  edgeLayer.clearLayers()
  edges.forEach((edge) => {
    if (edge.path !== null && !edge.interdimensional) {
      const path = edge.path
      if (path.length === 4) {
        L.curve(
          ["M", xz(path[0]), "C", xz(path[1]), xz(path[2]), xz(path[3])],
          {
            className: "track",
            interactive: false,
            pane: "tracks",
          }
        ).addTo(edgeLayer)
      } else if (path.length === 2) {
        L.polyline([xz(path[0]), xz(path[1])], {
          className: "track",
          interactive: false,
          pane: "tracks",
        }).addTo(edgeLayer)
      }
    }
  })

  stationLayer.clearLayers()
  stations.forEach((stn) => {
    L.marker(xz(stn.location), {
      icon: stationIcon,
      rotationAngle: stn.angle,
      pane: "stations",
    })
      .bindTooltip(stn.name, {
        className: "station-name",
        direction: "top",
        offset: L.point(0, -12),
        opacity: 0.7,
      })
      .addTo(stationLayer)
  })

  edges.forEach((edge) => {
    if (edge.interdimensional) {
      L.marker(xz(edge.path[0]), {
        icon: portalIcon,
        pane: "stations",
      })
        .on("click", (e) => map.panTo(xz(edge.path[1])))
        .addTo(portalLayer)
      L.marker(xz(edge.path[1]), {
        icon: portalIcon,
        pane: "stations",
      })
        .on("click", (e) => map.panTo(xz(edge.path[0])))
        .addTo(portalLayer)
    }
  })
}

blockStatusStream.onmessage = (e) => {
  let { blocks } = JSON.parse(e.data)

  blockLayer.clearLayers()
  blocks.forEach((block) => {
    if (!block.reserved && !block.occupied) {
      return
    }
    block.segments.forEach((path) => {
      if (path.length === 4) {
        L.curve(
          ["M", xz(path[0]), "C", xz(path[1]), xz(path[2]), xz(path[3])],
          {
            className:
              "track " +
              (block.reserved ? "reserved" : block.occupied ? "occupied" : ""),
            interactive: false,
            pane: "signal-blocks",
          }
        ).addTo(blockLayer)
      } else if (path.length === 2) {
        L.polyline([xz(path[0]), xz(path[1])], {
          className:
            "track " +
            (block.reserved ? "reserved" : block.occupied ? "occupied" : ""),
          interactive: false,
          pane: "signal-blocks",
        }).addTo(blockLayer)
      }
    })
  })
}

signalStatusStream.onmessage = (e) => {
  let { signals } = JSON.parse(e.data)

  signalLayer.clearLayers()
  signals.forEach((sig) => {
    if (!!sig.forward) {
      let marker = L.marker(xz(sig.location), {
        icon: signalIcon,
        rotationAngle: sig.forward.angle,
        interactive: false,
        pane: "signals",
      }).addTo(signalLayer)

      marker._icon.dataset.color = sig.forward.state.toLowerCase()
    }
    if (!!sig.reverse) {
      let marker = L.marker(xz(sig.location), {
        icon: signalIcon,
        rotationAngle: sig.reverse.angle,
        interactive: false,
        pane: "signals",
      }).addTo(signalLayer)

      marker._icon.dataset.color = sig.reverse.state.toLowerCase()
    }
  })
}

trainStatusStream.onmessage = (e) => {
  let { trains } = JSON.parse(e.data)

  trainLayer.clearLayers()
  trains.forEach((train) => {
    train.cars.forEach((car, i) => {
      L.polyline([xz(car.leading), xz(car.trailing)], {
        weight: 12,
        lineCap: "square",
        className: "train",
        pane: "trains",
      })
        .bindTooltip(
          train.cars.length === 1
            ? train.name
            : `${train.name} <span class="car-number">${i + 1}</span>`,
          {
            className: "train-name",
            direction: "right",
            offset: L.point(12, 0),
            opacity: 0.7,
          }
        )
        .addTo(trainLayer)
    })
  })
}
