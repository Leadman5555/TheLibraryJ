import {Component, inject} from '@angular/core';
import {BookPreview} from '../../book/shared/models/book-preview';
import {ReactiveFormsModule} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {BookPreviewCardComponent} from '../../book/book-preview-card/book-preview-card.component';
import {provideComponentStore} from '@ngrx/component-store';
import {HomeComponentStore} from './paging/home.component-store';
import {TimesMaxPagingPipe} from '../../shared/pipes/times-max-paging.pipe';
import {BookFilterComponent} from '../../book/book-filter/filterBox/book-filter.component';
import {PagingHelper} from '../../shared/paging/paging-helper';

@Component({
  selector: 'app-home',
  imports: [
    ReactiveFormsModule, NgIf, NgForOf, BookPreviewCardComponent, AsyncPipe, TimesMaxPagingPipe, BookFilterComponent
  ],
  providers: [
    provideComponentStore(HomeComponentStore)
  ],
  templateUrl: './home.component.html',
  standalone: true,
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
    console.log(window.innerWidth);
    console.log(window.devicePixelRatio);
    this.componentStore.loadNextPage();
  }

  onChosenPage(pageNumber: number){
    this.componentStore.loadSpecifiedPage(pageNumber);
  }

  identifyBp(_: number, item : BookPreview) {
    return item.title;
  }
}
