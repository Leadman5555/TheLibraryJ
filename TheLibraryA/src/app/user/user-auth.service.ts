import {Injectable} from '@angular/core';
import {UserProfile} from './shared/models/user-profile';
import {BehaviorSubject, map, Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {AuthenticationResponse} from './shared/models/authentication-response';
import {AuthenticationRequest} from './shared/models/authentication-request';
import {GoogleCallbackResponse} from '../googleOAuth2/auth-callback/google-callback-response';
import {GoogleLinkResponse} from '../googleOAuth2/auth-callback/google-link-response';
import {AuthUserData} from './shared/models/auth-user-data';
import {UserMini} from './shared/models/user-mini';

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {
  private readonly loggedOutData: AuthUserData = {userProfile: undefined, token: undefined};
  private userAuthDataSubject: BehaviorSubject<AuthUserData> = new BehaviorSubject<AuthUserData>(this.loggedOutData);
  userData$ = this.userAuthDataSubject.asObservable().pipe(map(data => data.userProfile));
  loggedIn$ =this.userAuthDataSubject.asObservable().pipe(map(data => data.token !== undefined));

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';
  //private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9';

  constructor(private httpClient: HttpClient) {}

  logOut() {
    this.httpClient.get(`${this.baseUrl}/auth/logout`, {withCredentials: true}); //test it
    sessionStorage.removeItem('jwt-token');
    document.cookie = "XSRF-TOKEN=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    this.userAuthDataSubject.next(this.loggedOutData);
  }

  logIn(request: AuthenticationRequest){
      this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/auth/login`, request).subscribe(
        {
          next: (response) => {
            this.fetchUserData(request.email).subscribe({
              next: (userProfile) => {
                sessionStorage.setItem('jwt-token', response.token);
                this.userAuthDataSubject.next({token: response.token, userProfile: userProfile});
              },
              error: (error) => {
                console.error(error);
              }
            });
          },
          error: (error) => {
            console.error(error);
          }
        }
      );
  }

  // getXSRFToken() {
  //   return this.httpClient.get(`${this.baseUrl}/auth/csrf`, {withCredentials: true}).subscribe(
  //     (response) => {
  //       console.log(response);
  //       console.log(document.cookie);
  //     },
  //     (error) => {
  //       console.error(error);
  //     }
  //   );
  // }
  //
  // postXSRFToken() {
  //   return this.httpClient.post(`${this.baseUrl}/auth/p`, null, {withCredentials: true}).subscribe(
  //     (response) => {
  //       console.log(response);
  //       console.log(document.cookie);
  //     },
  //     (error) => {
  //       console.error(error);
  //     }
  //   );
  // }

  private fetchUserData(email: string): Observable<UserProfile> {
    return this.httpClient.get<UserProfile>(`${this.baseUrl}/user/email/` + email);
  }

  register(email: string, password: string, username: string, profileImage: string): Observable<Boolean> {
    throw new Error("Method not implemented.");
  }

  getGoogleLogInLink(): Observable<GoogleLinkResponse> {
    return this.httpClient.get<GoogleLinkResponse>(`${this.baseUrl}/auth/google`);
  }

  googleOnSuccessRedirect(response: GoogleCallbackResponse) {
      this.fetchUserData(response.email).subscribe({
        next: (userProfile) => {
          sessionStorage.setItem('jwt-token', response.token);
          this.userAuthDataSubject.next({token: response.token, userProfile: userProfile});
        },
        error: (error) => {
          console.log("Google login unavailable", error)
        }
      });
  }
}
