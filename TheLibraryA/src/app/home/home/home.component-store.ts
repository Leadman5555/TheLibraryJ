import {ComponentStore, OnStoreInit} from '@ngrx/component-store';
import {tapResponse} from '@ngrx/operators'
import {inject, Injectable} from '@angular/core';
import {BookService} from '../../book/book-service';
import {map, Observable, switchMap, tap, withLatestFrom} from 'rxjs';
import {BookPage} from './BookPage';

export interface HomeState {
  currentPage: BookPage;
  pageSize: number;
}

const initialState: HomeState = {
  currentPage: {
    content: [],
    page: {
      number: 0,
      size: 0,
      totalElements: 0,
      totalPages: 0,
    }
  },
  pageSize: 2,
}

@Injectable()
export class HomeComponentStore extends ComponentStore<HomeState> implements OnStoreInit {
  private readonly bookService = inject(BookService);

  readonly vm$ = this.select((state: HomeState) => (state.currentPage.content));
  readonly info$ = this.select((state: HomeState) => (state.currentPage.page));

  constructor() {
    super(initialState);
  }

  ngrxOnStoreInit() {
    this.loadPage();
  }

  private readonly updatePage = this.updater(
    (state: HomeState, number: number) => ({
      ...state,
      currentPage: {
        ...state.currentPage,
        page: {
          ...state.currentPage.page,
          number: number
        }
      }
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
      switchMap(({currentPage, pageSize}) =>
        this.bookService.getBookPreviews(currentPage.page.number, pageSize).pipe(
          tapResponse(
            (newBookPage: BookPage) => {
              console.log(newBookPage);
              this.updatePreviews(newBookPage);
            },
            () => console.error("Something went wrong")
          )
        )
      )
    );
  });

  readonly loadNextPage = this.effect((trigger$: Observable<void>) => {
    console.log(this.select((state) => state.currentPage).subscribe(v => {
      console.log(v.page);
      console.log(v.content)
    }));

    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.currentPage.page)),
      map(([, info]) => [info.number, info.totalPages]),
      tap((paging: number[]) => {
        if (paging[0] < paging[1]) {
          this.updatePage(paging[0] + 1);
          this.loadPage();
        }
      }))
  });

  readonly loadPreviousPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.currentPage.page)),
      map(([, info]) => info.number),
      tap((number: number) => {
        if (number > 0) {
          this.updatePage(number - 1);
          this.loadPage();
        }
      }))
  });

  loadSpecifiedPage(pageNumber: number) {
    if (pageNumber < 0) return;
    this.effect((trigger$: Observable<void>) => {
      return trigger$.pipe(
        withLatestFrom(this.select((state) => state.currentPage.page.totalPages)),
        map(([, totalPages]) => totalPages),
        tap((totalPages: number) => {
          if (pageNumber <= totalPages) {
            this.updatePage(pageNumber);
            this.loadPage();
          }
        }))
    });
  }
}
