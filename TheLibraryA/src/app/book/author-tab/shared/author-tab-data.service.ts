import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';
import {BookResponse} from '../../shared/models/book-response';

@Injectable({
  providedIn: 'root'
})
export class AuthorTabDataService {
  private currentlyEditing = new Subject<BookResponse>();
  currentlyEditing$ = this.currentlyEditing.asObservable();
  authorEmail!: string;

  setCurrentBook(book: BookResponse) {
    this.currentlyEditing.next(book);
  }

  setAuthorEmail(email: string){
    this.authorEmail = email;
  }


}
