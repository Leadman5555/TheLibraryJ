import {ComponentStore, OnStoreInit} from '@ngrx/component-store';
import {tapResponse} from '@ngrx/operators'
import {inject, Injectable} from '@angular/core';
import {BookService} from '../../book/book-service';
import {map, Observable, switchMap, tap, withLatestFrom} from 'rxjs';
import {BookPage} from './paging/book-page';
import {KeysetPage} from './paging/keyset-page';
import {PageInfo} from './paging/page-info';

export interface HomeState {
  currentPage: BookPage;
}
//index 0
const initialState: HomeState = {
  currentPage: {
    content: [],
    pageInfo: {
      page: 0,
      totalPages: 0,
      keysetPage: {
        firstResult: 0,
        maxResults: 3,
        lowest : {
          chapterCount : 0,
          bookId : ''
        },
        highest: {
          chapterCount: 0,
          bookId : ''
        },
        keysets: []
      }
    }
  },
}

@Injectable()
export class HomeComponentStore extends ComponentStore<HomeState> implements OnStoreInit {
  private readonly bookService = inject(BookService);

  readonly vm$ = this.select((state: HomeState) => (state.currentPage.content));
  readonly info$ = this.select((state: HomeState) => (state.currentPage.pageInfo));

  constructor() {
    super(initialState);
  }

  ngrxOnStoreInit() {
    this.loadPageByOffset();
  }

  private readonly updatePage = this.updater(
    (state: HomeState, page: number) => ({
      currentPage: {
        ...state.currentPage,
        pageInfo: {
          ...state.currentPage.pageInfo,
          page: page
        }
      }
    })
  );

  private readonly updatePreviews = this.updater(
    (state: HomeState, currentPage: BookPage) => ({
      currentPage
    })
  )

  readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getBookPreviewsPageByOffset(currentState.currentPage.pageInfo.page, currentState.currentPage.pageInfo.keysetPage.maxResults).pipe(
          tapResponse(
            (newBookPage: BookPage) => {
              this.updatePreviews(newBookPage);
            },
            () => console.error("Something went wrong")
          )
        )
      )
    );
  });

  readonly loadPageByKeyset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getBookPreviewsPageByKeySet(currentState.currentPage.pageInfo.page, currentState.currentPage.pageInfo.keysetPage).pipe(
          tapResponse(
            (newBookPage: BookPage) => {
              this.updatePreviews(newBookPage);
            },
            () => this.loadPageByOffset()
          )
        )
      )
    );
  });

  readonly loadNextPage = this.effect((trigger$: Observable<void>) => {
    // console.log(this.select((state) => state.currentPage).subscribe(v => {
    //   console.log(v.page);
    //   console.log(v.content)
    // }));

    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.currentPage.pageInfo)),
      map(([, info]) => info),
      tap((pageInfo: PageInfo) => {
        if (pageInfo.page < pageInfo.totalPages - 1) {
          this.updatePage(pageInfo.page + 1);
          if(pageInfo.keysetPage) this.loadPageByKeyset();
          else this.loadPageByOffset();
        }
      }))
  });

  readonly loadPreviousPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.currentPage.pageInfo)),
      map(([, info]) => info.page),
      tap((previousPageNumber: number) => {
        if (previousPageNumber > 0) {
          this.updatePage(previousPageNumber - 1);
          this.loadPageByOffset();
        }
      }))
  });

  loadSpecifiedPage(pageNumber: number) {
    if (pageNumber < 1) return;
    this.effect((trigger$: Observable<void>) => {
      return trigger$.pipe(
        withLatestFrom(this.select((state) => state.currentPage.pageInfo.totalPages)),
        map(([, totalPages]) => totalPages),
        tap((totalPages: number) => {
          if (pageNumber <= totalPages - 1) {
            this.updatePage(pageNumber);
            this.loadPageByOffset();
          }
        }))
    });
  }
}
