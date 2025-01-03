import {Component, OnInit} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule, Validators
} from '@angular/forms';
import {NgIf} from '@angular/common';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {passwordMatchValidator} from './passwordMatchValidator';

@Component({
  selector: 'app-password-recovery',
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './password-recovery.component.html',
  styleUrl: './password-recovery.component.css'
})
export class PasswordRecoveryComponent implements OnInit {
  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
  }

  private readonly BASE_URL = 'http://localhost:8082/v0.9/na/auth/password';

  tokenValue?: string;
  successReset?: boolean = undefined;
  successSend?: boolean = undefined;
  passwordResetForm?: FormGroup;
  emailInputForm?: FormGroup;
  showPassword: boolean = false;

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  ngOnInit(): void {
    const tokenValue = this.activatedRoute.snapshot.paramMap.get('token');
    if (tokenValue) {
      this.tokenValue = tokenValue;
      this.passwordResetForm = new FormGroup({
          newPassword: new FormControl('', {validators: [Validators.required, Validators.minLength(8), Validators.pattern('^(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9]).*$'), Validators.maxLength(30)]}),
          repeatPassword: new FormControl('', {validators: [Validators.required]})
        },
        {validators: passwordMatchValidator()});
    } else {
      this.emailInputForm = new FormGroup({
        email: new FormControl('', {validators: [Validators.required, Validators.email]})
      });
    }
  }

  get passwordMismatchError(): boolean {
    // @ts-ignore
    return this.passwordResetForm.errors?.['passwordMismatch'] ?? false;
  }

  sendPasswordResetEmail() {
    // @ts-ignore
    if (this.emailInputForm.pristine) return;
    // @ts-ignore
    this.http.post(this.BASE_URL + '/' + this.emailInputForm.value.email, null).subscribe({
      next: () => {
        this.emailInputForm = undefined;
        this.successSend = true;
      },
      error: (error) => {
        this.successSend = false;
        console.error("Error sending password reset email", error);
      }
    });
    // @ts-ignore
    this.emailInputForm.reset();
  }

  attemptPasswordReset() {
    // @ts-ignore
    if (this.passwordResetForm.pristine || this.passwordResetForm.value.newPassword !== this.passwordResetForm.value.repeatPassword) return;
    // @ts-ignore
    const body = {tokenId: this.tokenValue!, newPassword: this.passwordResetForm.value.newPassword};
    this.http.patch(this.BASE_URL, body).subscribe({
      next: () => {
        this.successReset = true;
        this.passwordResetForm = undefined;
        this.tokenValue = undefined;
      },
      error: (error) => {
        this.successReset = false;
        console.error("Error resetting password", error);
      }
    });
    // @ts-ignore
    this.passwordResetForm.reset();
  }

  reload(){
    this.successReset = undefined;
    this.successSend = undefined;
    this.tokenValue = undefined;
    this.passwordResetForm = undefined;
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }

}
