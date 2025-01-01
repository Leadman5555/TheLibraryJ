import {Component, inject, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {UserAuthService} from './user/user-auth.service';
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {AuthenticationRequest} from './user/shared/models/authentication-request';
import {UserMini} from './user/shared/models/user-mini';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, NgIf, ReactiveFormsModule, NgOptimizedImage],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

  constructor(private fb: NonNullableFormBuilder) {
  }

  ngOnInit(): void {
    this.userAuthService.loggedIn$.subscribe(v => this.showLoggedIn = v);
    this.userAuthService.userData$.subscribe(v => this.userMini = {
      username: v?.username,
      profileImage: v?.profileImage
    });
    this.logInForm = this.fb.group({
      email: ['', [Validators.email, Validators.required]],
      password: ['', Validators.required]
    });
  }

  showSettings: boolean = false;
  readonly userAuthService: UserAuthService = inject(UserAuthService);
  logInForm!: FormGroup;
  showLoggedIn: boolean = false;
  userMini!: UserMini;
  showPassword: boolean = false;

  handleLogInSubmit(): void {
    if (this.logInForm.pristine) return;
    const request: AuthenticationRequest = {
      email: this.logInForm.value.email,
      password: this.logInForm.value.password,
    };
    this.userAuthService.logIn(request);
    this.logInForm.reset();
  }

  toggleSettings() {
    this.showSettings = !this.showSettings;
  }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  logOut() {
    this.userAuthService.logOut();
  }

  logInWithGoogle() {
    this.userAuthService.getGoogleLogInLink().subscribe({
      next: (linkResponse) => window.location.href = linkResponse.authLink,
      error: (err) => console.error("Google login unavailable", err)
    });
  }

}
