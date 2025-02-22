import {Injectable} from '@angular/core';
import {BookPreview} from './models/book-preview';
import {catchError, Observable, retry} from 'rxjs';
import {HttpClient, HttpParams} from '@angular/common/http';
import {BookDetail} from './models/book-detail';
import {BookResponse} from './models/book-response';
import {ChapterContent} from './models/chapter-content';
import {BookPage} from '../../shared/paging/book/book-page';
import {KeySetPage} from '../../shared/paging/models/key-set-page';
import {RatingResponse} from './models/rating-response';
import {ChapterPreviewPage} from '../book/paging/chapterPreview-page';
import {RatingRequest} from './models/rating-request';
import {handleError} from '../../shared/errorHandling/handleError';
import {ChapterPreview} from './models/chapter-preview';

@Injectable({
  providedIn: 'root'
})
export class BookService {
  constructor(private readonly http: HttpClient) {
  }

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na/books';
  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9/books';

  private static getParamsForKeySetPaging(page: number, params?: HttpParams): HttpParams {
    return (params ?? new HttpParams()).set('page', page);
  }

  private static getParamsForOffsetPaging(page: number, pageSize: number, params?: HttpParams): HttpParams {
    return (params ?? new HttpParams()).set('page', page).set('pageSize', pageSize);
  }

  public getBookPreviewsPageByOffset(page: number, pageSize: number): Observable<BookPage> {
    const params = BookService.getParamsForOffsetPaging(page, pageSize);
    return this.http.get<BookPage>(this.baseUrl, {params});
  }

  public getBookPreviewsPageByKeySet(page: number, keySetPage: KeySetPage): Observable<BookPage> {
    const params = BookService.getParamsForKeySetPaging(page);
    return this.http.post<BookPage>(this.baseUrl, keySetPage, {params});
  }

  public getChapterPreviewsPageByOffset(bookId: string, page: number, pageSize: number): Observable<ChapterPreviewPage> {
    const params = BookService.getParamsForOffsetPaging(page, pageSize);
    return this.http.get<ChapterPreviewPage>(`${this.baseUrl}/${bookId}/chapter`, {params});
  }

  public getChapterPreviewsPageByKeySet(bookId: string, page: number, keySetPage: KeySetPage): Observable<ChapterPreviewPage> {
    const params = BookService.getParamsForKeySetPaging(page);
    return this.http.post<ChapterPreviewPage>(`${this.baseUrl}/${bookId}/chapter`, keySetPage, {params});
  }

  public getBookPreviewsByParamsPageByOffset(filterParams: HttpParams, page: number, pageSize: number): Observable<BookPage> {
    const params = BookService.getParamsForOffsetPaging(page, pageSize, filterParams);
    console.log('offset')
    return this.http.get<BookPage>(`${this.baseUrl}/filtered`, {params});
  }

  public getBookPreviewsByParamsPageByKeySet(filterParams: HttpParams, page: number, keySetPage: KeySetPage): Observable<BookPage> {
    const params = BookService.getParamsForKeySetPaging(page, filterParams);
    console.log('keySetPage')
    return this.http.post<BookPage>(`${this.baseUrl}/filtered`, keySetPage, {params});
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

  public createBook(bookCreationData: FormData): Observable<BookResponse> {
    return this.http.post<BookResponse>(`${this.baseAuthUrl}/book`, bookCreationData).pipe(catchError(handleError));
  }

  public updateBook(bookUpdateData: FormData): Observable<BookResponse> {
    return this.http.put<BookResponse>(`${this.baseAuthUrl}/book`, bookUpdateData).pipe(catchError(handleError));
  }

  public deleteBook(bookId: string, userEmail: string): Observable<void> {
    const body = {bookId: bookId, userEmail: userEmail};
    return this.http.delete<void>(`${this.baseAuthUrl}/book`, {body: body}).pipe(catchError(handleError));
  }

  public uploadChaptersInBatch(bookId: string, authorEmail: string, chapters: File[]): Observable<ChapterPreview[]> {
    const formData = new FormData();
    chapters.forEach(chapter => {
      formData.append('chapterBatch', chapter);
    });
    const params = new HttpParams().set('authorEmail', authorEmail);
    return this.http.put<ChapterPreview[]>(`${this.baseAuthUrl}/book/${bookId}/chapter`, formData, {params}).pipe(catchError(handleError));
  }

  public deleteChapters(bookId: string, authorEmail: string, chapterNumber: number): Observable<void> {
    const body = {bookId: bookId, userEmail: authorEmail};
    return this.http.delete<void>(`${this.baseAuthUrl}/book/chapter/${chapterNumber}`, {body: body}).pipe(catchError(handleError));
  }
}
