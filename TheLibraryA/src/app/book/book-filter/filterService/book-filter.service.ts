import {Injectable} from '@angular/core';
import {BehaviorSubject, Subject} from 'rxjs';
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

  refreshSelection(){
    this.formOutcomeSource.next(this.formOutcomeSource.getValue());
  }

  getRedirectData() : FormOutcome | undefined {
    let newForm : FormOutcome | undefined;
    this.currentForm$.subscribe(form => form.isRedirected ? newForm = form : newForm = undefined);
    return newForm;
  }
}
