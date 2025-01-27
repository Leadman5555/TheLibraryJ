import {Injectable} from '@angular/core';
import {BookPreview} from './models/book-preview';
import {catchError, Observable, retry} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BookDetail} from './models/book-detail';
import {BookResponse} from './models/book-response';
import {ChapterContent} from './models/chapter-content';
import {BookPage} from '../../home/home/paging/book-page';
import {KeysetPage} from '../../shared/paging/models/keyset-page';
import {RatingResponse} from './models/rating-response';
import {ChapterPreviewPage} from '../book/paging/chapterPreview-page';
import {RatingRequest} from './models/rating-request';
import {handleError} from '../../shared/errorHandling/handleError';
import {FormOutcome} from '../book-filter/filterService/form-outcome';

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

  public getChapterPreviewsPageByOffset(bookId: string, page?: number, pageSize?: number): Observable<ChapterPreviewPage> {
    const params = new HttpParams().set('page', page ?? 0).set('pageSize', pageSize ?? 20);
    return this.http.get<ChapterPreviewPage>(`${this.baseUrl}/${bookId}/chapter`, {params});
  }

  public getChapterPreviewsPageByKeySet(bookId: string, page: number, keysetPage: KeysetPage): Observable<ChapterPreviewPage> {
    const params = new HttpParams().set('page', page);
    return this.http.post<ChapterPreviewPage>(`${this.baseUrl}/${bookId}/chapter`, keysetPage , {params});
  }

  public getBookPreviewsByParams(params : HttpParams): Observable<BookPreview[]> {
    return this.http.get<BookPreview[]>(`${this.baseUrl}/filtered`, {params});
  }

  public getBookPreviewsByAuthor(author: string): Observable<BookPreview[]> {
    return this.http.get<BookPreview[]>(`${this.baseUrl}/authored/${author}`).pipe(retry(1), catchError(handleError));
  }


  public getBookDetail(bookId: string): Observable<BookDetail> {
    return this.http.get<BookDetail>(`${this.baseUrl}/${bookId}`).pipe(catchError(handleError));
  }

  public getBook(bookTitle: string): Observable<BookResponse> {
    return this.http.get<BookResponse>(`${this.baseUrl}/book/${bookTitle}`);
  }

  public getChapterContentByNumber(bookId: string, chapterNumber: number): Observable<ChapterContent> {
    const params = new HttpParams().set('bookId', bookId).set('chapterNumber', chapterNumber);
    return this.http.get<ChapterContent>(`${this.baseUrl}/book/chapter`, {params: params});
  }

  public getRatingsForBook(bookId: string): Observable<RatingResponse[]> {
    return this.http.get<RatingResponse[]>(`${this.baseUrl}/${bookId}/rating`);
  }

  public upsertRatingForBook(request: RatingRequest): Observable<RatingResponse> {
    return this.http.put<RatingResponse>(`${this.baseAuthUrl}/rating`, request).pipe(catchError(handleError));
  }

  public mergePreviewAndDetail(bookPreview: BookPreview, bookDetail: BookDetail): BookResponse {
    return {
      ...bookPreview,
      ...bookDetail
    }
  }

  public createBook(bookCreationData: FormData): Observable<BookResponse>{
    // const formData = new FormData();
    // console.log(bookCreationData.get('title'))
    // for(let key of bookCreationData.keys()){
    //   console.log(key)
    //   formData.set(key, bookCreationData.get(key)!.toString());
    // }
    return this.http.post<BookResponse>(`${this.baseAuthUrl}/book`, bookCreationData).pipe(catchError(handleError));
  }

  public updateBook(bookUpdateData: FormData): Observable<BookResponse>{
    return this.http.put<BookResponse>(`${this.baseAuthUrl}/book`, bookUpdateData).pipe(catchError(handleError));
  }
}
