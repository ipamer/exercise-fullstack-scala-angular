import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';

import {animate, state, style, transition, trigger} from '@angular/animations';
import {webSocket, WebSocketSubject} from 'rxjs/webSocket';
import {Subscription} from 'rxjs';
import {MeetupResponse, TerminalData, TerminalService} from './terminal.service';
import {MapService} from './map.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [
    trigger('editAnimation', [
      state('normal', style({
        transform: 'scale(1)'
      })),
      state('edited', style({
        backgroundColor: '#9aefb1',
        transform: 'scale(1.1)'
      })),
      transition('* => edited', animate('500ms ease-in')),
      transition('edited => *', animate('500ms ease-out'))
    ]),
  ],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class AppComponent implements OnInit, OnDestroy {

  title = 'My Favourite Meetup Locations';
  wsStatus = 'Connecting ...';
  subscription: Subscription;
  consoleContent: TerminalData[] = [];

  constructor(
    private terminalService: TerminalService,
    private mapService: MapService,
    private cdr: ChangeDetectorRef,
  ) {
  }

  ngOnInit(): void {
    this.initWebSocket();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  private initWebSocket() {
    const wsSubject: WebSocketSubject<any> = webSocket('ws://localhost:8080/mymeetups');
    this.subscription = wsSubject.subscribe(
      (data: MeetupResponse[]) => {
        // console.log('message received: ' + JSON.stringify(msg));
        this.wsStatus = 'Connected. The terminal will refresh when new data arrives.';
        this.consoleContent = this.terminalService.process(data);
        this.mapService.refreshMarkers(this.consoleContent);

        this.cdr.detectChanges();
        setTimeout(() => {
          this.consoleContent.forEach(x => x.animation = 'normal');
          this.cdr.detectChanges();
        }, 500);
      },
      err => {
        // console.log(err);
        this.wsStatus = 'Connection error. Try reloading the page.';
      },
      () => {
        // console.log('websocket complete');
        this.wsStatus = 'Connection closed. Try reloading the page.';
      }
    );
  }

}
