import {Component, OnInit} from '@angular/core';
import {
  FormGroup,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule, Validators
} from "@angular/forms";
import {NgForOf, NgIf, NgOptimizedImage} from "@angular/common";
import {BookTag} from '../../shared/models/BookTag';
import {FormOutcome} from '../filterService/form-outcome';
import {BookFilterService} from '../filterService/book-filter.service';
import {Router} from '@angular/router';
import {integerValidator, stepValidator} from './filterValidators';

@Component({
  selector: 'app-book-filter',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule,
    NgOptimizedImage
  ],
  templateUrl: './book-filter.component.html',
  styleUrl: './book-filter.component.css'
})
export class BookFilterComponent implements OnInit {

  constructor(private fb: NonNullableFormBuilder, private router: Router, private filterService: BookFilterService) {
  }

  readonly allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8', 'UNTAGGED'];

  private readonly defaultFormValues = {
    titleLike: '',
    minChapters: 0,
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

  private wasIRedirected: boolean = false;

  filterForm!: FormGroup;

  handleFilterSubmit(): void {
    if (this.filterForm.pristine) return;
    const url = this.router.url;
    if (url.substring(url.lastIndexOf('/') + 1) !== 'filter') {
      this.filterService.onFormSubmit(this.getFormOutcomeRedirected());
      this.router.navigate(['filter']);
    } else this.filterService.onFormSubmit(this.getFormOutcome());
  }

  resetFilters(): void {
    if (!this.filterForm.pristine || this.wasIRedirected) this.filterService.onFormSubmit(new FormOutcome(true));
    this.wasIRedirected = false;
    this.filterForm.reset(this.defaultFormValues);
    this.lastSubmittedData = this.filterForm.value;
  }

  private lastSubmittedData: any;

  private getFormOutcome(): FormOutcome {
    const currentValues = this.filterForm.value;
    let result: FormOutcome = new FormOutcome(false);
    const titleLike: string = currentValues.titleLike.trim();
    const lastTitle: string = this.lastSubmittedData['titleLike'];
    if (titleLike.startsWith(lastTitle)) result.and(bp => bp.title.startsWith(titleLike));
    else result.setInvalid();

    const minChapters: number = currentValues.minChapters ?? 0;
    if (result.isValid) {
      const lastCount: number = this.lastSubmittedData['minChapters'];
      if (minChapters > lastCount) result.and(bp => bp.chapterCount >= minChapters);
      else if (minChapters !== lastCount) result.setInvalid();
    }

    const minRating: number = currentValues.minRating ?? 0;
    if (result.isValid) {
      const lastRating: number = this.lastSubmittedData['minRating'];
      if (minRating > lastRating) result.and(bp => bp.averageRating >= minRating);
      else if (minRating !== lastRating) result.setInvalid();
    }

    const selectedTags: BookTag[] = this.getSelectedTags(currentValues.filterByTags);
    if (result.isValid) {
      const lastTags: BookTag[] = this.getSelectedTags(this.lastSubmittedData.filterByTags);
      for (let tag of this.allTags) {
        const b1: boolean = lastTags.includes(tag);
        const b2: boolean = selectedTags.includes(tag);
        if (b1 !== b2) {
          if (b2) result.and(bp => bp.bookTags.includes(tag));
          else {
            result.setInvalid();
            break;
          }
        }
      }
    }

    const ratingOrder: string = currentValues.ratingOrder;
    if (ratingOrder !== 'No order') result.sortAsc = (ratingOrder === 'A');

    if (!result.isValid) {
      if (titleLike !== this.defaultFormValues.titleLike) result.setParam('titleLike', titleLike);
      if (minRating !== this.defaultFormValues.minRating) result.setParam('minRating', minRating);
      if (minChapters !== this.defaultFormValues.minChapters) result.setParam('minChapters', minChapters);
      if (result.sortAsc !== undefined) result.setParam('ratingOrder', result.sortAsc);
      selectedTags.forEach(tag => result.appendParam('hasTags', tag));
    }
    this.lastSubmittedData = currentValues;

    return result;
  }

  private getFormOutcomeRedirected(): FormOutcome {
    const currentValues = this.filterForm.value;
    let result: FormOutcome = new FormOutcome(true);
    const titleLike: string = currentValues.titleLike.trim();
    const minChapters: number = currentValues.minChapters ?? 0;
    const minRating: number = currentValues.minRating ?? 0;
    const selectedTags: BookTag[] = this.getSelectedTags(currentValues.filterByTags);
    const ratingOrder: string = currentValues.ratingOrder;

    if (titleLike !== this.defaultFormValues.titleLike) result.setRedirectTitleLike(titleLike);
    if (minRating !== this.defaultFormValues.minRating) result.setRedirectMinRating(minRating);
    if (minChapters !== this.defaultFormValues.minChapters) result.setRedirectChapterCount(minChapters);
    if (ratingOrder !== 'No order') result.setRedirectRatingOrder((ratingOrder === 'A'));
    if (selectedTags.length > 0) result.setRedirectTags(selectedTags);
    this.lastSubmittedData = currentValues;

    result.isRedirected = true;
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

    const redirectData: FormOutcome | undefined = this.filterService.getRedirectData();
    if (redirectData) {
      this.wasIRedirected = true;
      const redirectTags = redirectData.getRedirectTags();
      if (redirectTags) redirectTags.forEach(tag => {
        this.filterForm.patchValue({
          filterByTags: {
            [tag]: true
          }
        })
      });

      const redirectTitleLike = redirectData.getRedirectTitleLike();
      if (redirectTitleLike) this.filterForm.patchValue({titleLike: [redirectTitleLike]});

      const redirectChapterCount = redirectData.getRedirectChapterCount();
      if (redirectChapterCount) this.filterForm.patchValue({minChapters: [redirectChapterCount]});

      const redirectMinRating = redirectData.getRedirectMinRating();
      if (redirectMinRating) this.filterForm.patchValue({minRating: [redirectMinRating]});

      const redirectRatingOrder = redirectData.getRedirectRatingOrder();
      if (redirectRatingOrder !== undefined) this.filterForm.patchValue({ratingOrder: [redirectRatingOrder ? 'A' : 'D']});
    }

    this.lastSubmittedData = this.filterForm.value;
  }

  private createFilterForm() {
    this.filterForm = this.fb.group({
      titleLike: [this.defaultFormValues.titleLike, [Validators.maxLength(20), Validators.minLength(3), Validators.pattern('^[a-zA-Z\\s]*$')]],
      minChapters: [this.defaultFormValues.minChapters, [Validators.min(0), Validators.max(5000), integerValidator()]],
      ratingOrder: this.defaultFormValues.ratingOrder,
      filterByTags: this.createTagFilter(),
      minRating: [this.defaultFormValues.minRating, [Validators.min(0), Validators.max(10), stepValidator(3)]],
    });
  }

  private createTagFilter() {
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
