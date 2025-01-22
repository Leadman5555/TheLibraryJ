import {Injectable} from '@angular/core';
import {Subject, Subscription} from 'rxjs';
import {filter, map} from 'rxjs/operators';
import {EventData} from './event.class';


export const LOGIN_EVENT = 'login';
export const LOGOUT_EVENT = 'logout';
export const REFRESH_EVENT = 'refresh';
@Injectable({
  providedIn: 'root'
})
export class EventBusService {
  private subject$ = new Subject<EventData>();

  constructor() {
  }

  emit(event: EventData) {
    this.subject$.next(event);
  }

  onMultiple(eventActions: { eventName: string, action: any }[]): Subscription {
    const subscriptions = eventActions.map(pair => {
      return this.subject$.pipe(
        filter((e: EventData) => e.name === pair.eventName),
        map((e: EventData) => e["value"])
      ).subscribe(pair.action);
    });
    return new Subscription(() => {
      subscriptions.forEach(sub => sub.unsubscribe());
    });
  }

  on(eventName: string, action: any): Subscription {
    return this.subject$.pipe(
      filter((e: EventData) => e.name === eventName),
      map((e: EventData) => e["value"])).subscribe(action);
  }
}
