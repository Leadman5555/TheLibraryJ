import {ComponentStore, OnStoreInit} from '@ngrx/component-store';
import {map, Observable, Subscription, tap, withLatestFrom} from 'rxjs';
import {PageInfo} from './models/page-info';
import {GenericPage} from './models/generic-page';
import {PagingHelper} from './paging-helper';

export abstract class GenericComponentStore<TS, T extends GenericPage<TS>> extends ComponentStore<T> implements OnStoreInit, PagingHelper {
  protected readonly initialState: T;
  readonly vm$ = this.select((state: T) => (state.content));
  readonly info$ = this.select((state: T) => (state.pageInfo));

  protected constructor(initialState: T) {
    super(initialState);
    this.initialState = initialState;
  }

  public onPreviousPage(): void {
    this.loadPreviousPage();
  }

  public onNextPage(): void {
    this.loadNextPage();
  }

  public onChosenPage(pageNumber: number): void {
    this.loadSpecifiedPage(pageNumber);
  }

  public onResetToInitialState(): void {
    this.resetToInitialState();
  }

  ngrxOnStoreInit() {
    this.loadPageByOffset();
  }

  protected readonly updatePage = this.updater((_: T, currentPage: T): T => (currentPage));

  protected readonly replaceAllPageContent = this.updater((currentPage: T, newContent: TS[]): T =>
    ({
      ...currentPage,
      pageInfo: {
        page: 0,
        totalPages: 1,
      },
      content: newContent,
    })
  );

  protected abstract readonly updatePageNumber: (observableOrValue: (number | Observable<number>)) => Subscription;

  protected abstract readonly loadPageByOffset: (observableOrValue?: (void | Observable<void> | undefined)) => Subscription;

  protected abstract readonly loadPageByKeySet: (observableOrValue?: (void | Observable<void> | undefined)) => Subscription;

  private readonly loadNextPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([, info]) => info),
      tap((pageInfo: PageInfo) => {
        if (pageInfo.page < pageInfo.totalPages - 1) {
          this.updatePageNumber(pageInfo.page + 1);
          if (pageInfo.keysetPage) this.loadPageByKeySet();
          else this.loadPageByOffset();
        }
      }))
  });

  private readonly loadPreviousPage = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([, info]) => info.page),
      tap((previousPageNumber: number) => {
        if (previousPageNumber > 0) {
          this.updatePageNumber(previousPageNumber - 1);
          this.loadPageByOffset();
        }
      }))
  });

  private readonly loadSpecifiedPage = this.effect((pageNumber$: Observable<number>) => {
    return pageNumber$.pipe(
      withLatestFrom(this.select((state) => state.pageInfo)),
      map(([pageNumber, pageInfo]) => [pageNumber, pageInfo.page, pageInfo.totalPages]),
      tap((pageData: number[]) => {
        if (pageData[0] !== pageData[1] && pageData[0] <= pageData[2] - 1) {
          this.updatePageNumber(pageData[0]);
          if (pageData[0] === pageData[1] + 1) this.loadPageByKeySet();
          else this.loadPageByOffset();
        }
      }))
  });

  private readonly resetToInitialState = this.effect((trigger$: Observable<void>) => {
    return trigger$.pipe(
      map(() => this.initialState.pageInfo.page),
      tap((pageNumber: number) => {
        this.updatePageNumber(pageNumber);
        this.loadPageByOffset();
      })
    )
  });

}
