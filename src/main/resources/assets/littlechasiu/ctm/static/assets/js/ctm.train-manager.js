class TrainManager {
  constructor(map, layerManager) {
    this.trains = new Map(); // Map instead of a Set to store train objects
    this.map = map;
    this.control = L.control.trainList(layerManager).addTo(map);
  }

  update(newTrains) {
    const updatedTrains = new Set();

    newTrains.forEach((train) => {
      updatedTrains.add(train.id);

      if (this.trains.has(train.id)) {
        const existingTrain = this.trains.get(train.id);
        const marker = existingTrain.marker;

        if (train.lat !== existingTrain.lat || train.lng !== existingTrain.lng) {
          marker.animateTo([train.lat, train.lng], {
            duration: 1000, // I think the data is updated every 1 second so thr animation should be 1 second
            easing: 'linear',
          });
        }

        this.control.update(train.id, train);
      } else {
        const marker = L.Marker.movingMarker(
          [[train.lat, train.lng]],
          [],
          { duration: 0 }
        ).addTo(this.map);

        marker.bindPopup(`Train ${train.id}`);
        marker.start();

        this.trains.set(train.id, { lat: train.lat, lng: train.lng, marker });
        this.control.add(train.id, train);
      }
    });

    this.trains.forEach((existingTrain, id) => {
      if (!updatedTrains.has(id)) {
        const marker = existingTrain.marker;
        marker.stop();
        marker.removeFrom(this.map);
        this.trains.delete(id);
        this.control.remove(id);
      }
    });

    this.control.reorder();
  }
}
