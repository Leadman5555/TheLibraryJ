import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {FormOutcome} from './form-outcome';

@Injectable({
  providedIn: 'root'
})
export class BookFilterService {
  private readonly formOutcomeSource = new BehaviorSubject<FormOutcome>(new FormOutcome(true));
  readonly currentForm$ = this.formOutcomeSource.asObservable();

  onFormSubmit(form: FormOutcome) {
    this.formOutcomeSource.next(form);
  }

  refreshSelection(){
    this.formOutcomeSource.next(this.formOutcomeSource.getValue());
  }

  getRedirectData() : FormOutcome | null {
    const redirectForm = this.formOutcomeSource.value;
    return redirectForm.isRedirected ? redirectForm : null;
  }
}
