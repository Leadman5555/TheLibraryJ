import {Component, OnInit} from '@angular/core';
import {
  FormArray,
  FormGroup,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from "@angular/forms";
import {NgForOf, NgIf} from "@angular/common";
import {allTags, BookTag, identifyTag} from '../../shared/models/BookTag';
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

  private readonly defaultFormValues = {
    titleLike: '',
    minChapters: 0,
    ratingOrder: 'No order',
    minRating: 0,
    filterByTags: Array.from(Array(allTags.length), () => false)
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
    return !this.areTheSameTagsSelected(this.lastSubmittedData.filterByTags, values.filterByTags);
  }

  private amINotDefault(values: any): boolean {
    if (values.titleLike !== this.defaultFormValues.titleLike) return true;
    if (values.minChapters !== this.defaultFormValues.minChapters) return true;
    if (values.minRating !== this.defaultFormValues.minRating) return true;
    if (values.ratingOrder !== this.defaultFormValues.ratingOrder) return true;
    return this.areAnyTagsSelected(values.filterByTags);
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

    const selectedTags: boolean[] = currentValues.filterByTags;
    if (result.isValid) {
      const lastTags: boolean[] = this.lastSubmittedData.filterByTags;
      for (let i = 0; i < allTags.length; i++) {
        const b1: boolean = lastTags[i];
        const b2: boolean = selectedTags[i];
        if (b1 !== b2) {
          if (b2) result.and(bp => bp.bookTags.includes(allTags[i]));
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
      selectedTags.forEach((value, index) => {
        if (value) result.appendParam('hasTags', allTags[index])
      });
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

  private getSelectedTags(tagsGroup: FormArray): BookTag[] {
    return allTags.filter(
      (_, index) => tagsGroup.at(index)
    );
  }

  private areAnyTagsSelected(tagsGroupValue: boolean[]): boolean {
    for (let i = 0; i < tagsGroupValue.length; i++) if (tagsGroupValue[i]) return true;
    return false;
  }

  private patchTagValues(tagsToPatch: string[], tagsGroup: FormArray) {
    tagsToPatch.forEach(tag => {
      const tagIndex = allTags.findIndex(t => t === tag);
      if (tagIndex !== -1) tagsGroup.at(tagIndex).patchValue(true);
    });
  }

  private areTheSameTagsSelected(selectedTags1: boolean[], selectedTags2: boolean[]): boolean {
    return selectedTags1.length === selectedTags2.length && selectedTags1.every((value, index) => value === selectedTags2[index]);
  }

  get filterByTags(): FormArray {
    return this.filterForm.get('filterByTags') as FormArray;
  }

  ngOnInit(): void {
    this.createFilterForm();
    const tagsFromRedirect = this.activatedRoute.snapshot.paramMap.getAll('hasTags');
    if (tagsFromRedirect.length > 0) {
      //redirect from book tag
      const formOutcome = new FormOutcome(true);
      formOutcome.isRedirected = true;
      formOutcome.setRedirectTags(tagsFromRedirect);
      this.patchTagValues(tagsFromRedirect, this.filterByTags);
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
      if (redirectTags) this.patchTagValues(redirectTags, this.filterByTags);

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
    } else this.filterService.refreshSelection();
  }

  private createFilterForm() {
    this.filterForm = this.fb.group({
      titleLike: [this.defaultFormValues.titleLike, [Validators.maxLength(20), Validators.minLength(3), Validators.pattern('^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s\'_!.-]*$')]],
      minChapters: [this.defaultFormValues.minChapters, [Validators.min(0), Validators.max(5000), integerValidator()]],
      ratingOrder: this.defaultFormValues.ratingOrder,
      filterByTags: this.fb.array(Array.from(Array(allTags.length), (_, index) => this.defaultFormValues.filterByTags[index])),
      minRating: [this.defaultFormValues.minRating, [Validators.min(0), Validators.max(10), stepValidator(3)]],
    });
  }

  protected readonly allTags = allTags;
  protected readonly identifyTag = identifyTag;
}
