import {Component, inject, OnInit} from '@angular/core';
import {UserAuthService} from '../../user/user-auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HttpClient, HttpParams} from '@angular/common/http';
import {GoogleCallbackResponse} from './google-callback-response';

@Component({
  selector: 'app-auth-callback',
  template: '<p>Loading</p>',
})
export class AuthCallbackComponent implements OnInit{
  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  private userAuthService: UserAuthService = inject(UserAuthService);
  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';
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
    this.router.navigate([this.redirectTo]);
  }

  serverRedirect(code: string): void {
    const params = new HttpParams().set('code', code);
    this.http.get<GoogleCallbackResponse>(`${this.baseUrl}/auth/google/callback`, {params, withCredentials: true}).subscribe({
      next: (response : GoogleCallbackResponse) => {
        this.userAuthService.googleOnSuccessRedirect(response);
      },
      error: (err) => {
        console.error('Error exchanging code:', err);
      },
    });
  }
}
