import {tapResponse} from '@ngrx/operators'
import {Injectable} from '@angular/core';
import {BehaviorSubject, map, Observable, of, switchMap, tap, withLatestFrom} from 'rxjs';
import {BookPage} from '../../../shared/paging/book/book-page';
import {BookPreviewComponentStore} from '../../../shared/paging/book/bookPreview.component-store';
import {logError} from '../../../shared/errorHandling/handleError';
import {HttpErrorResponse, HttpParams} from '@angular/common/http';
import {ParamMap} from '@angular/router';
import {BookPreview} from '../../shared/models/book-preview';
import {allTagsAsString, BookTag} from '../../shared/models/BookTag';
import {FilterSelection} from '../../book-filter/filterBox/book-filter.component';

function and(...predicates: ((value: BookPreview) => boolean)[]): (value: BookPreview) => boolean {
  return (value: BookPreview) => predicates.every((predicate) => predicate(value));
}

type SanitizationResult = {
  sanitizedValue: FilterSelection,
  stricterFilters: boolean
  anyValue: boolean
}

type FilterParamPair = {
  currentFilter: SanitizationResult,
  currentParams?: HttpParams
}

const emptySanitationValue: SanitizationResult = {
  sanitizedValue: {
    titleLike: null,
    minChapters: null,
    sortAscByRating: null,
    minRating: null,
    hasTags: null,
  },
  stricterFilters: true,
  anyValue: false
}

const emptyFilterValue: FilterParamPair = {
  currentFilter: emptySanitationValue,
  currentParams: new HttpParams()
}

@Injectable({
  providedIn: 'root'
})
export class BookViewComponentStore extends BookPreviewComponentStore {

  private readonly currentFiltersValue: BehaviorSubject<FilterParamPair> = new BehaviorSubject<FilterParamPair>(emptyFilterValue);

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


  public readonly onFilterSelectionChange = this.effect((filterChange$: Observable<ParamMap>) => {
    return filterChange$.pipe(
      map(params => this.sanitizeParamsAndAssertIncrease(params)),
      withLatestFrom(this.select((state) => state)),
      tap((data: [SanitizationResult, BookPage]) => {
        if (!data[0].anyValue) {
          this.currentFiltersValue.next(emptyFilterValue);
          this.onResetToInitialState();
        } else if (!data[0].stricterFilters || data[1].pageInfo.totalPages > 1) {
          this.currentFiltersValue.next({
            currentFilter: data[0],
            currentParams: BookViewComponentStore.getNewHttpParams(data[0].sanitizedValue)
          });
          this.onResetToInitialState();
        } else if (data[0].stricterFilters && data[1].content.length === 0) return;
        else {
          this.currentFiltersValue.next({
            currentFilter: data[0]
          })
          this.replaceAllPageContent(BookViewComponentStore.filterCurrentContent(data[1].content, data[0].sanitizedValue))
        }
      }))
  });

  private sanitizeParamsAndAssertIncrease(params: ParamMap): SanitizationResult {
    const sanitizationResult: SanitizationResult = {
      ...emptySanitationValue
    };
    const previousFilters = this.currentFiltersValue.value.currentFilter.sanitizedValue;
    this.sanitizedAndGetTitleLikeValue(params.get('titleLike'), sanitizationResult, previousFilters);
    this.sanitizeAndGetMinChaptersValue(params.get('minChapters'), sanitizationResult, previousFilters);
    this.sanitizeAndGetMinRatingValue(params.get('minRating'), sanitizationResult, previousFilters);
    this.sanitizeAndGetSortByRatingValue(params.get('sortAscByRating'), sanitizationResult);
    this.sanitizeAndGetHasTagsValue(params.getAll('hasTags'), sanitizationResult, previousFilters);
    return sanitizationResult;
  }

  private sanitizedAndGetTitleLikeValue(stringValue: string | null, sanitizationResult: SanitizationResult, previousFilters: FilterSelection) {
    if (stringValue !== null && stringValue.length >= 3 && stringValue.length <= 20) {
      sanitizationResult.anyValue = true;
      if (previousFilters.titleLike !== null && !stringValue.startsWith(previousFilters.titleLike)) sanitizationResult.stricterFilters = false;
      sanitizationResult.sanitizedValue.titleLike = stringValue;
    }
  }

