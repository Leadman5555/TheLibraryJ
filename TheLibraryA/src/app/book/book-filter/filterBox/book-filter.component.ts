import {Component, OnInit} from '@angular/core';
import {FormGroup, FormsModule, NonNullableFormBuilder, ReactiveFormsModule, Validators} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {BookTag} from '../../shared/models/BookTag';
import {FormOutcome} from '../filterService/form-outcome';
import {BookFilterService} from '../filterService/book-filter.service';
import {ActivatedRoute, Router} from '@angular/router';
import {integerValidator, stepValidator} from './filterValidators';

@Component({
  selector: 'app-book-filter',
  imports: [
    FormsModule,
    NgForOf,
    NgIf,
    ReactiveFormsModule
  ],
  templateUrl: './book-filter.component.html',
  standalone: true,
  styleUrl: './book-filter.component.css'
})
export class BookFilterComponent implements OnInit {

  constructor(private fb: NonNullableFormBuilder,
              private router: Router,
              private filterService: BookFilterService,
              private activatedRoute: ActivatedRoute) {
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

  filterForm!: FormGroup;

  handleFilterSubmit(): void {
    if (this.filterForm.pristine) return;
    const url = this.router.url;
    if (url.substring(url.lastIndexOf('/') + 1, 7) !== 'filter') {
      //submit on home
      const outcome = this.getFormOutcomeRedirected();
      if (outcome !== null) {
        this.filterService.onFormSubmit(outcome);
        this.router.navigate(['filter']);
      } else this.filterForm.markAsPristine();
    } else {
      //submit on filter
      const outcome = this.getFormOutcome();
      if (outcome !== null) this.filterService.onFormSubmit(outcome);
      else this.filterForm.markAsPristine();
    }
  }

  resetFilters(): void {
    if (this.filterForm.pristine) return;
    if (!this.router.url.includes('/filter')) {
      this.filterForm.reset(this.defaultFormValues);
      this.filterForm.markAsPristine();
      return;
    }
    this.filterForm.reset(this.defaultFormValues);
    if (this.wasISubmitted) {
      if (this.wasIReallyChanged()) {
        this.filterService.onFormSubmit(new FormOutcome(true));
        this.wasISubmitted = false;
        this.lastSubmittedData = this.defaultFormValues;
      } else {
        this.filterForm.markAsPristine();
      }
    }
  }

  private wasIReallyChanged(): boolean {
    const values = this.filterForm.value;
    if (values.titleLike !== this.lastSubmittedData.titleLike) return true;
    if (values.minChapters !== this.lastSubmittedData.minChapters) return true;
    if (values.minRating !== this.lastSubmittedData.minRating) return true;
    const lastTags = this.getSelectedTags(this.lastSubmittedData.filterByTags);
    const currentTags = this.getSelectedTags(values.filterByTags);
    if (lastTags.length !== currentTags.length) return true;
    return !currentTags.every((value, index) => value === lastTags[index]);
  }

  private amINotDefault(values: any): boolean {
    if (values.titleLike !== this.defaultFormValues.titleLike) return true;
    if (values.minChapters !== this.defaultFormValues.minChapters) return true;
    if (values.minRating !== this.defaultFormValues.minRating) return true;
    if (values.ratingOrder !== this.defaultFormValues.ratingOrder) return true;
    return this.getSelectedTags(values.filterByTags).length > 0;
  }

  private wasISubmitted = false;
  private lastSubmittedData: any = this.defaultFormValues;

  private getFormOutcome(): FormOutcome | null {
    const currentValues = this.filterForm.value;
    if (!this.amINotDefault(currentValues)) return null;

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
    if (ratingOrder !== this.defaultFormValues.ratingOrder) result.sortAsc = (ratingOrder === 'A');

    if (!result.isValid) {
      if (titleLike !== this.defaultFormValues.titleLike) result.setParam('titleLike', titleLike);
      if (minRating !== this.defaultFormValues.minRating) result.setParam('minRating', minRating);
      if (minChapters !== this.defaultFormValues.minChapters) result.setParam('minChapters', minChapters);
      if (result.sortAsc !== undefined) result.setParam('ratingOrder', result.sortAsc);
      selectedTags.forEach(tag => result.appendParam('hasTags', tag));
    }
    this.wasISubmitted = true;
    this.lastSubmittedData = currentValues;

    return result;
  }

  private getFormOutcomeRedirected(): FormOutcome | null {
    const currentValues = this.filterForm.value;
    let result: FormOutcome = new FormOutcome(true);
    let anyValue: boolean = false;
    const titleLike: string = currentValues.titleLike.trim();
    const minChapters: number = currentValues.minChapters ?? 0;
    const minRating: number = currentValues.minRating ?? 0;
    const selectedTags: BookTag[] = this.getSelectedTags(currentValues.filterByTags);
    const ratingOrder: string = currentValues.ratingOrder;

    if (titleLike !== this.defaultFormValues.titleLike) {
      result.setRedirectTitleLike(titleLike);
      anyValue = true;
    }
    if (minRating !== this.defaultFormValues.minRating) {
      result.setRedirectMinRating(minRating);
      anyValue = true;
    }
    if (minChapters !== this.defaultFormValues.minChapters) {
      result.setRedirectChapterCount(minChapters);
      anyValue = true;
    }
    if (ratingOrder !== 'No order') {
      result.setRedirectRatingOrder((ratingOrder === 'A'));
      anyValue = true;
    }
    if (selectedTags.length > 0) {
      result.setRedirectTags(selectedTags);
      anyValue = true;
    }
    this.lastSubmittedData = currentValues;

    if (!anyValue) return null;
    this.wasISubmitted = true;
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
    const tagsFromRedirect = this.activatedRoute.snapshot.paramMap.getAll('hasTags');
    if (tagsFromRedirect.length > 0) {
      //redirect from book tag
      const formOutcome = new FormOutcome(true);
      formOutcome.isRedirected = true;
      formOutcome.setRedirectTags(tagsFromRedirect);
      tagsFromRedirect.forEach(tag => {
        this.filterForm.patchValue({
          filterByTags: {
            [tag]: true
          }
        })
      });
      this.lastSubmittedData = this.filterForm.value;
      this.wasISubmitted = true;
      this.filterForm.markAsDirty();
      this.filterService.onFormSubmit(formOutcome);
      return;
    }
    //redirect from home
    const redirectData = this.filterService.getRedirectData();
    if (redirectData) {
      this.filterService.refreshSelection(); //don't ask why, it works
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
      this.lastSubmittedData = this.filterForm.value;
      this.wasISubmitted = true;
      this.filterForm.markAsDirty();
    }else this.filterService.refreshSelection();
  }

  private createFilterForm() {
    this.filterForm = this.fb.group({
      titleLike: [this.defaultFormValues.titleLike, [Validators.maxLength(20), Validators.minLength(3), Validators.pattern('^[a-zA-Z\\s\']*$')]],
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

  identifyTag(_: number, item: BookTag) {
    return item;
  }
}
