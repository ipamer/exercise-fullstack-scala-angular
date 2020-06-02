import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TerminalService} from './terminal.service';
import {MapComponent} from './map.component';
import {MapService} from './map.service';

@NgModule({
  declarations: [
    AppComponent,
    MapComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
  ],
  providers: [
    TerminalService,
    MapService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
