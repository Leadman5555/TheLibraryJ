import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {BookResponse} from '../../shared/models/book-response';

@Injectable({
  providedIn: 'root'
})
export class AuthorTabDataService {
  private currentlyEditing = new BehaviorSubject<BookResponse | null>(null);
  authorEmail!: string;

  setCurrentBook(book: BookResponse) {
    this.currentlyEditing.next(book);
  }

  setAuthorEmail(email: string){
    this.authorEmail = email;
  }

  getCurrentlyEditedBook() : BookResponse | null {
    return this.currentlyEditing.value;
  }



}
