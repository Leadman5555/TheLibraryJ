import { Injectable } from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {FormOutcome} from './form-outcome';
import {BookTag} from '../../BookTag';

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
    selectedTags.forEach(tag => form.appendParam('hasTags', tag));
    this.formOutcomeSource.next(form);
  }
}
