import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {logError} from '../../shared/errorHandling/handleError';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-account-activation',
  imports: [
    NgIf,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './account-activation.component.html',
  standalone: true,
  styleUrl: './account-activation.component.css'
})
export class AccountActivationComponent implements OnInit {
  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
  }

  private readonly BASE_URL = 'http://localhost:8082/v0.9/na/auth/activation';

  successActivation?: boolean = undefined;
  successSend?: boolean = undefined;
  emailInputForm?: FormGroup;
  tokenValue?: string = undefined;


  ngOnInit(): void {
    this.activatedRoute.paramMap.subscribe((params) => {
      const tokenValue = params.get('token');
      if (tokenValue) {
        this.tokenValue = tokenValue;
      } else {
        this.emailInputForm = new FormGroup({
          email: new FormControl('', {validators: [Validators.required, Validators.email]})
        });
      }
    });
  }

  attemptAccountActivation() {
    if (this.tokenValue)
      this.http.patch(this.BASE_URL, null, {params: {tokenId: this.tokenValue}}).subscribe({
        next: () => {
          this.successActivation = true;
        },
        error: (error) => {
          this.successActivation = false;
          logError(error);
        }
      });
    this.tokenValue = undefined;
  }

  sendAccountActivationEmail() {
    if (!this.emailInputForm || this.emailInputForm.pristine) return;
    this.http.post(this.BASE_URL, null, {params: {email: this.emailInputForm.value.email}}).subscribe({
      next: () => {
        this.emailInputForm = undefined;
        this.successSend = true;
      },
      error: (error) => {
        this.successSend = false;
        logError(error);
      }
    });
    this.emailInputForm.reset();
  }

  reload() {
    this.successActivation = undefined;
    this.successSend = undefined;
    this.tokenValue = undefined;
    this.emailInputForm = new FormGroup({
      email: new FormControl('', {validators: [Validators.required, Validators.email]})
    });
  }
}
