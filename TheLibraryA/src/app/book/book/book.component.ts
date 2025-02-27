import {Component, inject, OnInit} from '@angular/core';
import {BookService} from '../shared/book-service';
import {BookPreview} from '../shared/models/book-preview';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {BookDetail} from '../shared/models/book-detail';
import {RatingResponse} from '../shared/models/rating-response';
import {TimesMaxPagingPipe} from '@app/shared/pipes/times-max-paging.pipe';
import {ChapterPreviewComponentStore} from './paging/chapterPreview.component-store';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {Observable} from 'rxjs';
import {PageInfo} from '@app/shared/paging/models/page-info';
import {provideComponentStore} from '@ngrx/component-store';
import {parseDateString} from '@app/shared/functions/parseDate';
import {logError} from '@app/shared/errorHandling/handleError';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from '@angular/forms';
import {RangeSelectorComponent} from '@app/shared/range-selector/range-selector.component';
import {AsyncPipe, NgOptimizedImage} from '@angular/common';
import {UserProfileService} from '@app/user/profile/user-profile.service';


@Component({
  selector: 'app-book',
  imports: [RouterLink, TimesMaxPagingPipe, ReactiveFormsModule, AsyncPipe, FormsModule, RangeSelectorComponent, NgOptimizedImage],
  providers: [
    provideComponentStore(ChapterPreviewComponentStore)
  ],
  templateUrl: './book.component.html',
  standalone: true,
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private defaultRoute: string = '';
  bookPreview!: BookPreview;
  bookDetail!: BookDetail;
  ratings?: RatingResponse[];

  isBookInFavourites: boolean = false;

  private readonly componentStore: ChapterPreviewComponentStore = inject(ChapterPreviewComponentStore);
  readonly vm$: Observable<ChapterPreview[]> = this.componentStore.vm$;
  readonly info$: Observable<PageInfo> = this.componentStore.info$;
  private bookService: BookService = inject(BookService);

  constructor(private router: Router, private userAuthService: UserAuthService, private userProfileService: UserProfileService) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      const state = navigation.extras.state as { bp: BookPreview };
      this.bookPreview = state.bp;
    }
  }

  ngOnInit() {
    if (this.bookPreview) {
      this.bookService.getBookDetail(this.bookPreview.id).subscribe({
        next: (v) => {
          this.bookDetail = v;
          this.loadIsFavourite();
          this.fetchChapterPreviews();
          },
        error: (_) => this.router.navigate([`book`, this.bookPreview.title]),
      });
    } else {
      const title: string = this.activatedRoute.snapshot.params['title'];
      if (title) {
        this.bookService.getBook(title).subscribe({
          next: (v) => {
            this.bookPreview = {
              id: v.id,
              title: v.title,
              bookTags: v.bookTags,
              averageRating: v.averageRating,
              chapterCount: v.chapterCount,
              coverImage: v.coverImage,
              ratingCount: v.ratingCount,
              bookState: v.bookState,
            };
            this.bookDetail = {
              author: v.author,
              description: v.description,
            };
            this.loadIsFavourite();
            this.fetchChapterPreviews();
          },
          error: (e) => {
            logError(e);
            this.router.navigate([this.defaultRoute]);
          }
        });
      } else this.router.navigate([this.defaultRoute]);
    }
  }

  private loadIsFavourite(){
    if(this.userAuthService.isLoggedIn()) this.isBookInFavourites = this.userProfileService.isBookInLoggedFavourites(this.bookPreview.id);
    else this.isBookInFavourites = this.userProfileService.isBookInFavourites(this.bookPreview.id);
  }

  private fetchChapterPreviews() {
    this.componentStore.updateBookId(this.bookPreview.id);
    this.componentStore.loadPageByOffset();
  }

  fetchRatings(): Observable<never> {
    return new Observable<never>((observer) => {
      this.bookService.getRatingsForBook(this.bookPreview.id).subscribe({
        next: (v) => {
          this.ratings = v;
          observer.complete();
        },
        error: (err) => {
          logError(err);
          observer.error();
        },
      });
    });
  }

  protected ratingUpsertMessage?: string = undefined;
  protected ratingUpsertForm!: FormGroup;
  protected showRatingUpsertForm: boolean = false;
  protected previousRating?: RatingResponse;
  protected ratingValues: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10];

  startUpsertRatingProcedure() {
    if (!this.userAuthService.isLoggedIn()) {
      alert("Please log in to rate the book.");
      return;
    }
    if (this.ratings === undefined)
      this.fetchRatings().subscribe({
        complete: () => this.createRatingUpsertForm()
      });
    else this.createRatingUpsertForm();
  }

  private createRatingUpsertForm() {
    const loggedUser = this.userAuthService.getLoggedInUsername();
    if(!loggedUser){
      this.router.navigate(['']);
      return;
    }
    const foundRating = this.ratings!.find(rating => rating.username === loggedUser);
    if (foundRating !== undefined) {
      this.ratingUpsertForm = new FormGroup({
        currentRating: new FormControl(foundRating.currentRating, {
          validators: [
            Validators.required,
            Validators.min(1),
            Validators.max(10)
          ]
        }),
        comment: new FormControl(foundRating.comment, {
          validators: [
            Validators.maxLength(200)
          ]
        })
      });
      this.previousRating = foundRating;
    } else {
      this.ratingUpsertForm = new FormGroup({
        currentRating: new FormControl(5, {
          validators: [
            Validators.required,
            Validators.min(1),
            Validators.max(10)
          ]
        }),
        comment: new FormControl('', {
          validators: [
            Validators.maxLength(200)
          ]
        })
      });
    }
    this.ratingUpsertMessage = undefined;
    this.showRatingUpsertForm = true;
  }

  upsertRating() {
    if (this.ratingUpsertForm.invalid) return;
    const userEmail = this.userAuthService.getLoggedInEmail();
    if(!userEmail){
      this.router.navigate(['']);
      return;
    }
    this.ratingUpsertMessage = undefined;
    const values = this.ratingUpsertForm.value;
    if (this.previousRating !== undefined) {
      if (values.currentRating === this.previousRating.currentRating && (values.comment === this.previousRating.comment)) return;
    }
    this.bookService.upsertRatingForBook({
      currentRating: values.currentRating,
      comment: values.comment,
      bookId: this.bookPreview.id,
      userEmail: userEmail
    })
      .subscribe({
        next: (v) => {
          if(this.previousRating !== undefined) {
            this.ratings = this.ratings!.filter(rating => rating.username !== this.previousRating!.username);
            this.ratings.push(v);
          }else this.ratings!.push(v);
          this.previousRating = undefined
          this.showRatingUpsertForm = false;
        },
        error: (error) => this.ratingUpsertMessage = error
      })
  }

  get rangeSelectorControl(): FormControl {
    return this.ratingUpsertForm.get('currentRating') as FormControl;
  }

  closeRatingUpsertForm() {
    this.ratingUpsertForm.reset();
    this.showRatingUpsertForm = false;
  }

  onPreviousPage(): void {
    this.componentStore.onPreviousPage();
  }

  onNextPage(): void {
    this.componentStore.onNextPage();
  }

  onChosenPage(pageNumber: number) {
    this.componentStore.onChosenPage(pageNumber);
  }

  identifyPage(_: number, item: number) {
    return item;
  }

  protected readonly parseDateString = parseDateString;

  addBookToFavourites(){
    if(this.isBookInFavourites) return;
    if(this.userAuthService.isLoggedIn()){
      const email = this.userAuthService.getLoggedInEmail();
      if(!email) return;
      this.userProfileService.addBookToUserFavourites(this.bookPreview.id, email).subscribe({
        next: (totalCount) => {
          this.isBookInFavourites = true;
          alert('Book added to favourites. You have ' + (totalCount === 1 ? 'one book' : totalCount + ' books')  + ' in favourites.');
        },
        error: (error) => alert("Adding book failed. Details: " + error)
      });
    }else{
      const deviceCount = this.userProfileService.addBookToDeviceFavourites(this.bookPreview.id);
      this.isBookInFavourites = true;
      alert('Book added to favourites. Log in to access it on all devices. You have ' + (deviceCount === 1 ? 'one book' : deviceCount + ' books')  + ' in favourites locally.');
    }
  }

  removeBookFromFavourites(){
    if(!this.isBookInFavourites) return;
    if(this.userAuthService.isLoggedIn()){
      const email = this.userAuthService.getLoggedInEmail();
      if(!email) return;
      this.userProfileService.removeBookFromUserFavourites(this.bookPreview.id, email).subscribe({
        next: () => {
          this.isBookInFavourites = false;
          alert("Book removed from favourites successfully.");
        },
        error: (error) => alert("Removing book from favourites failed. Details: " + error)
      });
    }else{
      this.userProfileService.removeBookFromDeviceFavourites(this.bookPreview.id);
      alert("Book removed from favourites locally. Please log in to have access to favourite books on all devices.");
      this.isBookInFavourites = false;
    }
  }
}
