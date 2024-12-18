import {Component, inject} from '@angular/core';
import {BookPreview} from '../../book/shared/models/book-preview';
import {
  ReactiveFormsModule
} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {BookPreviewCardComponent} from '../../book/book-preview-card/book-preview-card.component';
import {provideComponentStore} from '@ngrx/component-store';
import {HomeComponentStore} from './paging/home.component-store';
import {TimesMaxPipe} from '../../shared/pipes/times-max.pipe';
import {BookFilterComponent} from '../../book/book-filter/filterBox/book-filter.component';
import {PagingHelper} from '../../shared/paging/paging-helper';

@Component({
  selector: 'app-home',
  imports: [
    ReactiveFormsModule, NgIf, NgForOf, BookPreviewCardComponent, AsyncPipe, TimesMaxPipe, BookFilterComponent
  ],
  providers: [
    provideComponentStore(HomeComponentStore)
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent extends PagingHelper {
  private readonly componentStore = inject(HomeComponentStore);
  readonly vm$ = this.componentStore.vm$;
  readonly info$ = this.componentStore.info$;

  onPreviousPage(): void {
    this.componentStore.loadPreviousPage();
  }

  onNextPage(): void {
    this.componentStore.loadNextPage();
  }

  onChosenPage(pageNumber: number){
    this.componentStore.loadSpecifiedPage(pageNumber);
  }

  identifyBp(index: number, item : BookPreview) {
    return item.title;
  }
}
