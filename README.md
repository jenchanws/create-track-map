# Create Track Map

*This mod is a work in progress!*

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

CTM is a *server side* only mod. It runs a web server on port 3876 (not 
configurable yet) that provides the following API:

* `/static`
    * `/network`: List of all track pieces and train stations
    * `/signals`: List of all train signals, including their states 
(green, yellow, red)
    * `/trains`: List of all assembled trains, including their names and 
positions

* `/rt`: Same as above, but updates in real time with Server-Sent Events 
(SSE)

The map itself is visible at the root (by default 
`http://localhost:3876/`). The view is not configurable at this time, but 
that will come with a later version.
