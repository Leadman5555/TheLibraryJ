import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {UserProfileData} from '../shared/user-profile-data';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {handleError} from '@app/shared/errorHandling/handleError';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {BookService} from '@app/book/shared/book-service';
import {catchError, Subscription, switchMap} from 'rxjs';
import {BookPreview} from '@app/book/shared/models/book-preview';
import {BookPreviewCardComponent} from '@app/book/book-preview-card/book-preview-card.component';
import {ProgressBarComponent} from '@app/shared/progress-bar/progress-bar.component';
import {findTitle, progressArray, rankArray} from '../shared/rankTitles';
import {UserProfileService} from '../user-profile.service';
import {parseDateString} from '@app/shared/functions/parseDate';

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
  styleUrl: './user-profile.component.css',
  standalone: true,
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
                this.userFetchErrorMsg = error;
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
        this.authoredBooks = previews.sort((a, b) => b.chapterCount - a.chapterCount);
      }
    });
  }

  protected readonly findTitle = findTitle;
  protected readonly parseDateString = parseDateString;
  protected readonly rankArray = rankArray;
  protected readonly progressArray = progressArray;
}
