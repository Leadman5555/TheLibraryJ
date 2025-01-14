import {Component, OnInit} from '@angular/core';
import {FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {passwordMatchValidator} from '../tokenServices/password-recovery/passwordMatchValidator';
import {HttpClient} from '@angular/common/http';
import {catchError} from 'rxjs';
import {handleError} from '../shared/errorHandling/handleError';
import {UserCreationResponse} from './user-creation-response';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {UserAuthService} from '../user/user-auth.service';
import {RouterLink} from '@angular/router';

@Component({
  selector: 'app-register',
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink,
    NgOptimizedImage,
  ],
  templateUrl: './register.component.html',
  standalone: true,
  styleUrl: './register.component.css'
})
export class RegisterComponent implements OnInit {

  private readonly registerUrl: string = 'http://localhost:8082/v0.9/na/auth/register';

  constructor(private fb: NonNullableFormBuilder, private http: HttpClient, private userAuthService: UserAuthService) {
  }

  ngOnInit(): void {
    this.createRegisterForm();
  }

  registerForm!: FormGroup;

  errorMessage? : string = undefined;

  registerSuccess : boolean = false;
  createdUser?: UserCreationResponse;

  isDropZoneActive: boolean = false;
  imagePreview: string | ArrayBuffer | null = null;
  private readonly maxSize = 2 * 1024 * 1024;

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
        newPassword: ['', [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$'), Validators.maxLength(30)]],
        repeatPassword: ['', [Validators.required]],
        username: ['',
          [
            Validators.required,
            Validators.minLength(5),
            Validators.maxLength(20),
            Validators.pattern('^[a-zA-Z0-9_-]+$')
          ]
        ],
        profileImage: [null]
      },
      {validators: passwordMatchValidator()}
    );
  }

  resetForm() {
    this.errorMessage = undefined;
    this.imagePreview = null;
    this.registerForm.reset(this.defaultFormValues);
  }

  attemptRegistration(): void {
    if (this.registerForm.invalid) return;

    const formData = new FormData();
    formData.set('email', this.registerForm.value.email);
    formData.set('password', this.registerForm.value.newPassword);
    formData.set('username', this.registerForm.value.username);
    formData.set('profileImage', this.registerForm.value.profileImage);

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

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = true;
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDropZoneActive = false;
    if(event.dataTransfer && event.dataTransfer.files.length > 0) {
      const file = event.dataTransfer.files[0];
      this.processFile(file);
      event.dataTransfer.clearData();
    }
  }

  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if(input.files && input.files[0]) this.processFile(input.files[0]);
  }

  private processFile(file: File): void {

    if (!file.type.startsWith('image/')) {
      alert('Only image types are supported.');
      return;
    }else if (file.size > this.maxSize) {
      alert(`The selected file exceeds the maximum size of ${this.maxSize/(1024*1024)} MB.`);
      return;
    }

    this.registerForm.patchValue({profileImage: file});
    this.registerForm.get('profileImage')?.updateValueAndValidity();

    const reader = new FileReader();
    reader.onload = () => {
      this.imagePreview = reader.result;
    };
    reader.readAsDataURL(file);
  }

  logInWithGoogle() {
    this.userAuthService.getGoogleLogInLink().subscribe({
      next: (linkResponse) => window.location.href = linkResponse.authLink,
      error: (err) => console.error("Google login unavailable", err)
    });
  }
}
