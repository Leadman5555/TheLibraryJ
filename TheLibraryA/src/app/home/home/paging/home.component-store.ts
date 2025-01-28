import {tapResponse} from '@ngrx/operators'
import {inject, Injectable} from '@angular/core';
import {BookService} from '../../../book/shared/book-service';
import {map, Observable, switchMap, withLatestFrom} from 'rxjs';
import {BookPage} from './book-page';
import {GenericComponentStore} from '../../../shared/paging/generic.component-store';
import {BookPreview} from '../../../book/shared/models/book-preview';

const initialState: BookPage = {
  content: [],
  pageInfo: {
    page: 0,
    totalPages: 0,
    keysetPage: {
      firstResult: 0,
      maxResults: 16,
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
}

@Injectable({
  providedIn: 'root'
})
export class HomeComponentStore extends GenericComponentStore<BookPreview, BookPage> {
  private readonly bookService = inject(BookService);

  constructor() {
    super(initialState);
  }

  protected override readonly updatePage = this.updater(
    (state: BookPage, page: number) => ({
      ...state,
      pageInfo: {
        ...state.pageInfo,
        page: page
      }
    })
  );

  readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getBookPreviewsPageByOffset(currentState.pageInfo.page, currentState.pageInfo.keysetPage.maxResults).pipe(
          tapResponse(
            (newBookPage: BookPage) => {
              this.updateContent(newBookPage);
            },
            (error) => console.error("ESomething went wrong", error)
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
        this.bookService.getBookPreviewsPageByKeySet(currentState.pageInfo.page, currentState.pageInfo.keysetPage).pipe(
          tapResponse(
            (newBookPage: BookPage) => this.updateContent(newBookPage),
            () => this.loadPageByOffset()
          )
        )
      )
    );
  });
}
