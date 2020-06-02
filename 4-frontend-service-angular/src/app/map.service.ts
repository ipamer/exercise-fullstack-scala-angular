import {Injectable} from '@angular/core';
import {environment} from '../environments/environment';
import {TerminalData} from './terminal.service';

declare var H: any;

@Injectable()
export class MapService {

  map: any;
  markers: any[] = [];

  init(): void {
    const platform = new H.service.Platform({
      apikey: environment.MAPS_API_KEY
    });

    const defaultLayers = platform.createDefaultLayers();

    this.map = new H.Map(
      document.getElementById('mapContainer'),
      defaultLayers.vector.normal.map,
      {
        zoom: 1,
        center: {lat: 20, lng: 0}
      });
  }

  refreshMarkers(data: TerminalData[]) {
    this.map.removeObjects(this.markers);
    let highestValueMarked = false;
    this.markers = data.map(d => {
      if (highestValueMarked) {
        return this.createMarker(d.group_lat, d.group_lon);
      } else {
        highestValueMarked = true;
        return this.createMarker(d.group_lat, d.group_lon, 'red');
      }
    });
    this.map.addObjects(this.markers);
  }

  private createMarker(lat: number, lon: number, color = 'blue') {
    const icon = new H.map.Icon(`https://maps.google.com/mapfiles/ms/icons/${color}.png`);
    return new H.map.Marker({lat: lat, lng: lon}, {icon: icon});
  }

}
