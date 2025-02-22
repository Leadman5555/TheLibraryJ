import {tapResponse} from '@ngrx/operators'
import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, of, switchMap, tap, withLatestFrom} from 'rxjs';
import {BookPage} from '../../../shared/paging/book/book-page';
import {BookPreviewComponentStore} from '../../../shared/paging/book/bookPreview.component-store';
import {logError} from '../../../shared/errorHandling/handleError';
import {HttpErrorResponse, HttpParams} from '@angular/common/http';
import {FormOutcome} from '../../book-filter/filterService/form-outcome';
import {BookPreview} from '../../shared/models/book-preview';


@Injectable({
  providedIn: 'root'
})
export class BookViewComponentStore extends BookPreviewComponentStore {
  private readonly currentFiltersValue: BehaviorSubject<HttpParams | null> = new BehaviorSubject<HttpParams | null>(null);

  constructor() {
    const defaultInitialState: BookPage = {
      content: [],
      pageInfo: {
        page: 0,
        totalPages: 0,
        keysetPage: {
          firstResult: 0,
          maxResults: 28,
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
      }
    };
    super(defaultInitialState);
  }


  public readonly onFilterSelectionChange = this.effect((filterChange$: Observable<FormOutcome>) => {
    return filterChange$.pipe(
      withLatestFrom(this.select((state) => state.content)),
      tap((data: [FormOutcome, BookPreview[]]) => {
        const newContent: BookPreview[] | null = BookViewComponentStore.getNewContent(data[0], data[1]);
        this.currentFiltersValue.next(data[0].params);
        if (newContent) this.replaceAllPageContent(newContent);
        else this.onResetToInitialState();
      }))
  });

  private static getNewContent(outcome: FormOutcome, currentContent: BookPreview[]): BookPreview[] | null {
    if (outcome.isValid && !outcome.isRedirected) {
      if (outcome.sortAsc !== undefined) {
        let compareF;
        if (outcome.sortAsc) compareF = (a: BookPreview, b: BookPreview) => a.averageRating - b.averageRating;
        else compareF = (a: BookPreview, b: BookPreview) => b.averageRating - a.averageRating;
        return currentContent.filter(outcome.predicate).sort(compareF);
      } else return currentContent.filter(outcome.predicate);
    }
    return null;
  }

  private recalculatePageCount(newContentLength: number): number{
    const maxResults = this.initialState.pageInfo.keysetPage.maxResults;
    return maxResults >= newContentLength ? 1 : Math.ceil(newContentLength / maxResults);
  }

  override readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) => {
          const currentFilters = this.currentFiltersValue.value;
          if (!currentFilters) return of(this.initialState);
          return this.bookService.getBookPreviewsByParamsPageByOffset(currentFilters, currentState.pageInfo.page, currentState.pageInfo.keysetPage.maxResults).pipe(
            tapResponse(
              (newBookPage: BookPage) => this.updatePage(newBookPage),
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
      switchMap((currentState) => {
          const currentFilters = this.currentFiltersValue.value;
          if (!currentFilters) return of(this.initialState);
          return this.bookService.getBookPreviewsByParamsPageByKeySet(this.currentFiltersValue.value, currentState.pageInfo.page, currentState.pageInfo.keysetPage).pipe(
            tapResponse(
              (newBookPage: BookPage) => this.updatePage(newBookPage),
              () => this.loadPageByOffset()
            )
          )
        }
      )
    );
  });
}
