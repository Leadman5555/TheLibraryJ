import {Component, inject} from '@angular/core';
import {BookPreview} from '@app/book/shared/models/book-preview';
import {ReactiveFormsModule} from '@angular/forms';
import {BookPreviewCardComponent} from '@app/book/book-preview-card/book-preview-card.component';
import {provideComponentStore} from '@ngrx/component-store';
import {HomeComponentStore} from './paging/home.component-store';
import {TimesMaxPagingPipe} from '@app/shared/pipes/times-max-paging.pipe';
import {BookFilterComponent} from '@app/book/book-filter/filterBox/book-filter.component';
import { AsyncPipe } from '@angular/common';
import {Observable} from 'rxjs';
import {PageInfo} from '@app/shared/paging/models/page-info';


@Component({
  selector: 'app-home',
  imports: [
    ReactiveFormsModule, AsyncPipe, BookPreviewCardComponent, TimesMaxPagingPipe, BookFilterComponent
  ],
  providers: [
    provideComponentStore(HomeComponentStore)
  ],
  templateUrl: './home.component.html',
  standalone: true,
  styleUrl: './home.component.css'
})
export class HomeComponent {
  private readonly componentStore = inject(HomeComponentStore);
  readonly vm$: Observable<BookPreview[]> = this.componentStore.vm$;
  readonly info$: Observable<PageInfo> = this.componentStore.info$;

  onPreviousPage(): void {
    this.componentStore.onPreviousPage();
  }

  onNextPage(): void {
    this.componentStore.onNextPage();
  }

  onChosenPage(pageNumber: number){
    this.componentStore.onChosenPage(pageNumber);
  }

  identifyPage(_: number, item: number) {
    return item;
  }
}
