import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {passwordMatchValidator} from './passwordMatchValidator';
import {handleError} from '@app/shared/errorHandling/handleError';
import {catchError} from 'rxjs';

@Component({
  selector: 'app-password-recovery',
  imports: [
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './password-recovery.component.html',
  standalone: true,
  styleUrl: './password-recovery.component.css'
})
export class PasswordRecoveryComponent implements OnInit {
  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
  }

  private readonly BASE_URL = 'http://localhost:8082/v0.9/na/auth/password';

  tokenValue?: string;
  successReset?: boolean = undefined;
  resetErrorMessage? : string = undefined;
  successSend?: boolean = undefined;
  sendErrorMessage? : string = undefined;
  passwordResetForm?: FormGroup;
  emailInputForm?: FormGroup;
  showPassword: boolean = false;

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  ngOnInit(): void {
    const tokenValue = this.activatedRoute.snapshot.paramMap.get('token');
    if (tokenValue) {
      if(!tokenValue.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)){
        alert('Token does not match required format.');
        this.generateEmailForm();
      }
      this.tokenValue = tokenValue;
      this.passwordResetForm = new FormGroup({
          newPassword: new FormControl('', {validators: [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$/), Validators.maxLength(30)]}),
          repeatPassword: new FormControl('', {validators: [Validators.required]})
        },
        {validators: passwordMatchValidator()});
    } else {
      this.generateEmailForm();
    }
  }

  private generateEmailForm(){
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }

  get passwordMismatchError(): boolean {
    return this.passwordResetForm?.errors?.['passwordMismatch'] ?? false;
  }

  get isPasswordFormPristine(): boolean {
    return this.passwordResetForm!.pristine;
  }

  get isEmailFormPristine(): boolean {
    return this.emailInputForm!.pristine;
  }

  get isPasswordFormTouched(): boolean {
    return this.passwordResetForm!.touched;
  }


  sendPasswordResetEmail() {
    this.successSend = undefined;
    this.sendErrorMessage = undefined;
    if (!this.emailInputForm || this.emailInputForm.pristine) return;
    this.http.post(this.BASE_URL + '/' + this.emailInputForm.value.email, null).pipe(catchError(handleError)).subscribe({
      next: () => {
        this.emailInputForm = undefined;
        this.successSend = true;
      },
      error: (error) => {
        this.successSend = false;
        this.sendErrorMessage = error;
      }
    });
    this.emailInputForm.reset();
  }

  attemptPasswordReset() {
    this.successReset = undefined;
    this.resetErrorMessage = undefined;
    if (!this.passwordResetForm || this.passwordResetForm.pristine || this.passwordResetForm.value.newPassword !== this.passwordResetForm.value.repeatPassword) return;
    const body = {tokenId: this.tokenValue!, newPassword: this.passwordResetForm.value.newPassword};
    this.http.patch(this.BASE_URL, body).pipe(catchError(handleError)).subscribe({
      next: () => {
        this.successReset = true;
        this.passwordResetForm = undefined;
        this.tokenValue = undefined;
      },
      error: (error) => {
        this.successReset = false;
        this.resetErrorMessage = error;
      }
    });
    this.passwordResetForm.reset();
  }

  reload(){
    this.successReset = undefined;
    this.successSend = undefined;
    this.tokenValue = undefined;
    this.passwordResetForm = undefined;
    this.resetErrorMessage = undefined;
    this.sendErrorMessage = undefined;
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }

}
