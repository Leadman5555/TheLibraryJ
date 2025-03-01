import {Routes} from '@angular/router';
import {HomeComponent} from './home/home/home.component';
import {BookComponent} from './book/book/book.component';
import {ChapterComponent} from './book/chapter/chapter.component';
import {BookViewComponent} from './book/book-view/book-view.component';
import {AuthCallbackComponent} from './user/account/googleOAuth2/auth-callback/auth-callback.component';
import {PasswordRecoveryComponent} from './user/account/tokenServices/password-recovery/password-recovery.component';
import {AccountActivationComponent} from './user/account/tokenServices/account-activation/account-activation.component';
import {RegisterComponent} from './user/account/register/register.component';
import {UserProfileComponent} from './user/profile/user-profile/user-profile.component';
import {UserProfileEditComponent} from './user/profile/user-profile-edit/user-profile-edit.component';
import {AuthGuard} from './user/account/userAuth/auth.guard';
import {AuthorTabComponent} from './book/author-tab/author-tab.component';
import {AuthorGuard} from './user/account/userAuth/author.guard';
import {AuthorTabCreateComponent} from './book/author-tab/author-tab-create/author-tab-create.component';
import {AuthorTabEditComponent} from './book/author-tab/author-tab-edit/author-tab-edit.component';
import {FavouriteBooksComponent} from '@app/book/favourite-books/favourite-books.component';
import {PavilionOfGloryComponent} from '@app/user/pavilion-of-glory/pavilion-of-glory.component';

export const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'book/:title', component: BookComponent},
  {path: 'book/:title/chapter/:chapterNumber/:bookId', component: ChapterComponent},
  {path: 'filter', component: BookViewComponent},
  {path: 'profile/:username', component: UserProfileComponent},
  {path: 'profile-edit', component: UserProfileEditComponent, canActivate: [AuthGuard]},
  {path: 'register', component: RegisterComponent},
  {path: 'oauth2Callback', component: AuthCallbackComponent},
  {path: 'password-recovery', component: PasswordRecoveryComponent},
  {path: 'activate-account', component: AccountActivationComponent},
  {path: 'favourite-books', component: FavouriteBooksComponent},
  {
    path: 'author-tab', component: AuthorTabComponent, canActivate: [AuthGuard, AuthorGuard],
    children: [
      {path: 'create', component: AuthorTabCreateComponent},
      {path: 'edit', component: AuthorTabEditComponent}
    ]
  },
  {path: 'pavilion-of-glory', component: PavilionOfGloryComponent},
  {path: '*', component: HomeComponent}
];

export const serverRoute: string = 'http://localhost:8082/v0.9';
export const serverAuthFreeRoute = serverRoute + '/na';
