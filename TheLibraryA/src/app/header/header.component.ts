import {afterNextRender, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {EventBusService, LOGIN_EVENT, LOGOUT_EVENT, REFRESH_EVENT} from '@app/shared/eventBus/event-bus.service';
import {StorageService} from '@app/shared/storage/storage.service';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import { UserMini } from '@app/user/shared/models/user-mini';
import {Subscription} from 'rxjs';
import {AuthenticationRequest} from '@app/user/shared/models/authentication-request';
import {logError} from '@app/shared/errorHandling/handleError';
import {RouterLink} from '@angular/router';
import {UserSearchComponent} from '@app/user/profile/user-search/user-search.component';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [
    RouterLink,
    ReactiveFormsModule,
    UserSearchComponent,
    NgOptimizedImage
  ],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {
  constructor(private fb: NonNullableFormBuilder,
              private eventBus: EventBusService,
              private storageService: StorageService,
              private userAuthService: UserAuthService,
              private changeDetectorRef: ChangeDetectorRef) {
    afterNextRender(() => {
      this.showLoggedIn = this.storageService.isLoggedIn();
      if (this.showLoggedIn) {
        this.userMini = this.storageService.getUserMini();
        this.subscribeToLogOut();
        changeDetectorRef.detectChanges();
      } else this.subscribeToLogIn();
    });
  }

  ngOnInit(): void {
    this.logInForm = this.fb.group({
      email: ['', [Validators.email, Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  private eventBusSubscription?: Subscription;
  showMenu: boolean = false;
  showOptions: boolean = false;
  showLoggedIn: boolean = false;
  showPassword: boolean = false;
  logInForm!: FormGroup;
  userMini?: UserMini;

  errorMessage?: string;

  handleLogInSubmit(): void {
    if (this.logInForm.pristine) return;
    const request: AuthenticationRequest = {
      email: this.logInForm.value.email,
      password: this.logInForm.value.password,
    };
    this.userAuthService.logIn(request).subscribe({
      error: (error) => {
        this.errorMessage = error || 'An unknown error occurred!';
      },
      complete: () => {
        this.hideWindows();
        this.resetForm();
      }
    });
  }

  resetForm(){
    this.errorMessage = undefined;
    this.showPassword = false;
    this.logInForm.reset();
  }

  toggleMenu() {
    this.showMenu = !this.showMenu;
    this.showOptions = false;
  }

  toggleOptions() {
    this.showOptions = !this.showOptions;
    this.showMenu = false;
  }

  hideWindows(){
    this.showMenu = false;
    this.showOptions = false;
  }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  logOut() {
    this.userAuthService.logOut().subscribe({
        next: () => {
          console.log('User logged out.');
          this.subscribeToLogIn();
          this.showLoggedIn = false;
          this.errorMessage = undefined;
          window.location.reload();
        },
        error: err => logError(err),
      }
    );
  }

  logIn() {
    this.subscribeToLogOut();
    this.showLoggedIn = true;
    this.errorMessage = undefined;
    this.userMini = this.storageService.getUserMini();
    window.location.reload();
  }

  private subscribeToLogIn(): void {
    this.eventBusSubscription?.unsubscribe();
    this.eventBusSubscription = this.eventBus.on(LOGIN_EVENT, () => this.logIn());
  }

  private subscribeToLogOut(): void {
    this.eventBusSubscription?.unsubscribe();
    this.eventBusSubscription = this.eventBus.onMultiple([{eventName: LOGOUT_EVENT, action: () => this.logOut()}, {eventName: REFRESH_EVENT, action: () => this.refreshMini()}]);
  }

  private refreshMini() {
    this.userMini = this.storageService.getUserMini();
  }

  logInWithGoogle() {
    this.userAuthService.getGoogleLogInLink().subscribe({
      next: (linkResponse) => window.location.href = linkResponse.authLink,
      error: (err) => console.error("Google login unavailable", err)
    });
  }

  get isFormPristine(): boolean {
    return this.logInForm!.pristine;
  }
}
