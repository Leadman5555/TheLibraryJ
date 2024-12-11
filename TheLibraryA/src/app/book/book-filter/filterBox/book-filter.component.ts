import {Component, inject, OnInit, Output, Predicate} from '@angular/core';
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BookPreviewCardComponent} from '../../book-preview-card/book-preview-card.component';
import {FilterByTagNamePipe} from '../../filter-by-tag-name.pipe';
import {TimesMaxPipe} from '../../../shared/pipes/times-max.pipe';
import {ActivatedRoute, Router} from '@angular/router';
import {BookTag} from '../../BookTag';
import {HomeComponentStore} from '../../../home/home/home.component-store';
import {BookPreview} from '../../book-preview';
import EventEmitter from 'node:events';
import {HttpParams} from '@angular/common/http';
import {FormOutcome} from '../filterService/form-outcome';
import {BookFilterService} from '../filterService/book-filter.service';
import {min} from 'rxjs';

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

  constructor(private fb: NonNullableFormBuilder, private router: Router, private filterService : BookFilterService) {
  }

  readonly allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8', 'UNTAGGED'];

  defaultFormValues = {
    titleLike: '',
    minChapterCount: 0,
    ratingOrder: 'No order',
    minRating: 0,
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
  };

  filterForm!: FormGroup;

  handleFilterSubmit(): void {
    this.filterService.onFormSubmit(this.getFormOutcome());
    if (this.activatedRoute.snapshot.url.pop()?.path !== 'filter') this.router.navigate(['filter']);
  }

  resetFilters(): void {
    //fix no tag reset when param cons ?
    //fix tag filter checks for some
    //fix order rating does not update
    this.filterForm.reset();
    this.filterService.onFormSubmit(new FormOutcome(true));
  }

  private lastSubmittedData: any;

  getFormOutcome(): FormOutcome {
    const currentValues = this.filterForm.value;
    let result: FormOutcome = new FormOutcome(false);
    const titleLike: string = currentValues.titleLike;
    const lastTitle: string = this.lastSubmittedData['titleLike'];
    if (titleLike.startsWith(lastTitle)) result.and(bp => bp.title.startsWith(titleLike));
    else result.setInvalid();
    console.log("title " + result.isValid)

    const minChapterCount: number = currentValues.minChapterCount;
    if (result.isValid) {
      const lastCount: number = this.lastSubmittedData['minChapterCount'];
      if (minChapterCount > lastCount) result.and(bp => bp.chapterCount >= minChapterCount);
      else if(minChapterCount !== lastCount) result.setInvalid();
    }
    console.log("ch count  " + result.isValid)
    const minRating: number = currentValues.minRating;
    if (result.isValid) {
      const lastRating: number = this.lastSubmittedData['minRating'];
      if (minRating > lastRating) result.and(bp => bp.averageRating >= minRating);
      else if(minRating !== lastRating) result.setInvalid();
    }
    console.log("rating " + result.isValid)
    const selectedTags: BookTag[] = this.getSelectedTags(currentValues.filterByTags);
    if (result.isValid) {
      const lastTags: BookTag[] = this.getSelectedTags(this.lastSubmittedData.filterByTags);
      for (let tag of lastTags) {
        const b1: boolean = lastTags.includes(tag);
        const b2: boolean = selectedTags.includes(tag);
        if (!b1 || !b2) {
          if (b2) result.and(bp => bp.bookTags.includes(tag));
          else{
            result.setInvalid();
            break;
          }
        }
      }
    }
    console.log("tag " + result.isValid)

    const ratingOrder: string = currentValues.ratingOrder;
    if (ratingOrder !== 'No order') {
      console.log(result.sortAsc);
      result.sortAsc = (ratingOrder === 'A');
      console.log(result.sortAsc);
    }

    console.log("Valid" + result.isValid);
    if (!result.isValid) {
      if(titleLike !== this.defaultFormValues.titleLike) result.setParam('titleLike', titleLike);
      if(minRating !== this.defaultFormValues.minRating)result.setParam('minRating', minRating);
      if(minChapterCount !== this.defaultFormValues.minChapterCount) result.setParam('minChapters', minChapterCount);
      if (result.sortAsc !== undefined) result.setParam('ratingOrder', result.sortAsc);
      selectedTags.forEach(tag => result.appendParam('hasTags', tag));
    }
    this.lastSubmittedData = currentValues;

    return result;
  }

  private getSelectedTags(tagsGroup: any): BookTag[] {
    const result: BookTag[] = [];
    this.allTags.forEach(tag => {
      if (tagsGroup[tag]) {
        result.push(tag);
      }
    });
    return result;
  }

  ngOnInit(): void {
    this.createFilterForm();
    this.lastSubmittedData = this.filterForm.value;
  }

  createFilterForm() {
    this.filterForm = this.fb.group({
      titleLike: [this.defaultFormValues.titleLike, [Validators.maxLength(20), Validators.minLength(3)]],
      minChapterCount: [this.defaultFormValues.minChapterCount, Validators.min(0)],
      ratingOrder: this.defaultFormValues.ratingOrder,
      filterByTags: this.createTagFilter(),
      minRating: [this.defaultFormValues.minRating, [Validators.min(0), Validators.max(10)]],
    });
  }

  createTagFilter() {
    return this.fb.group({
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
  }

  identifyTag(index: number, item: BookTag) {
    return item;
  }
}
