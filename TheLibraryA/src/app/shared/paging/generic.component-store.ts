import {ComponentStore, OnStoreInit} from '@ngrx/component-store';
import {map, Observable, Subscription, tap, timer, withLatestFrom} from 'rxjs';
import {PageInfo} from './models/page-info';
import {GenericPage} from './models/generic-page';

export abstract class GenericComponentStore<TS, T extends GenericPage<TS>> extends ComponentStore<T> implements OnStoreInit{

  readonly vm$ = this.select((state: T) => (state.content));
  readonly info$ = this.select((state: T) => (state.pageInfo));

  protected constructor(initialState: T) {
    super(initialState);
  }

  ngrxOnStoreInit() {
    this.loadPageByOffset();
  }

  protected abstract readonly updatePage : (observableOrValue: (number | Observable<number>)) => Subscription;

  protected readonly updateContent = this.updater(
    (_: T, currentPage: T) => (currentPage));

  abstract readonly loadPageByOffset :  (observableOrValue?: (void | Observable<void> | undefined)) => Subscription;

  abstract readonly loadPageByKeyset :  (observableOrValue?: (void | Observable<void> | undefined)) => Subscription;

  readonly loadNextPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([, info]) => info),
      tap((pageInfo: PageInfo) => {
        if (pageInfo.page < pageInfo.totalPages - 1) {
          this.updatePage(pageInfo.page + 1);
          if (pageInfo.keysetPage) this.loadPageByKeyset();
          else this.loadPageByOffset();
        }
      }))
  });

  readonly loadPreviousPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([, info]) => info.page),
      tap((previousPageNumber: number) => {
        if (previousPageNumber > 0) {
          this.updatePage(previousPageNumber - 1);
          this.loadPageByOffset();
        }
      }))
  });

  readonly loadSpecifiedPage = this.effect((pageNumber$: Observable<number>) => {
    return pageNumber$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([pageNumber, pageInfo]) => [pageNumber, pageInfo.page, pageInfo.totalPages]),
      tap((pageData: number[]) => {
        if (pageData[0] !== pageData[1] && pageData[0] <= pageData[2] - 1) {
          this.updatePage(pageData[0]);
          if(pageData[0] === pageData[1]+1) this.loadPageByKeyset();
          else this.loadPageByOffset();
        }
      }))
  });
}
