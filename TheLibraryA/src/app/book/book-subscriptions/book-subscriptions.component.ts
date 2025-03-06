import {Component, OnInit} from '@angular/core';
import {BookPreviewCardComponent} from "@app/book/book-preview-card/book-preview-card.component";
import {ReactiveFormsModule} from "@angular/forms";
import {BookPreview} from '@app/book/shared/models/book-preview';
import {UserProfileService} from '@app/user/profile/user-profile.service';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import {NgOptimizedImage} from '@angular/common';

@Component({
  selector: 'app-book-subscriptions',
  imports: [
    BookPreviewCardComponent,
    ReactiveFormsModule,
    NgOptimizedImage
  ],
  templateUrl: './book-subscriptions.component.html',
  styleUrl: './book-subscriptions.component.css'
})
export class BookSubscriptionsComponent implements OnInit {

  constructor(private userAuthService: UserAuthService, private userProfileService: UserProfileService) {
  }
  subscribedBooks?: BookPreview[];
  errorFetchingMessage!: string;

  ngOnInit(): void {
      this.getSubscribedBooksForUser();
  }

  private getSubscribedBooksForUser() {
    const email = this.userAuthService.getLoggedInEmail();
    if (!email) return;
    this.userProfileService.getSubscribedBooksForUser(email).subscribe({
      next: (v) => this.subscribedBooks = v,
      error: (error: string) => this.errorFetchingMessage = error
    });
  }

  getSubscribedBookCount(): string{
    if(!this.subscribedBooks) return '0 books';
    return this.subscribedBooks.length === 1 ? '1 book' : `${this.subscribedBooks.length} books`;
  }
}
