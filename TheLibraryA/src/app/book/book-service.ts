import {Injectable} from '@angular/core';
import {BookPreview} from './book-preview';
import {Observable} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BookDetail} from './book-detail';
import {BookResponse} from './book-response';
import {ChapterContent} from './chapter-content';
import {BookPage} from '../home/home/paging/book-page';
import {KeysetPage} from '../home/home/paging/keyset-page';
import {BookState} from './BookState';
import {BookTag} from './BookTag';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  constructor(private readonly http: HttpClient) {
  }

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na/books';
  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9/books';

  public getBookPreviewsPageByOffset(page?: number, pageSize?: number): Observable<BookPage> {
    const params = new HttpParams().set('page', page ?? 0).set('pageSize', pageSize ?? 20);
    return this.http.get<BookPage>(this.baseUrl, {params});
  }

  public getBookPreviewsPageByKeySet(page: number, keysetPage: KeysetPage): Observable<BookPage> {
    const params = new HttpParams().set('page', page);
    return this.http.post<BookPage>(this.baseUrl, keysetPage , {params});
  }

  public getBookPreviewsByParams(titleLike : string, minChapters : number, minRating: number, state: BookState, hasTags : BookTag[]): Observable<BookPage> {
    let params = new HttpParams();
    if(titleLike) params = params.set('titleLike', titleLike);
    if(minChapters) params = params.set('minChapters', minChapters);
    if(minRating) params = params.set('minRating', minRating);
    if(state) params = params.set('state', state);
    if(hasTags) hasTags.forEach((tag) => params = params.append('hasTags', tag));
    return this.http.get<BookPage>(this.baseUrl, {params});
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
