import {tapResponse} from '@ngrx/operators'
import {Injectable} from '@angular/core';
import {map, Observable, switchMap, withLatestFrom} from 'rxjs';
import {BookPage} from '../../../shared/paging/book/book-page';
import {BookPreviewComponentStore} from '../../../shared/paging/book/bookPreview.component-store';
import {logError} from '../../../shared/errorHandling/handleError';
import {HttpErrorResponse} from '@angular/common/http';



@Injectable({
  providedIn: 'root'
})
export class HomeComponentStore extends BookPreviewComponentStore {

  constructor() {
    super();
  }

  override readonly loadPageByOffset = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getBookPreviewsPageByOffset(currentState.pageInfo.page, currentState.pageInfo.keysetPage.maxResults).pipe(
          tapResponse(
            (newBookPage: BookPage) => {
              this.updatePage(newBookPage);
            },
            (error: HttpErrorResponse) => logError(error)
          )
        )
      )
    );
  });

  override readonly loadPageByKeySet = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state)),
      map(([, state]) => state),
      switchMap((currentState) =>
        this.bookService.getBookPreviewsPageByKeySet(currentState.pageInfo.page, currentState.pageInfo.keysetPage).pipe(
          tapResponse(
            (newBookPage: BookPage) => this.updatePage(newBookPage),
            () => this.loadPageByOffset()
          )
        )
      )
    );
  });
}
