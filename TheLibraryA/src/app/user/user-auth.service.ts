import { Injectable } from '@angular/core';
import {UserProfile} from './shared/models/user-profile';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {AuthenticationResponse} from './shared/models/authentication-response';
import {AuthenticationRequest} from './shared/models/authentication-request';

@Injectable({
  providedIn: 'root'
})
export class UserAuthService {
  userData?: UserProfile;
  loggedIn: boolean = false;
  token?: string;

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';
  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9/';

  constructor(private httpClient: HttpClient) {
  }

  isLoggedIn(): boolean {
    return this.loggedIn && this.token !== undefined;
  }

  logOut(){
    this.userData = undefined;
    this.loggedIn = false;
    this.token = undefined;
  }

  logIn(request: AuthenticationRequest) : Observable<Boolean>{
    return new Observable<Boolean>((observer) => {
      this.httpClient.post<AuthenticationResponse>(`${this.baseUrl}/auth`, request).subscribe(
        {
          next: (response) => {
            this.loggedIn = true;
            this.token = response.token;
            this.fetchUserData(request.email).subscribe({
              next: (userProfile) => {
                this.userData = userProfile;
                observer.next(true);
                observer.complete();
              },
              error: (error) => {
                console.error(error);
                observer.error(error);
              }
            });
          },
          error: (error) => {
            console.error(error);
            observer.error(error);
          }
        }
      );
    });
  }

  private fetchUserData(email: string) : Observable<UserProfile>{
    return this.httpClient.get<UserProfile>(`${this.baseUrl}/user/email/` + email);
  }

  register(email: string, password: string, username:string, profileImage: string) : Observable<Boolean>{
    throw new Error("Method not implemented.");
  }
}
