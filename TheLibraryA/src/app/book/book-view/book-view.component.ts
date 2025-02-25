import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {BookFilterComponent} from "../book-filter/filterBox/book-filter.component";
import {BookPreviewCardComponent} from "../book-preview-card/book-preview-card.component";
import {Observable, Subscription} from 'rxjs';
import {BookViewComponentStore} from './paging/BookView.component-store';
import {provideComponentStore} from '@ngrx/component-store';
import {TimesMaxPagingPipe} from '../../shared/pipes/times-max-paging.pipe';
import {ActivatedRoute} from '@angular/router';
import { AsyncPipe } from '@angular/common';
import {BookPreview} from '../shared/models/book-preview';
import {PageInfo} from '../../shared/paging/models/page-info';

@Component({
  selector: 'app-book-view',
  imports: [
    BookFilterComponent,
    BookPreviewCardComponent,
    TimesMaxPagingPipe,
    AsyncPipe
  ],
  providers: [
    provideComponentStore(BookViewComponentStore)
  ],
  templateUrl: './book-view.component.html',
  standalone: true,
  styleUrl: './book-view.component.css'
})
export class BookViewComponent implements OnInit, OnDestroy{
  private readonly componentStore = inject(BookViewComponentStore);
  readonly vm$: Observable<BookPreview[]> = this.componentStore.vm$;
  readonly info$: Observable<PageInfo> = this.componentStore.info$;
  private filterSubscription!: Subscription;

  constructor(private activatedRoute: ActivatedRoute) {
  }

  ngOnDestroy(): void {
    if (this.filterSubscription) this.filterSubscription.unsubscribe();
  }

  onPreviousPage(): void {
    this.componentStore.onPreviousPage();
  }

  onNextPage(): void {
    this.componentStore.onNextPage();
  }

  onChosenPage(pageNumber: number) {
    this.componentStore.onChosenPage(pageNumber);
  }

  ngOnInit(): void {
    setTimeout(
      () => this.filterSubscription = this.activatedRoute.queryParamMap.subscribe(newFilters => this.componentStore.onFilterSelectionChange(newFilters)),
      10
    )//it prevents double server call on tag redirect
  }

  identifyPage(_: number, item: number) {
    return item;
  }
}
