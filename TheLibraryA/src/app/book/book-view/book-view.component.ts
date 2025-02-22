import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BookFilterComponent} from "../book-filter/filterBox/book-filter.component";
import {BookPreviewCardComponent} from "../book-preview-card/book-preview-card.component";
import {BookPreview} from '../shared/models/book-preview';
import {BookService} from '../shared/book-service';
import {BookFilterService} from '../book-filter/filterService/book-filter.service';
import {catchError, skip, Subscription, timer} from 'rxjs';
import {handleError, logError} from '../../shared/errorHandling/handleError';
import {BookViewComponentStore} from './paging/BookView.component-store';
import {provideComponentStore} from '@ngrx/component-store';
import {TimesMaxPagingPipe} from '../../shared/pipes/times-max-paging.pipe';
import {log} from 'node:util';

@Component({
  selector: 'app-book-view',
  imports: [
    BookFilterComponent,
    BookPreviewCardComponent,
    NgForOf,
    AsyncPipe,
    NgIf,
    TimesMaxPagingPipe
  ],
  providers: [
    provideComponentStore(BookViewComponentStore)
  ],
  templateUrl: './book-view.component.html',
  standalone: true,
  styleUrl: './book-view.component.css'
})
export class BookViewComponent implements OnInit, OnDestroy {
  private readonly componentStore = inject(BookViewComponentStore);
  readonly vm$ = this.componentStore.vm$;
  readonly info$ = this.componentStore.info$;
  private filterSubscription!: Subscription;

  constructor(private filterService: BookFilterService) {
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
      () => this.filterSubscription = this.filterService.currentForm$.subscribe(newFilters => this.componentStore.onFilterSelectionChange(newFilters)),
      10
    )//it prevents double server call on tag redirect
  }

  identifyBp(_: number, item: BookPreview) {
    return item.title;
  }

  identifyPage(_: number, item: number) {
    return item;
  }
}
