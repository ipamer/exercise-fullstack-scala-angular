import {Injectable} from '@angular/core';

@Injectable()
export class TerminalService {

  private previous = {};

  process(data: MeetupResponse[]): TerminalData[] {
    const previous = {};
    const result: TerminalData[] = [];
    data.forEach(meetup => {
      const newValue: TerminalData = {
        key: meetup.group_city,
        value: meetup.numberOfMeetups,
        data: meetup.group_city + ' - ' + meetup.numberOfMeetups,
        group_lat: meetup.group_lat,
        group_lon: meetup.group_lon,
        animation: 'normal',
      };
      result.push(newValue);
      previous[meetup.group_city] = newValue;
      if (this.previous.hasOwnProperty(meetup.group_city)) {
        if (this.previous[meetup.group_city].value !== newValue.value) {
          newValue.animation = 'edited';
        }
      } else {
        newValue.animation = 'edited';
      }
    });
    this.previous = previous;
    return result.sort((a, b) => b.value - a.value);
  }

}

export interface TerminalData {
  key: string;
  value: number;
  data: string;
  animation: string;
  group_lat: number;
  group_lon: number;
}

export interface MeetupResponse {
  group_city: string;
  group_country: string;
  group_lat: number;
  group_lon: number;
  numberOfMeetups: number;
}
