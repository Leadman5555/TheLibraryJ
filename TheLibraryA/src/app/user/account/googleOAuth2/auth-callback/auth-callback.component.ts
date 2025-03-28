import {Component, inject, OnInit} from '@angular/core';
import {UserAuthService} from '../../userAuth/user-auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {GoogleCallbackResponse} from './google-callback-response';
import {serverAuthFreeRoute} from '@app/app.routes';

@Component({
  selector: 'app-auth-callback',
  template: '<p>Loading</p>',
  standalone: true
})
export class AuthCallbackComponent implements OnInit{
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  private userAuthService: UserAuthService = inject(UserAuthService);
  private readonly baseUrl: string = `${serverAuthFreeRoute}`;
  private readonly redirectTo: string = '';

  ngOnInit(): void {
    this.route.queryParams.subscribe((params) => {
      const code = params['code'];
      if (code) {
        this.serverRedirect(code);
      } else {
        console.error('Authorization code not found in callback.');
      }
    });
    void this.router.navigate([this.redirectTo]);
  }

  serverRedirect(code: string): void {
    const params = new HttpParams().set('code', code);
    this.http.get<GoogleCallbackResponse>(`${this.baseUrl}/auth/google/callback`, {params: params, withCredentials: true}).subscribe({
      next: (response : GoogleCallbackResponse) => {
        this.userAuthService.googleOnSuccessRedirect(response);
      },
      error: (err) => {
        console.error('Error exchanging code:', err);
      },
    });
  }
}
