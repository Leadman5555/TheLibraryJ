import {Injectable} from '@angular/core';
import {BookPreview} from './book-preview';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BookDetail} from './book-detail';
import {BookResponse} from './book-response';
import {ChapterContent} from './chapter-content';
import {BookPage} from '../home/home/BookPage';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  constructor(private readonly http: HttpClient) {
  }

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na/books';
  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9/books';

  public getBookPreviews(page?: number, pageSize?: number): Observable<BookPage> {
    let params = new HttpParams();
    params = params.append('page', page ?? 0);
    params = params.append('pageSize', pageSize ?? 0);
    return  this.http.get<BookPage>(this.baseUrl, {params});
  }

  public getBookDetail(bookId: string): Observable<BookDetail> {
    return this.http.get<BookDetail>(`${this.baseUrl}/${bookId}`);
  }

  public getBook(bookTitle: string): Observable<BookResponse> {
    return this.http.get<BookResponse>(`${this.baseUrl}/book/${bookTitle}`);
  }

  public getChapterContentByNumber(bookId: string, chapterNumber: number): Observable<ChapterContent> {
    let params = new HttpParams();
    params = params.append('bookId', bookId);
    params = params.append('chapterNumber', chapterNumber);
    return this.http.get<ChapterContent>(`${this.baseUrl}/book/chapter`, {params: params});
  }
}
