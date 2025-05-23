import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {handleError} from '@app/shared/errorHandling/handleError';
import {catchError} from 'rxjs';
import {serverAuthFreeRoute} from '@app/app.routes';

@Component({
  selector: 'app-account-activation',
  imports: [
    ReactiveFormsModule,
    RouterLink,
  ],
  templateUrl: './account-activation.component.html',
  standalone: true,
  styleUrl: './account-activation.component.css'
})
export class AccountActivationComponent implements OnInit {
  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
  }

  private readonly BASE_URL = `${serverAuthFreeRoute}/auth/activation`;

  successActivation?: boolean = undefined;
  activationErrorMessage? : string = undefined;
  successSend?: boolean = undefined;
  sendErrorMessage? : string = undefined;
  emailInputForm?: FormGroup;
  tokenValue?: string = undefined;


  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe((params) => {
      const tokenValue = params.get('token');
      if (tokenValue) {
        if(!tokenValue.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)){
          alert('Token does not match required format.');
          this.generateEmailForm();
        }
        else this.tokenValue = tokenValue;
      } else {
        this.generateEmailForm();
      }
    });
  }

  private generateEmailForm(){
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }

  attemptAccountActivation() {
    this.successActivation = undefined;
    this.activationErrorMessage = undefined;
    if (this.tokenValue)
      this.http.patch(this.BASE_URL, null, {params: {token: this.tokenValue}}).subscribe({
        next: () => {
          this.successActivation = true;
        },
        error: (error) => {
          this.successActivation = false;
          this.sendErrorMessage = error;
        }
      });
    this.tokenValue = undefined;
  }

  sendAccountActivationEmail() {
    this.successSend = undefined;
    this.sendErrorMessage = undefined;
    if (!this.emailInputForm || this.emailInputForm.pristine) return;
    this.http.post(this.BASE_URL, null, {params: {email: this.emailInputForm.value.email}}).pipe(catchError(handleError)).subscribe({
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

  reload() {
    this.successActivation = undefined;
    this.successSend = undefined;
    this.tokenValue = undefined;
    this.activationErrorMessage = undefined;
    this.sendErrorMessage = undefined;
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }

  get isFormPristine(): boolean {
    return this.emailInputForm!.pristine;
  }
}
