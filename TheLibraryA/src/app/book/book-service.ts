import {Injectable} from '@angular/core';
import {BookPreview} from './book-preview';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';

@Injectable({
    providedIn: 'root'
})
export class BookService {
    constructor(private http: HttpClient) { }
    private baseUrl: string = 'http://localhost:8082/v0.9/na/books';
    private baseAuthUrl: string = 'http://localhost:8082/v0.9/books';

    public getBookPreviews() : Observable<BookPreview[]> {
        return this.http.get<BookPreview[]>(this.baseUrl);
    }
}
