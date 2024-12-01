import {Injectable} from '@angular/core';
import {BookPreview} from './book-preview';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BookDetail} from './book-detail';
import {BookResponse} from './book-response';
import {ChapterContent} from './chapter-content';

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

    public getBookDetail(bookId : string) : Observable<BookDetail> {
      return this.http.get<BookDetail>(`${this.baseUrl}/${bookId}`);
    }

    public getBook(bookTitle : string) : Observable<BookResponse> {
      return this.http.get<BookResponse>(`${this.baseUrl}/book/${bookTitle}`);
    }

  public getChapterContentByNumber(bookId: string, chapterNumber: number) : Observable<ChapterContent> {
    let params = new HttpParams();
    params = params.append('bookId', bookId);
    params = params.append('chapterNumber', chapterNumber);
    return this.http.get<ChapterContent>(`${this.baseUrl}/book/chapter`, {params: params} );
  }
}
