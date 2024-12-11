import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {FormOutcome} from './form-outcome';

@Injectable({
  providedIn: 'root'
})
export class BookFilterService {
  private formOutcomeSource = new BehaviorSubject<FormOutcome>(new FormOutcome(true));
  currentForm$ = this.formOutcomeSource.asObservable();

  onFormSubmit(form: FormOutcome) {
    this.formOutcomeSource.next(form);
  }
}
