import {tapResponse} from '@ngrx/operators'
import {inject, Injectable} from '@angular/core';
import {BookService} from '../../shared/book-service';
import {asyncScheduler, map, Observable, scheduled, switchMap, withLatestFrom} from 'rxjs';
import {ChapterPreviewPage} from './chapterPreview-page';
import {GenericComponentStore} from '@app/shared/paging/generic.component-store';
import {ChapterPreview} from '../../shared/models/chapter-preview';
import {HttpErrorResponse} from '@angular/common/http';
import {logError} from '@app/shared/errorHandling/handleError';


@Injectable({
  providedIn: 'root'
})
export class ChapterPreviewComponentStore extends GenericComponentStore<ChapterPreview, ChapterPreviewPage> {
  private readonly bookService = inject(BookService);

  constructor() {
    const initialState: ChapterPreviewPage = {
      content: [],
      pageInfo: {
        page: 0,
        totalPages: 0,
        keysetPage: {
          firstResult: 0,
          maxResults: 30,
          lowest: {
            number: 0,
            id: ''
          },
          highest: {
            number: 0,
            id: ''
          },
          keysets: []
        }
      },
      bookId: '',
    }
    super(initialState);
  }

  readonly updateBookId = this.updater(
    (state: ChapterPreviewPage, bookId: string) => ({
      ...state,
      bookId: bookId
    })
  );

  protected override readonly updatePageNumber = this.updater(
    (state: ChapterPreviewPage, page: number) => ({
      ...state,
      pageInfo: {
        ...state.pageInfo,
        page: page
      }
    })
  );

  override readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) => {
          if (currentState.bookId === '') return scheduled([], asyncScheduler)
          else return this.bookService.getChapterPreviewsPageByOffset(currentState.bookId, currentState.pageInfo.page ?? this.initialState.pageInfo.page, currentState.pageInfo.keysetPage?.maxResults ?? this.initialState.pageInfo.keysetPage.maxResults).pipe(
            tapResponse(
              (newChapterPreviewPage: ChapterPreviewPage) => {
                this.updatePage(newChapterPreviewPage);
              },
              (error: HttpErrorResponse) => logError(error)
            )
          )
        }
      )
    );
  });

 override readonly loadPageByKeySet = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getChapterPreviewsPageByKeySet(currentState.bookId, currentState.pageInfo.page, currentState.pageInfo.keysetPage).pipe(
          tapResponse(
            (newChapterPreviewPage: ChapterPreviewPage) => {
              this.updatePage(newChapterPreviewPage);
            },
            () => this.loadPageByOffset()
          )
        )
      )
    );
  });
}
