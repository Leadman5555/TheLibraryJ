import {Component, inject} from '@angular/core';
import {BookPreview} from '../../book/book-preview';
import {
  ReactiveFormsModule
} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {BookPreviewCardComponent} from '../../book/book-preview-card/book-preview-card.component';
import {provideComponentStore} from '@ngrx/component-store';
import {HomeComponentStore} from './home.component-store';
import {TimesMaxPipe} from '../../shared/pipes/times-max.pipe';
import {BookFilterComponent} from '../../book/book-filter/book-filter.component';

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
export class HomeComponent {
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

  identifyPage(index: number, page: number) {
    return page;
  }
}
