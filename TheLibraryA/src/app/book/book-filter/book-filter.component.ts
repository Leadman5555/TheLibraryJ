import {Component, inject, OnInit} from '@angular/core';
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BookPreviewCardComponent} from '../book-preview-card/book-preview-card.component';
import {FilterByTagNamePipe} from '../filter-by-tag-name.pipe';
import {TimesMaxPipe} from '../../shared/pipes/times-max.pipe';
import {ActivatedRoute} from '@angular/router';
import {BookTag} from '../BookTag';
import {HomeComponentStore} from '../../home/home/home.component-store';
import {BookPreview} from '../book-preview';

@Component({
  selector: 'app-book-filter',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './book-filter.component.html',
  styleUrl: './book-filter.component.css'
})
export class BookFilterComponent implements OnInit {
  private readonly activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  constructor(private fb: NonNullableFormBuilder) {}

  readonly allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8', 'UNTAGGED']
  // selectedTags: BookTag[] = [];
  // keywordIncluded: string = '';
  // orderByRating: string = '';
  // minChapterCount: number = 0;

  defaultFormValues = {
    titleContains: '',
    minChapterCount: 0,
    ratingOrder: 'No order',
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
    //make it rout to a parent ? spit out? output? swap from home to search or stay if already on search;
  }

  resetFilters(): void {
    //fix no tag reset when param cons
    this.filterForm.reset();
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
      this.createFilterForm(params['tags']);
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

  identifyTag(index: number, item : BookTag) {
    return item;
  }
}
