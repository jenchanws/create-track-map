<!-- modrinth_exclude.start -->
![Create Track Map](https://raw.githubusercontent.com/gist/jenchanws/44291eb9435be4fc46be1b8661ae4406/raw/5498836314b52ffab4ab2170c5b7be9006a365e2/ctm.svg)
<!-- modrinth_exclude.end -->
# Create Track Map

![Mod version](https://img.shields.io/modrinth/v/gxoNIjg6)
![Minecraft versions](https://img.shields.io/badge/minecraft-1.18.2%20•%201.19.2-blue)
![Download count](https://img.shields.io/modrinth/dt/gxoNIjg6)

![Available for Fabric](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/fabric.svg)
[![Requires Create ≥0.5](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/create-0.5.svg)](https://modrinth.com/mod/create-fabric)
[![Requires Fabric API](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/1fc7a8dfba76460bedba7fa0b5516b917815e39c/fabric-api.svg)](https://modrinth.com/mod/fabric-api)
[![Requires Create ≥0.5](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/1fc7a8dfba76460bedba7fa0b5516b917815e39c/fabric-kotlin.svg)](https://modrinth.com/mod/fabric-language-kotlin)

![Available for Forge](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/forge.svg)
[![Requires Create ≥0.5](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/create-0.5.svg)](https://modrinth.com/mod/create)
[![Requires Create ≥0.5](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/522a26e2c91cbafab79ddbbc6d942f1720249a8c/forge-kotlin.svg)](https://modrinth.com/mod/kotlin-for-forge)

[![Available on GitHub](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/github.svg)](https://github.com/jenchanws/create-track-map)
[![Available on Modrinth](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/modrinth.svg)](https://modrinth.com/mod/create-track-map)
[![Find me on Discord](https://raw.githubusercontent.com/gist/jenchanws/842eee8428e1e0aec20de4594878156a/raw/0dbefc2fcbec362d14f1689acb807183ceffdbe1/discord.svg)](https://smp.littlechasiu.com/discord)

A multi-loader mod that displays a track map of Create trains in your world,
including all tracks, signals, stations, and trains. The signals and
trains are updated in (practically) real time.

![Example track 
map](https://cdn.modrinth.com/data/gxoNIjg6/images/8aa58af4ca9cc459a84ce492770a92e358cd2714.gif)

### Usage

CTM is intended to be a server side mod, but can also run in single-player worlds and LAN servers. It runs a web server, on port 3876 by default, that provides the following API:

- `/api/network`, `/api/network.rt`: List of all track pieces and train stations
- `/api/signals`, `/api/signals.rt`: List of all train signals, including their states
  (green, yellow, red)
- `/api/blocks`, `/api/blocks.rt`: List of all signal control blocks, and whether they are occupied or reserved by a train
- `/api/trains`, `/api/trains.rt`: List of all assembled trains, including their names and
  positions
- `/api/style.css`: CSS style sheet generated from configured colors and fonts
- `/api/config.json`: Map configuration

The `.rt` versions update in real time with Server-Sent Events (SSE). If using a proxy to serve the map, make sure to configure it to let Server-Sent Events through.

The map itself is visible at the root (by default `http://localhost:3876/`).

### Configuration

CTM's config options can be found at `create-track-map.json` in your server's config directory. It is automatically created at startup if it doesn't exist.

The following options are available:

```js
{
  // Whether to actually start the watcher and the server.
  "enable": false,

  // How long to wait between track data updates.
  "watch_interval_seconds": 0.5,
  // The port the internal web server listens on.
  "server_port": 3876,

  "map_style": {
    // Font to use for the map's UI. Must be a valid CSS font stack.
    "font": "ui-monospace, \"JetBrains Mono\", monospace",
    // Colors for individual components of the map. Must be valid CSS colors.
    // Any CSS color format will work, such as named colors and rgb().
    "colors": {
      "background": "#888",
      "track": {
        "occupied": "red",
        "reserved": "pink",
        "free": "white"
      },
      "signal": {
        "green": "#71db51",
        "yellow": "#ffd15c",
        "red": "#ff5f5c",
        "outline": "black"
      },
      "portal": {
        "primary": "purple",
        "outline": "white"
      },
      "station": {
        "primary": "white",
        "outline": "black"
      },
      "train": "cyan"
    }
  },

  "map_view": {
    "initial_dimension": "minecraft:overworld",
    "initial_position": { "x": 0, "z": 0 },

    // Zoom levels must be integers, but may be negative.
    // Each zoom level is twice as big as the previous.
    // 0 is a decent minimum but may be impractical for large networks.
    // 3 is the sensible default for viewing double-tracked networks.
    "initial_zoom": 3,
    "min_zoom": 0,
    "max_zoom": 4,

    // Whether a zoom control should be visible on the screen.
    "zoom_controls": true
  },

  "dimensions": {
    // Dimension names must be namespaced.
    "minecraft:overworld": {
      // Label that shows up in the layer switcher.
      "label": "Overworld"
    },
    "minecraft:the_nether": {
      "label": "Nether"
    },
    "minecraft:the_end": {
      "label": "End"
    }
  }
}
```

Reload the config without restarting the server by running `/ctm reload` (operator permissions required).
