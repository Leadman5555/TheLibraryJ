import {Component, inject, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {UserAuthService} from './user/user-auth.service';
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf} from '@angular/common';
import {AuthenticationRequest} from './user/shared/models/authentication-request';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, FormsModule, NgIf, ReactiveFormsModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit{

  constructor(private fb: NonNullableFormBuilder) {
  }

  ngOnInit(): void {
     this.logInForm = this.fb.group({
       email : ['', [Validators.email, Validators.required]],
       password : ['', Validators.required]
     });
  }

  title = 'TheLibraryA';
  showSettings: boolean = false;
  readonly userAuthService: UserAuthService = inject(UserAuthService);
  showLoggedIn: boolean = false;
  logInForm!: FormGroup;
  showPassword: boolean = false;

  handleLogInSubmit(): void {
    if(this.logInForm.pristine) return;
    const request: AuthenticationRequest = {
      email: this.logInForm.value.email,
      password: this.logInForm.value.password,
    };
    this.userAuthService.logIn(request).subscribe({
      next: (_ : any) => this.showLoggedIn = true,
      error: (_ : any) => console.error("Error logging in")
    });
    this.logInForm.reset();
  }

  toggleSettings(){
    this.showSettings = !this.showSettings;
  }

  toggleShowPassword(){
    this.showPassword = !this.showPassword;
  }

  logOut(){
    this.userAuthService.logOut();
    this.showLoggedIn = false;
  }
}
