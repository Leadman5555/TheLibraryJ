import {Component, inject, OnInit} from '@angular/core';
import {BookPreview} from '../../book/book-preview';
import {BookService} from '../../book/book-service';
import {ActivatedRoute, Router} from '@angular/router';
import {BookTag} from '../../book/BookTag';
import {FilterByTagNamePipe} from '../../book/filter-by-tag-name.pipe'
import {
  FormGroup, NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {AsyncPipe, NgForOf, NgIf} from '@angular/common';
import {BookPreviewCardComponent} from '../../book/book-preview-card/book-preview-card.component';
import {provideComponentStore} from '@ngrx/component-store';
import {HomeComponentStore} from './home.component-store';

@Component({
  selector: 'app-home',
  imports: [
    FilterByTagNamePipe, ReactiveFormsModule, NgIf, NgForOf, BookPreviewCardComponent, AsyncPipe
  ],
  providers: [
    provideComponentStore(HomeComponentStore)
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  readonly allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8', 'UNTAGGED']
  private readonly componentStore = inject(HomeComponentStore);
  readonly vm$ = this.componentStore.vm$;

  constructor(private fb: NonNullableFormBuilder) {}

  selectedTags: BookTag[] = [];
  keywordIncluded: string = '';
  orderByRating: string = '';
  minChapterCount: number = 0;

  defaultFormValues = {
    titleContains: '',
    minChapterCount: 0,
    ratingOrder: '',
    filterByTags: {
      UNTAGGED: false,
      TAG1: false,
      TAG2: false,
      TAG3: false,
      TAG4: false,
      TAG5: false,
      TAG6: false,
      TAG7: false,
      TAG8: false,
    }
  }

  filterForm!: FormGroup;

  handleFilterSubmit(): void {
    this.selectedTags = this.getSelectedTags();
    this.keywordIncluded = this.filterForm.controls['titleContains'].value;
    this.orderByRating = this.filterForm.controls['ratingOrder'].value;
    this.minChapterCount = this.filterForm.controls['minChapterCount'].value;
  }

  resetFilters(): void {
    //fix no tag reset when param cons
    this.filterForm.reset();
    this.keywordIncluded = this.defaultFormValues.titleContains;
    this.orderByRating = this.defaultFormValues.ratingOrder;
    this.minChapterCount = this.defaultFormValues.minChapterCount;
    this.selectedTags = [];
  }

  getSelectedTags(): BookTag[] {
    const tagsGroup = this.filterForm.get('filterByTags');
    const result: BookTag[] = [];
    this.allTags.forEach(tag => {
      if (tagsGroup?.get(tag)?.value) {
        result.push(tag);
      }
    });
    return result;
  }

  ngOnInit(): void {
    this.createFilterForm();
    this.activatedRoute.queryParams.subscribe(params => {
      const fromParams = params['tags'];
      if (fromParams) {
        this.selectedTags = params['tags'];
        this.createFilterForm(this.selectedTags);
      } else this.createFilterForm();
    });
  }

  createFilterForm(selectedTags?: BookTag[]) {
    this.filterForm = this.fb.group({
      titleContains: [this.defaultFormValues.titleContains, [Validators.maxLength(20), Validators.minLength(3)]],
      minChapterCount: [this.defaultFormValues.minChapterCount, Validators.min(0)],
      ratingOrder: [this.defaultFormValues.ratingOrder],
      filterByTags: this.createTagFilter(selectedTags)
    });
  }

  createTagFilter(selectedTags?: BookTag[]) {
    if (!selectedTags) return this.fb.group({
      UNTAGGED: [this.defaultFormValues.filterByTags.UNTAGGED],
      TAG1: [this.defaultFormValues.filterByTags.TAG1],
      TAG2: [this.defaultFormValues.filterByTags.TAG2],
      TAG3: [this.defaultFormValues.filterByTags.TAG3],
      TAG4: [this.defaultFormValues.filterByTags.TAG4],
      TAG5: [this.defaultFormValues.filterByTags.TAG5],
      TAG6: [this.defaultFormValues.filterByTags.TAG6],
      TAG7: [this.defaultFormValues.filterByTags.TAG7],
      TAG8: [this.defaultFormValues.filterByTags.TAG8],
    });
    return this.fb.group({
      UNTAGGED: [selectedTags.includes('UNTAGGED') ? true : this.defaultFormValues.filterByTags.UNTAGGED],
      TAG1: [selectedTags.includes('TAG1') ? true : this.defaultFormValues.filterByTags.TAG1],
      TAG2: [selectedTags.includes('TAG2') ? true : this.defaultFormValues.filterByTags.TAG2],
      TAG3: [selectedTags.includes('TAG3') ? true : this.defaultFormValues.filterByTags.TAG3],
      TAG4: [selectedTags.includes('TAG4') ? true : this.defaultFormValues.filterByTags.TAG4],
      TAG5: [selectedTags.includes('TAG5') ? true : this.defaultFormValues.filterByTags.TAG5],
      TAG6: [selectedTags.includes('TAG6') ? true : this.defaultFormValues.filterByTags.TAG6],
      TAG7: [selectedTags.includes('TAG7') ? true : this.defaultFormValues.filterByTags.TAG7],
      TAG8: [selectedTags.includes('TAG8') ? true : this.defaultFormValues.filterByTags.TAG8],
    });
  }

  onPreviousPage(): void {
    //ADD handling so that > 0
    //add page size change?
    //figure out how would filters work with the page? and do filters stay enabled for next/prev page switch?
  }

  onNextPage(): void {
    //add handling so that > Max page from pageInfo
    this.componentStore.loadNextPage();
  }

}