  private sanitizeAndGetMinChaptersValue(stringValue: string | null, sanitizationResult: SanitizationResult, previousFilters: FilterSelection) {
    if (stringValue !== null && stringValue.match(/^\d+$/)) {
      sanitizationResult.anyValue = true;
      if (previousFilters.minChapters !== null && parseInt(stringValue) < parseInt(previousFilters.minChapters)) sanitizationResult.stricterFilters = false;
      sanitizationResult.sanitizedValue.minChapters = stringValue;
    }
  }

  private sanitizeAndGetMinRatingValue(stringValue: string | null, sanitizationResult: SanitizationResult, previousFilters: FilterSelection) {
    if (stringValue !== null && stringValue.match(/^[0-9](\.[0-9]{1,2})?$/)) {
      sanitizationResult.anyValue = true;
      if (previousFilters.minRating !== null && Number(stringValue) < Number(previousFilters.minRating)) sanitizationResult.stricterFilters = false;
      sanitizationResult.sanitizedValue.minRating = stringValue;
    }
  }

  private sanitizeAndGetSortByRatingValue(stringValue: string | null, sanitizationResult: SanitizationResult) {
    if (stringValue !== null && (stringValue === 'true' || stringValue === 'false')) {
      sanitizationResult.anyValue = true;
      sanitizationResult.sanitizedValue.sortAscByRating = stringValue;
    }
  }

  private sanitizeAndGetHasTagsValue(stringValue: string[] | null, sanitizationResult: SanitizationResult, previousFilters: FilterSelection) {
    if (stringValue === null) return;
    let filtered = stringValue.filter(tag => allTagsAsString.includes(tag));
    if (filtered.length === 0) return;
    if (previousFilters.hasTags !== null && previousFilters.hasTags.some(tag => !filtered.includes(tag))) sanitizationResult.stricterFilters = false;
    sanitizationResult.sanitizedValue.hasTags = filtered as BookTag[];
    sanitizationResult.anyValue = true;
  }

  private static getNewHttpParams(newFilters: FilterSelection): HttpParams {
    let params = new HttpParams();
    if (newFilters.titleLike) params = params.append('titleLike', newFilters.titleLike);
    if (newFilters.minChapters) params = params.append('minChapters', newFilters.minChapters);
    if (newFilters.minRating) params = params.append('minRating', newFilters.minRating);
    if (newFilters.sortAscByRating) params = params.append('sortAscByRating', newFilters.sortAscByRating);
    if (newFilters.hasTags) newFilters.hasTags.forEach(tag => params = params.append('hasTags', tag));
    return params;
  }

  private static filterCurrentContent(currentContent: BookPreview[], filters: FilterSelection): BookPreview[] {
    let predicate = (_: BookPreview) => true;
    if (filters.hasTags) {
      filters.hasTags.forEach(tag => {
        predicate = and(predicate, (book: BookPreview) => book.bookTags.includes(tag))
      });
    }
    if (filters.titleLike) {
      // @ts-ignore
      predicate = and(predicate, (book: BookPreview) => book.title.startsWith(filters.titleLike));
    }
    if (filters.minChapters) {
      predicate = and(predicate, (book: BookPreview) => book.chapterCount >= Number(filters.minChapters));
    }
    if (filters.minRating) {
      predicate = and(predicate, (book: BookPreview) => book.averageRating >= Number(filters.minRating));
    }

    if (filters.sortAscByRating) {
      let compareF;
      if (filters.sortAscByRating === 'true') compareF = (a: BookPreview, b: BookPreview) => a.averageRating - b.averageRating;
      else compareF = (a: BookPreview, b: BookPreview) => b.averageRating - a.averageRating;
      return currentContent.filter(predicate).sort(compareF);
    }
    return currentContent.filter(predicate);
  }

  override readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) => {
          const currentFilters = this.currentFiltersValue.value;
          if (!currentFilters) return of(this.initialState);
          return this.bookService.getBookPreviewsByParamsPageByOffset(currentFilters.currentParams!, currentState.pageInfo.page ?? this.initialState.pageInfo.page, currentState.pageInfo.keysetPage?.maxResults ?? this.initialState.pageInfo.keysetPage.maxResults).pipe(
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
          return this.bookService.getBookPreviewsByParamsPageByKeySet(currentFilters.currentParams!, currentState.pageInfo.page, currentState.pageInfo.keysetPage).pipe(
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
