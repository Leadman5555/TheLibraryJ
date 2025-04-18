import {HttpClient, HttpParams} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, Observable, of, tap} from 'rxjs';
import {UserProfileData} from './shared/user-profile-data';
import {handleError, logError} from '@app/shared/errorHandling/handleError';
import {
  UserPreferenceUpdateRequest,
  UserPreferenceUpdateResponse,
  UserProfileImageUpdateResponse,
  UserRankUpdateResponse,
  UserStatusUpdateRequest,
  UserStatusUpdateResponse,
  UserUsernameUpdateRequest,
  UserUsernameUpdateResponse
} from './user-profile-edit/dto/UserUpdateDtos';
import {BookPreview} from '@app/book/shared/models/book-preview';
import {FetchedUserMini} from '@app/user/shared/models/fetched-user-mini';
import {StorageService} from '@app/shared/storage/storage.service';
import {BookTokenResponse} from '@app/user/profile/shared/dto/book-token-response';
import {BookTokenConsummationRequest} from '@app/user/profile/shared/dto/book-token-consummation-request';
import {FavouriteBookMergerResponse} from '@app/user/profile/shared/dto/favourite-book-merger-response';
import {serverAuthFreeRoute, serverRoute} from '@app/app.routes';
import {TopRankerResponse} from '@app/user/shared/models/top-ranker-response';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {

  private readonly baseUrl: string = `${serverAuthFreeRoute}/user`;
  private readonly baseAuthUrl: string =`${serverRoute}/user`;
  private readonly bookByIdFetchUrl: string = `${serverAuthFreeRoute}/books/id`;

  constructor(private http: HttpClient, private storageService: StorageService) { }

  fetchUserProfile(forUsername: string): Observable<UserProfileData> {
    return this.http.get<UserProfileData>(`${this.baseUrl}/${forUsername}`)
      .pipe(catchError(handleError));
  }

  updateRank(forEmail: string) : Observable<UserRankUpdateResponse> {
    return this.http.patch<UserRankUpdateResponse>(`${this.baseAuthUrl}/profile/rank/` + forEmail, null)
      .pipe(catchError(handleError));
  }

  updateUsername(updateRequest: UserUsernameUpdateRequest) : Observable<UserUsernameUpdateResponse> {
    return this.http.patch<UserUsernameUpdateResponse>(`${this.baseAuthUrl}/profile/username`, updateRequest)
      .pipe(catchError(handleError));
  }

  updateStatus(updateRequest: UserStatusUpdateRequest) : Observable<UserStatusUpdateResponse> {
    return this.http.patch<UserStatusUpdateResponse>(`${this.baseAuthUrl}/profile/status`, updateRequest)
      .pipe(catchError(handleError));
  }

  updateProfileImage(profileImageUpdateRequest: FormData) : Observable<UserProfileImageUpdateResponse> {
    return this.http.patch<UserProfileImageUpdateResponse>(`${this.baseAuthUrl}/profile/image`, profileImageUpdateRequest)
      .pipe(catchError(handleError));
  }

  updatePreference(preferenceUpdateRequest: UserPreferenceUpdateRequest) : Observable<UserPreferenceUpdateResponse> {
    return this.http.patch<UserPreferenceUpdateResponse>(`${this.baseAuthUrl}/profile/preference`, preferenceUpdateRequest)
      .pipe(catchError(handleError));
  }

  public addBookToUserFavourites(bookId: string, userEmail: string): Observable<number> {
    return this.http.post<number>(`${this.baseAuthUrl}/book/favourite`, null, {params: new HttpParams().set('bookId', bookId).set('email', userEmail)})
      .pipe(
        tap(countOnServer => {
          if(!this.storageService.addBookToLoggedFavBooks(bookId, countOnServer)){
            console.log('Favourite book count on server differs from local count. Updating local stash.')
            this.updateLocalFavouriteBookIds(userEmail);
          }
        }),
        catchError(handleError)
      );
  }


  private updateLocalFavouriteBookIds(email: string){
    this.http.get<string[]>(`${this.baseAuthUrl}/book/favourite`, {params: new HttpParams().set('email', email).set('onlyIds', 'true')}).subscribe({
      next: (bookIds) => this.storageService.setLoggedFavBooks(bookIds),
      error: (error) => logError(error)
    })
  }

  public removeBookFromUserFavourites(bookId: string, userEmail: string): Observable<void> {
    return this.http.delete<void>(`${this.baseAuthUrl}/book/favourite`, {params: new HttpParams().set('bookId', bookId).set('email', userEmail)})
      .pipe(
        tap(() => this.storageService.removeBookFromLoggedFavBooks(bookId)),
        catchError(handleError)
      );
  }

  public getFavouriteBooksForUser(email: string): Observable<BookPreview[]> {
    return this.http.get<BookPreview[]>(`${this.baseAuthUrl}/book/favourite`, {params: new HttpParams().set('email', email)})
      .pipe(
        tap(bookPreviews => this.storageService.setLoggedFavBooks(bookPreviews.map(bookPreview => bookPreview.id))),
        catchError(handleError)
      );
  }

  public getFavouriteBooksForDevice(): Observable<BookPreview[]> {
    const deviceFavBooks = this.storageService.getDeviceFavBooks();
    if(!deviceFavBooks || deviceFavBooks.length === 0) return of([]);
    return this.http.post<BookPreview[]>(this.bookByIdFetchUrl, deviceFavBooks).pipe(catchError(handleError));
  }

  public getFavouriteBookIdsForUser(email: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseAuthUrl}/book/favourite`, {params: new HttpParams().set('email', email).set('onlyIds', 'true')})
      .pipe(
        tap(bookIds => this.storageService.setLoggedFavBooks(bookIds)),
        catchError(handleError)
      );
  }

  public addBookToDeviceFavourites(bookId: string ): number {
    return this.storageService.addBookToDeviceFavBooks(bookId);
  }

  public removeBookFromDeviceFavourites(bookId: string): number {
    return this.storageService.removeBookFromDeviceFavBooks(bookId);
  }

  public isBookInLoggedFavourites(bookId: string): boolean {
    return this.storageService.isBookInLoggedFavBooks(bookId);
  }

  public isBookInFavourites(bookId: string): boolean {
    return this.storageService.isBookInDeviceFavBooks(bookId);
  }

  public upsertAndGetFavouriteBookToken(email: string): Observable<BookTokenResponse> {
    const body = {email: email};
    return this.http.put<BookTokenResponse>(`${this.baseAuthUrl}/book/token`, body).pipe(catchError(handleError));
  }

  public mergeFavouriteBooksUsingToken(consummationRequest: BookTokenConsummationRequest): Observable<FavouriteBookMergerResponse> {
    return this.http.post<FavouriteBookMergerResponse>(`${this.baseAuthUrl}/book/token`, consummationRequest).pipe(catchError(handleError));
  }

  public sendExistingBookTokenToOwnerEmail(consummationRequest: BookTokenConsummationRequest): Observable<void> {
    return this.http.post<void>(`${this.baseAuthUrl}/book/token/email`, consummationRequest).pipe(catchError(handleError));
  }

  public addBookToUserSubscribed(bookId: string, userEmail: string): Observable<number> {
    return this.http.post<number>(`${this.baseAuthUrl}/book/subscribed`, null, {params: new HttpParams().set('bookId', bookId).set('email', userEmail)})
      .pipe(
        tap(countOnServer => {
          if(!this.storageService.addBookToLoggedSubBooks(bookId, countOnServer)){
            console.log('Subscribed book count on server differs from local count. Updating local stash.')
            this.updateLocalSubscribedBookIds(userEmail);
          }
        }),
        catchError(handleError)
      );
  }

  private updateLocalSubscribedBookIds(email: string){
    this.http.get<string[]>(`${this.baseAuthUrl}/book/subscribed`, {params: new HttpParams().set('email', email).set('onlyIds', 'true')}).subscribe({
      next: (bookIds) => this.storageService.setLoggedSubBooks(bookIds),
      error: (error) => logError(error)
    })
  }

  public removeBookFromUserSubscribed(bookId: string, userEmail: string): Observable<void> {
    return this.http.delete<void>(`${this.baseAuthUrl}/book/subscribed`, {params: new HttpParams().set('bookId', bookId).set('email', userEmail)})
      .pipe(
        tap(() => this.storageService.removeBookFromLoggedSubBooks(bookId)),
        catchError(handleError)
      );
  }

  public getSubscribedBooksForUser(email: string): Observable<BookPreview[]> {
    return this.http.get<BookPreview[]>(`${this.baseAuthUrl}/book/subscribed`, {params: new HttpParams().set('email', email)})
      .pipe(
        tap(bookPreviews => this.storageService.setLoggedSubBooks(bookPreviews.map(bookPreview => bookPreview.id))),
        catchError(handleError)
      );
  }

  public getSubscribedBookIdsForUser(email: string): Observable<string[]> {
    return this.http.get<string[]>(`${this.baseAuthUrl}/book/subscribed`, {params: new HttpParams().set('email', email).set('onlyIds', 'true')})
      .pipe(
        tap(bookIds => this.storageService.setLoggedSubBooks(bookIds)),
        catchError(handleError)
      );
  }


  public isBookInLoggedSubscribed(bookId: string): boolean {
    return this.storageService.isBookInLoggedSubBooks(bookId);
  }

  public canUserAuthorBooks(email: string): Observable<void>{
    return this.http.post<void>(`${this.baseAuthUrl}/verify/${email}`, null);
  }

  public fetchUserMiniData(email: string): Observable<FetchedUserMini> {
    return this.http.get<FetchedUserMini>(`${this.baseUrl}/mini/${email}`);
  }

  public fetchTopRankingUsers(): Observable<TopRankerResponse[]> {
    return this.http.get<TopRankerResponse[]>(`${this.baseUrl}/top`).pipe(catchError(handleError));
  }
}
