import {Component, OnInit} from '@angular/core';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import {UserProfileService} from '@app/user/profile/user-profile.service';
import {BookPreview} from '@app/book/shared/models/book-preview';
import {BookTokenResponse} from '@app/user/profile/shared/dto/book-token-response';
import {BookTokenConsummationRequest} from '@app/user/profile/shared/dto/book-token-consummation-request';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {BookPreviewCardComponent} from '@app/book/book-preview-card/book-preview-card.component';
import {hoursFromNowAsHourString} from '@app/shared/functions/parseDate';

@Component({
  selector: 'app-favourite-books',
  imports: [
    BookPreviewCardComponent,
    ReactiveFormsModule
  ],
  templateUrl: './favourite-books.component.html',
  styleUrl: './favourite-books.component.css'
})
export class FavouriteBooksComponent implements OnInit {

  constructor(private userAuthService: UserAuthService, private userProfileService: UserProfileService, private fb: FormBuilder) {
  }

  userLoggedIn: boolean = false;
  favouriteBooks?: BookPreview[];
  errorFetchingMessage!: string;
  errorGeneratingMessage!: string;
  favouriteBookToken?: BookTokenResponse;

  ngOnInit(): void {
    this.userLoggedIn = this.userAuthService.isLoggedIn();
    if (this.userLoggedIn) this.getFavouriteBooksForUser();
    else this.getFavouriteBooksForDevice();
  }

  private getFavouriteBooksForUser() {
    const email = this.userAuthService.getLoggedInEmail();
    if (!email) return;
    this.userProfileService.getFavouriteBooksForUser(email).subscribe({
      next: (v) => this.favouriteBooks = v,
      error: (error: string) => this.errorFetchingMessage = error
    });
  }

  private getFavouriteBooksForDevice() {
    this.userProfileService.getFavouriteBooksForDevice().subscribe({
      next: (v) => this.favouriteBooks = v,
      error: (error: string) => this.errorFetchingMessage = error
    });
  }

  generateFavouriteBookToken() {
    this.favouriteBookToken = undefined;
    if (!this.userLoggedIn) return;
    const email = this.userAuthService.getLoggedInEmail();
    if (!email) return;
    this.userProfileService.upsertAndGetFavouriteBookToken(email).subscribe({
      next: (v) => this.favouriteBookToken = v,
      error: (error: string) => this.errorGeneratingMessage = error
    });
  }

  sendTokenToEmail() {
    if (!this.userLoggedIn) return;
    const email = this.userAuthService.getLoggedInEmail();
    if (!email || !this.favouriteBookToken) return;
    const request: BookTokenConsummationRequest = {email: email, token: this.favouriteBookToken.token};
    this.userProfileService.sendExistingBookTokenToOwnerEmail(request).subscribe({
      next: (_) => alert('Token sent to ' + email),
      error: (error: string) => this.errorGeneratingMessage = error
    });
  }

  importForm?: FormGroup;
  importErrorMessage?: string;
  importSuccessMessage?: string;

  showImportForm() {
    this.importForm = this.fb.group({
      token: ['', [Validators.required, Validators.pattern(/^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i)]]
    });
    this.importErrorMessage = undefined;
    this.importSuccessMessage = undefined;
  }

  hideImportForm() {
    this.importForm = undefined;
    this.importErrorMessage = undefined;
    this.importSuccessMessage = undefined;
  }

  attemptImport() {
    this.importErrorMessage = undefined;
    this.importSuccessMessage = undefined;
    if (this.importForm!.pristine || this.importForm!.invalid) return;
    const email = this.userAuthService.getLoggedInEmail();
    if (!email) return;
    const request: BookTokenConsummationRequest = {email: email, token: this.importForm!.value.token};
    this.userProfileService.mergeFavouriteBooksUsingToken(request).subscribe({
      next: (mergerResponse) => {
        this.importSuccessMessage = `Token belonged to Daoist ${mergerResponse.fromUsername}. Attempted to merge ${mergerResponse.attemptedToMergeCount === 1 ? 'one book' : mergerResponse.attemptedToMergeCount + ' books'}  and your favourite collection grew from ${mergerResponse.sizeBeforeMerge} to ${mergerResponse.sizeAfterMerge === 1 ? 'one book' : mergerResponse.sizeAfterMerge + ' books'}.`;
        this.getFavouriteBooksForUser();
      },
      error: (error: string) => this.importErrorMessage = error
    });
  }

  getFavouriteBookCount(): string{
    if(!this.favouriteBooks) return '0 books';
    return this.favouriteBooks.length === 1 ? '1 book' : `${this.favouriteBooks.length} books`;
  }

  get isFormPristine(): boolean {
    return this.importForm!.pristine;
  }

  protected readonly hoursFromNowAsHourString = hoursFromNowAsHourString;
}
