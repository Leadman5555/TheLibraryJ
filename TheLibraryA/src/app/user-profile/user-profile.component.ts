import {HttpClient} from '@angular/common/http';
import {Component, inject, OnInit} from '@angular/core';
import {UserProfileData} from './user-profile-data';
import {parseDateArray, parseDateString} from '../shared/functions/parseData';
import {NgIf} from '@angular/common';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {handleError, logError} from '../shared/errorHandling/handleError';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {BookService} from '../book/shared/book-service';
import {catchError} from 'rxjs';
import {BookPreview} from '../book/shared/models/book-preview';
import {BookPreviewCardComponent} from '../book/book-preview-card/book-preview-card.component';

@Component({
  selector: 'app-user-profile',
  imports: [
    NgIf,
    FormsModule,
    ReactiveFormsModule,
    RouterLink,
    BookPreviewCardComponent
  ],
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.css'
})

export class UserProfileComponent implements OnInit {

  private readonly baseUrl: string = 'http://localhost:8082/v0.9/na';

  private readonly baseAuthUrl: string = 'http://localhost:8082/v0.9';

  constructor(private http: HttpClient, private activatedRoute: ActivatedRoute) {
    this.userSearchForm = new FormGroup({
      username: new FormControl('',
        [
          Validators.required,
          Validators.minLength(5),
          Validators.maxLength(20),
          Validators.pattern('^[a-zA-Z0-9_-]+$')
        ])
    });
  }

  ngOnInit(): void {
    const username: string = this.activatedRoute.snapshot.params['username'];
    if (username)
      this.http.get<UserProfileData>(this.baseUrl + '/user/' + username).pipe(catchError(handleError)).subscribe({
          next: (userProfile) => {
            this.userData = userProfile;
            this.fetchedUser = true;
          },
          error: (error) => {
            this.userFetchErrorMsg = error;
          }
        }
      )
  }

  readonly preferenceArray: string[] = ['Junior disciple', 'Senior disciple'];
  readonly rankArray: string[] = ['Mortal', 'Qi condensation'];
  readonly progressArray: number[] = [3, 5, 10, 20, 40, 60, 100, 200, 500, 1000, 3333];

  userData!: UserProfileData;
  userFetchErrorMsg?: string = undefined;
  fetchedUser : boolean = false;

  protected readonly parseDate = parseDateArray;
  userSearchForm: FormGroup;

  searchForUser() {
    if (this.userSearchForm.invalid || this.userSearchForm.pristine) return;
    this.http.get<UserProfileData>(this.baseUrl + '/user/' + this.userSearchForm.value.username).pipe(catchError(handleError)).subscribe({
      next: (userProfile) => {
        this.userData = userProfile;
        this.userFetchErrorMsg = undefined;
        this.fetchedUser = true;
      },
      error: (error) => {
        this.userFetchErrorMsg = error;
        this.fetchedUser = false;
      }
    });
    this.userSearchForm.reset();
  }

  bookService: BookService = inject(BookService);
  authoredBooks?: BookPreview[];
  bookFetchErrorMsg? : string = undefined;

  fetchAuthoredBooks() {
    if(!this.fetchedUser) return;
    this.bookService.getBookPreviewsByAuthor(this.userData.username).pipe(catchError(handleError)).subscribe({
      next: (previews) => {
        this.authoredBooks = previews;
      },
        error: (error) => {
        this.bookFetchErrorMsg = error;
      }
    });
  }

}
