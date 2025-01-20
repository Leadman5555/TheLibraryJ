import {afterNextRender, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {RouterLink, RouterOutlet} from '@angular/router';
import {UserAuthService} from './user/userAuth/user-auth.service';
import {FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {NgIf, NgOptimizedImage} from '@angular/common';
import {AuthenticationRequest} from './user/shared/models/authentication-request';
import {UserMini} from './user/shared/models/user-mini';
import {EventBusService} from './shared/eventBus/event-bus.service';
import {Subscription} from 'rxjs';
import {StorageService} from './shared/storage/storage.service';
import {UserSearchComponent} from './user/profile/user-search/user-search.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, NgIf, ReactiveFormsModule, NgOptimizedImage, UserSearchComponent],
  templateUrl: './app.component.html',
  standalone: true,
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {

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
        console.log(error)
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
          console.log('Logged out.');
          this.subscribeToLogIn();
          this.showLoggedIn = false;
          this.errorMessage = undefined;
          window.location.reload();
        },
        error: err => console.log(err),
      }
    );
  }

  logIn() {
    this.subscribeToLogOut();
    this.showLoggedIn = true;
    this.errorMessage = undefined;
    this.userMini = this.storageService.getUserMini();
  }

  private subscribeToLogIn(): void {
    this.eventBusSubscription?.unsubscribe();
    this.eventBusSubscription = this.eventBus.on('login', () => this.logIn());
  }

  private subscribeToLogOut(): void {
    this.eventBusSubscription?.unsubscribe();
    this.eventBusSubscription = this.eventBus.on('logout', () => this.logOut());
  }

  logInWithGoogle() {
    this.userAuthService.getGoogleLogInLink().subscribe({
      next: (linkResponse) => window.location.href = linkResponse.authLink,
      error: (err) => console.error("Google login unavailable", err)
    });
  }

}
