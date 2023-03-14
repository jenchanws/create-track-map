# Create Track Map

_This mod is a work in progress!_

![Mod version](https://img.shields.io/modrinth/v/gxoNIjg6)
![Minecraft versions](https://img.shields.io/modrinth/game-versions/gxoNIjg6)
![Download count](https://img.shields.io/modrinth/dt/gxoNIjg6)

![Requires Fabric 
API](https://cdn.sammdot.ca/img/modbadges/requires-fabric-api.png)
![Requires Fabric 
Kotlin](https://cdn.sammdot.ca/img/modbadges/requires-fabric-kotlin.png)

A Fabric mod that displays a track map of Create trains in your world,
including all tracks, signals, stations, and trains. The signals and
trains are updated in (practically) real time.

![Example track 
map](https://cdn.modrinth.com/data/gxoNIjg6/images/42ed31bbb88df37f393a6f8a284cc26eb9c64886.png)

### Usage

CTM is a _server side_ only mod. It runs a web server, on port 3876 by default, that provides the following API:

-   `/static`

    -   `/network`: List of all track pieces and train stations
    -   `/signals`: List of all train signals, including their states
        (green, yellow, red)
    -   `/trains`: List of all assembled trains, including their names and
        positions

-   `/rt`: Same as above, but updates in real time with Server-Sent Events
    (SSE)

The map itself is visible at the root (by default
`http://localhost:3876/`). The view is not configurable at this time, but
that will come with a later version.

### Configuration

CTM's config options can be found at `create-track-map.json` in your server's config directory. It is automatically created at startup if it doesn't exist.

The following options are available:

```js
{
  // How long to wait between track data updates.
  "watch_interval_seconds": 0.5,
  // The port the internal web server listens on.
  "server_port": 3876
}
```

Reload the config without restarting the server by running `/ctm reload` (operator permissions required).
