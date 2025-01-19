import {HttpClient} from '@angular/common/http';
import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {UserProfileData} from './user-profile-data';
import {parseDateArray} from '../shared/functions/parseData';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {handleError, logAndExtractMessage, logError} from '../shared/errorHandling/handleError';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {BookService} from '../book/shared/book-service';
import {catchError, Subscription, switchMap} from 'rxjs';
import {BookPreview} from '../book/shared/models/book-preview';
import {BookPreviewCardComponent} from '../book/book-preview-card/book-preview-card.component';
import {ProgressBarComponent} from '../shared/progress-bar/progress-bar.component';

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

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';

  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
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
          return this.http.get<UserProfileData>(this.baseUrl + '/user/' + username)
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
  }

  readonly preferenceArray: string[] = ['Junior disciple', 'Senior disciple'];
  readonly rankArray: string[] = ['Mortal', 'Qi condensation'];
  readonly progressArray: number[] = [3, 5, 10, 20, 40, 60, 100, 200, 500, 1000, 3333];

  userData: UserProfileData | null = null;
  userFetchErrorMsg: string | null = null;

  protected readonly parseDate = parseDateArray;

  bookService: BookService = inject(BookService);
  authoredBooks?: BookPreview[];

  fetchAuthoredBooks() {
    if (!this.userData) return;
    this.bookService.getBookPreviewsByAuthor(this.userData.username).subscribe({
      next: (previews) => {
        this.authoredBooks = previews;
      },
      error: (error) => {
        logError(error);
      }
    });
  }

}
