import {Component, OnInit} from '@angular/core';
import {MapService} from './map.service';

declare var H: any;

@Component({
  selector: 'app-map',
  template: `
    <div style="width: 1000px; height: 500px; margin: 20px; border: #b8b8b8 1px solid; border-radius: 5px" id="mapContainer"></div>
  `
})
export class MapComponent implements OnInit {

  // maps from: https://developer.here.com/

  constructor(
    private mapService: MapService,
  ) {
  }

  ngOnInit(): void {
    this.mapService.init();
  }

}
