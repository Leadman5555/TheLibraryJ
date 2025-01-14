import {Component, inject, OnInit} from '@angular/core';
import {BookService} from '../shared/book-service';
import {BookPreview} from '../shared/models/book-preview';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {BookDetail} from '../shared/models/book-detail';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {RatingResponse} from '../shared/models/rating-response';
import {TimesMaxPagingPipe} from '../../shared/pipes/times-max-paging.pipe';
import {ChapterPreviewComponentStore} from './paging/chapterPreview.component-store';
import {PagingHelper} from '../../shared/paging/paging-helper';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {Observable} from 'rxjs';
import {PageInfo} from '../../shared/paging/models/page-info';
import {provideComponentStore} from '@ngrx/component-store';

@Component({
  selector: 'app-book',
  imports: [RouterLink, NgIf, AsyncPipe, NgForOf, TimesMaxPagingPipe],
  providers: [
    provideComponentStore(ChapterPreviewComponentStore)
  ],
  templateUrl: './book.component.html',
  standalone: true,
  styleUrl: './book.component.css'
})
export class BookComponent extends PagingHelper implements OnInit {
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private defaultRoute: string = '';
  bookPreview!: BookPreview;
  bookDetail!: BookDetail;
  ratings!: RatingResponse[];

  private readonly componentStore: ChapterPreviewComponentStore = inject(ChapterPreviewComponentStore);
  readonly vm$: Observable<ChapterPreview[]> = this.componentStore.vm$;
  readonly info$: Observable<PageInfo> = this.componentStore.info$;
  private bookService: BookService = inject(BookService);

  constructor(private router: Router) {
    super();
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      const state = navigation.extras.state as { bp: BookPreview };
      this.bookPreview = state.bp;
    }
  }

  ngOnInit() {
    if (this.bookPreview) {
      this.bookService.getBookDetail(this.bookPreview.id).subscribe({
        next: (v) => this.bookDetail = v,
        error: (_) => this.router.navigate([`book`, this.bookPreview.title]),
      });
      this.fetchChapterPreviews();
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
            this.fetchChapterPreviews();
          },
          error: (e) => {
            console.error(e);
            this.router.navigate([this.defaultRoute]);
          }
        });
      } else this.router.navigate([this.defaultRoute]);
    }
  }

  private fetchChapterPreviews(){
    this.componentStore.updateBookId(this.bookPreview.id);
    this.componentStore.loadPageByOffset();
  }

  fetchRatings() {
    this.bookService.getRatingsForBook(this.bookPreview.id).subscribe({
      next: (v) => {
        this.ratings = v;
      },
      error: (_) => console.error("Error fetched ratings"),
    })
  }

  upsertRating(){

  }

  parseDate(date: string): string {
    const splitIndex = date.indexOf('T');
    const calendarParts = date.substring(0, splitIndex).split('-');
    return `${calendarParts[0]}-${calendarParts[1].padStart(2, '0')}-${calendarParts[2].padStart(2, '0')} | ${date.substring(splitIndex + 1, splitIndex + 6)}`;
  }

  onPreviousPage(): void {
    this.componentStore.loadPreviousPage();
  }

  onNextPage(): void {
    this.componentStore.loadNextPage();
  }

  onChosenPage(pageNumber: number){
    this.componentStore.loadSpecifiedPage(pageNumber);
  }
}
