import {Injectable} from '@angular/core';
import {catchError, map, Observable, switchMap} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {AuthenticationResponse} from './shared/models/authentication-response';
import {AuthenticationRequest} from './shared/models/authentication-request';
import {GoogleCallbackResponse} from '../googleOAuth2/auth-callback/google-callback-response';
import {GoogleLinkResponse} from '../googleOAuth2/auth-callback/google-link-response';
import {UserMini} from './shared/models/user-mini';
import {FetchedUserMini} from './shared/models/fetched-user-mini';
import {StorageService} from '../shared/storage/storage.service';
import {EventBusService} from '../shared/eventBus/event-bus.service';
import {EventData} from '../shared/eventBus/event.class';
import {handleError, logError} from '../shared/errorHandling/handleError';

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {
  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';

  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9';

  constructor(private httpClient: HttpClient, private storageService: StorageService, private eventBus: EventBusService) {
  }

  refreshAccessToken(): Observable<any> {
    return this.httpClient.get(`${this.baseUrl}/auth/refresh`);
  }

  private setUserData(user: UserMini, token: string): void {
    this.storageService.setAccessToken(token);
    this.storageService.setUserMini(user);
  }

  logOut(): Observable<any> {
    this.storageService.clearData();
    return this.httpClient.get(`${this.baseUrl}/auth/logout`, {withCredentials: true});
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
            this.eventBus.emit(new EventData('login', null));
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

  register(email: string, password: string, username: string, profileImage: string): Observable<Boolean> {
    throw new Error("Method not implemented.");
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
        this.eventBus.emit(new EventData('login', null));
      },
      error: (error) => {
        logError(error);
      }
    });
  }
}
