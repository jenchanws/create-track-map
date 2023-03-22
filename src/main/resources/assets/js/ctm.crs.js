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
