import {Injectable} from '@angular/core';
import {catchError, map, Observable, switchMap} from 'rxjs';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {AuthenticationResponse} from '../shared/models/authentication-response';
import {AuthenticationRequest} from '../shared/models/authentication-request';
import {GoogleCallbackResponse} from '../../googleOAuth2/auth-callback/google-callback-response';
import {GoogleLinkResponse} from '../../googleOAuth2/auth-callback/google-link-response';
import {UserMini} from '../shared/models/user-mini';
import {FetchedUserMini} from '../shared/models/fetched-user-mini';
import {StorageService} from '../../shared/storage/storage.service';
import {EventBusService, LOGIN_EVENT, LOGOUT_EVENT, REFRESH_EVENT} from '../../shared/eventBus/event-bus.service';
import {EventData} from '../../shared/eventBus/event.class';
import {handleError, logError} from '../../shared/errorHandling/handleError';

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {
  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';

  constructor(private httpClient: HttpClient, private storageService: StorageService, private eventBus: EventBusService) {
  }

  refreshAccessToken(): Observable<HttpResponse<any>> {
    return this.httpClient.get(`${this.baseUrl}/auth/refresh`, {withCredentials: true, observe: 'response'});
  }

  private setUserData(user: UserMini, token: string): void {
    this.storageService.setAccessToken(token);
    this.storageService.setUserMini(user);
  }

  sendLogOutEvent(): void {
    this.eventBus.emit(new EventData(LOGOUT_EVENT, null));
  }

  logOut(): Observable<any> {
    this.storageService.clearData();
    return this.httpClient.get(`${this.baseUrl}/auth/logout`, {withCredentials: true});
  }

  isLoggedIn(): boolean {
    return this.storageService.isLoggedIn();
  }

  logIn(request: AuthenticationRequest): Observable<void> {
    return this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/auth/login`, request, {withCredentials: true}).pipe(
      switchMap((response: AuthenticationResponse) =>
        this.fetchUserMiniData(request.email).pipe(
          map((userProfile: FetchedUserMini) => {
            this.setUserData({
              username: userProfile.username,
              profileImage: userProfile.profileImage,
              email: request.email
            }, response.token);
            this.eventBus.emit(new EventData(LOGIN_EVENT, null));
          }),
          catchError((error) => {
            this.logOut();
            return handleError(error);
          })
        )
      ),
      catchError((error) => {
        return handleError(error);
      })
    );
  }


  private fetchUserMiniData(email: string): Observable<FetchedUserMini> {
    return this.httpClient.get<FetchedUserMini>(`${this.baseUrl}/user/mini/` + email);
  }

  updateUserMiniDataImage(image: string){
    if(!this.storageService.setUserMiniImage(image)) this.eventBus.emit(new EventData(LOGOUT_EVENT, null));
    else this.eventBus.emit(new EventData(REFRESH_EVENT, null));
  }

  updateUserMiniDataUsername(username: string){
    if(!this.storageService.setUserMiniUsername(username)) this.eventBus.emit(new EventData(LOGOUT_EVENT, null));
    else this.eventBus.emit(new EventData(REFRESH_EVENT, null));
  }

  getGoogleLogInLink(): Observable<GoogleLinkResponse> {
    return this.httpClient.get<GoogleLinkResponse>(`${this.baseUrl}/auth/google`);
  }

  googleOnSuccessRedirect(response: GoogleCallbackResponse) {
    this.fetchUserMiniData(response.email).subscribe({
      next: (userProfile) => {
        this.setUserData({
          username: userProfile.username,
          profileImage: userProfile.profileImage,
          email: response.email
        }, response.token);
        this.eventBus.emit(new EventData(LOGIN_EVENT, null));
      },
      error: (error) => {
        logError(error);
      }
    });
  }

  getLoggedInUsername(): string {
    const username = this.storageService.getUserMiniUsername();
    if(username !== undefined) return username;
    this.eventBus.emit(new EventData(LOGOUT_EVENT, null));
    throw new Error("Invalid session data");
  }
}
