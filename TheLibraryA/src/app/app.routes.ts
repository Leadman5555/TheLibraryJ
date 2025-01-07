import { Routes } from '@angular/router';
import { HomeComponent} from './home/home/home.component';
import {BookComponent} from './book/book/book.component';
import {ChapterComponent} from './book/chapter/chapter.component';
import {BookFilterComponent} from './book/book-filter/filterBox/book-filter.component';
import {BookViewComponent} from './book/book-view/book-view.component';
import {AppComponent} from './app.component';
import {AuthCallbackComponent} from './googleOAuth2/auth-callback/auth-callback.component';
import {
  PasswordRecoveryComponent
} from './tokenServices/password-recovery/password-recovery.component';
import {AccountActivationComponent} from './tokenServices/account-activation/account-activation.component';
import {RegisterComponent} from './register/register.component';

export const routes: Routes = [
  { path: '', component: HomeComponent},
  {
    path: 'book/:title',
    component: BookComponent,
  },
  {path : 'book/:title', component : BookComponent},
  {path:  'book/:title/chapter/:chapterNumber/:bookId', component : ChapterComponent },
  {path: 'filter', component:  BookViewComponent},
  {path: 'profile', component: AppComponent},
  {path: 'register', component: RegisterComponent},
  {path: 'oauth2Callback', component: AuthCallbackComponent},
  {path: 'password-recovery', component: PasswordRecoveryComponent},
  {path: 'activate-account', component: AccountActivationComponent},
  {path: '*', component: HomeComponent}
];
