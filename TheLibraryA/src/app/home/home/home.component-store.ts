import {ComponentStore, OnStoreInit} from '@ngrx/component-store';
import {tapResponse} from '@ngrx/operators'
import {inject, Injectable} from '@angular/core';
import {BookService} from '../../book/book-service';
import {map, Observable, switchMap, tap, withLatestFrom} from 'rxjs';
import {BookPage} from './BookPage';

export interface HomeState {
  currentPage: BookPage;
  page: number;
  pageSize: number;
}

const initialState: HomeState = {
  currentPage: {
    content : [],
    pageInfo : {
      number: 0,
      size: 0,
      totalElements: 0,
      totalPages: 0,
    }
  },
  page: 0,
  pageSize: 20,
}

@Injectable()
export class HomeComponentStore extends ComponentStore<HomeState> implements OnStoreInit {
  private readonly bookService = inject(BookService);

  readonly vm$ = this.select((state : HomeState) => (state.currentPage.content));
  readonly info$ = this.select((state : HomeState) => (state.currentPage.pageInfo));

  constructor() {
    super(initialState);
  }

  ngrxOnStoreInit() {
    this.loadPage();
  }

  private readonly updatePage = this.updater(
    (state: HomeState, page: number) => ({
      ...state,
      page
    })
  );

  private readonly updatePreviews = this.updater(
    (state: HomeState, currentPage: BookPage) => ({
      ...state,
      currentPage
    })
  )

  readonly loadPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap(({page, pageSize}) =>
        this.bookService.getBookPreviews(page, pageSize).pipe(
          tapResponse(
            (newBookPage : BookPage) => this.updatePreviews(newBookPage),
            () => console.error("Something went wrong")
          )
        )
      )
    );
  });

  readonly loadNextPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.page)),
      map(([, state]) => state),
      tap((page: number) => this.updatePage(page + 1)),
      tap(() => this.loadPage()))
  });
}
