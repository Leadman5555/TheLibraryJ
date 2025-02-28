import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {passwordMatchValidator} from '../tokenServices/password-recovery/passwordMatchValidator';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs';
import {handleError} from '@app/shared/errorHandling/handleError';
import {UserCreationResponse} from './user-creation-response';
import {UserAuthService} from '../userAuth/user-auth.service';
import {RouterLink} from '@angular/router';
import {ImageDropComponent} from '@app/shared/image-drop/image-drop.component';
import {imageFileTypeValidator} from '@app/shared/functions/fileTypeValidator';
import { NgOptimizedImage } from '@angular/common';
import {serverAuthFreeRoute} from '@app/app.routes';

@Component({
  selector: 'app-register',
  imports: [
    ReactiveFormsModule,
    RouterLink,
    ImageDropComponent,
    NgOptimizedImage
  ],
  templateUrl: './register.component.html',
  standalone: true,
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  private readonly registerUrl: string = `${serverAuthFreeRoute}/auth/register`;

  constructor(private fb: NonNullableFormBuilder, private http: HttpClient, private userAuthService: UserAuthService) {
  }

  ngOnInit(): void {
    this.createRegisterForm();
  }

  registerForm!: FormGroup;

  errorMessage? : string = undefined;

  registerSuccess : boolean = false;
  createdUser?: UserCreationResponse;

  showVersion: boolean = true;
  toggleShowVersion() {
    this.showVersion = !this.showVersion;
  }

  showPassword: boolean = false;
  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }
  get passwordMismatchError(): boolean {
    return this.registerForm?.errors?.['passwordMismatch'] ?? false;
  }

  get isFormPristine(): boolean {
    return this.registerForm.pristine;
  }

  get isFormTouched(): boolean {
    return this.registerForm.touched;
  }


  private defaultFormValues = {
    email: '',
    newPassword: '',
    repeatPassword: '',
    username: '',
    profileImage: null
  };

  private createRegisterForm() {
    this.registerForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        newPassword: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$/), Validators.maxLength(30)]],
        repeatPassword: ['', [Validators.required]],
        username: ['',
          [
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(20),
            Validators.pattern(/^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9_-]+$/)
          ]
        ],
        profileImage: [null, imageFileTypeValidator()]
      },
      {validators: passwordMatchValidator()}
    );
  }

  resetForm() {
    this.errorMessage = undefined;
    this.registerForm.reset(this.defaultFormValues);
  }

  attemptRegistration(): void {
    console.log(this.registerForm.errors)
    if (this.registerForm.invalid || this.registerForm.pristine) return;

    const formData = new FormData();
    const values = this.registerForm.value;
    formData.set('email', values.email);
    formData.set('password', values.newPassword);
    formData.set('username', values.username);
    formData.set('profileImage', values.profileImage);

    this.http.post<UserCreationResponse>(this.registerUrl,  formData).pipe(catchError(handleError)).subscribe({
      next: (response) => {
        this.registerSuccess = true;
        this.createdUser = response;
        this.resetForm();
      },
      error: (err : string) => {
        this.errorMessage = err;
      }
    });
  }

  get profileImageControl(): FormControl {
    return this.registerForm.get('profileImage')! as FormControl;
  }

  logInWithGoogle() {
    this.userAuthService.getGoogleLogInLink().subscribe({
      next: (linkResponse) => window.location.href = linkResponse.authLink,
      error: (err) => console.error("Google login unavailable", err)
    });
  }
}
