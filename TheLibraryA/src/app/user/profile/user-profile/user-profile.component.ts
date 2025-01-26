import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {UserProfileData} from '../shared/user-profile-data';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {handleError, logAndExtractMessage, logError} from '../../../shared/errorHandling/handleError';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {BookService} from '../../../book/shared/book-service';
import {catchError, Subscription, switchMap} from 'rxjs';
import {BookPreview} from '../../../book/shared/models/book-preview';
import {BookPreviewCardComponent} from '../../../book/book-preview-card/book-preview-card.component';
import {ProgressBarComponent} from '../../../shared/progress-bar/progress-bar.component';
import {findTitle, preferenceArray, progressArray, rankArray} from '../shared/rankTitles';
import {UserProfileService} from '../user-profile.service';
import {parseDateString} from '../../../shared/functions/parseData';

@Component({
  selector: 'app-user-profile',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    BookPreviewCardComponent,
    ProgressBarComponent
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})

export class UserProfileComponent implements OnInit, OnDestroy {

  constructor(private activatedRoute: ActivatedRoute, private userProfileService: UserProfileService) {
  }

  private routeSubscription!: Subscription;

  ngOnDestroy(): void {
    if (this.routeSubscription) this.routeSubscription.unsubscribe();
  }

  ngOnInit(): void {
    this.routeSubscription = this.activatedRoute.paramMap.pipe(
      switchMap((params) => {
        const username = params.get('username');
        this.resetUserState();
        if (username)
          return this.userProfileService.fetchUserProfile(username)
            .pipe(catchError((error) => {
                this.userFetchErrorMsg = logAndExtractMessage(error);
                return [];
              }
            ));
        return []
      }), catchError(handleError))
      .subscribe({
        next: (userProfile: UserProfileData | null) => {
          if (userProfile) this.userData = userProfile;
        }
      });
  }

  private resetUserState(): void {
    this.userData = null;
    this.userFetchErrorMsg = null;
    this.authoredBooks = undefined;
  }

  userData: UserProfileData | null = null;
  userFetchErrorMsg: string | null = null;

  bookService: BookService = inject(BookService);
  authoredBooks?: BookPreview[];

  fetchAuthoredBooks() {
    if (!this.userData) return;
    this.bookService.getBookPreviewsByAuthor(this.userData.username).subscribe({
      next: (previews) => {
        this.authoredBooks = previews;
      }
    });
  }

  protected readonly findTitle = findTitle;
  protected readonly parseDateString = parseDateString;
  protected readonly rankArray = rankArray;
  protected readonly progressArray = progressArray;
}
