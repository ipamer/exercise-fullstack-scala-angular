import {AppComponent} from './app.component';
import {TerminalService} from './terminal.service';

describe('AppComponent', () => {

  it('should create the app', () => {
    const testData = [
      {'group_city': 'London', 'group_country': 'uk', 'group_lat': 40.32, 'group_lon': 91.45, 'numberOfMeetups': 3},
      {'group_city': 'Taipei', 'group_country': 'tw', 'group_lat': 30.32, 'group_lon': 121.45, 'numberOfMeetups': 3}
    ];

    const underTest = new TerminalService();
    const response = underTest.process(testData);
    expect(response).toEqual([
      {key: 'London', value: 3, data: 'London - 3', group_lat: 40.32, group_lon: 91.45, animation: 'edited'},
      {key: 'Taipei', value: 3, data: 'Taipei - 3', group_lat: 30.32, group_lon: 121.45, animation: 'edited'},
    ]);
  });

});
