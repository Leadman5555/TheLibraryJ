import {Injectable} from '@angular/core';
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

  onTagsRedirect(selectedTags: string[]) {
    const form = new FormOutcome(true);
    form.setRedirectTags(selectedTags);
    form.isRedirected = true;
    this.formOutcomeSource.next(form);
  }

  getRedirectData() : FormOutcome | undefined {
    let newForm : FormOutcome | undefined;
    this.currentForm$.subscribe(form => form.isRedirected ? newForm = form : newForm = undefined);
    return newForm;
  }
}
