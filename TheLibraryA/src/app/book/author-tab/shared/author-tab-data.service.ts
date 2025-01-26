import { Injectable } from '@angular/core';
import {Subject} from 'rxjs';
import {BookResponse} from '../../shared/models/book-response';
import {FormOutcome} from '../../book-filter/filterService/form-outcome';

@Injectable({
  providedIn: 'root'
})
export class AuthorTabDataService {
  private currentlyEditing = new Subject<BookResponse>();
  currentlyEditing$ = this.currentlyEditing.asObservable();

  setCurrentBook(book: BookResponse) {
    this.currentlyEditing.next(book);
  }


}
