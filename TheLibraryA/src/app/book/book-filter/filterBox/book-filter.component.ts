import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {allTags, allTagsAsString, BookTag} from '../../shared/models/BookTag';
import {ActivatedRoute, Router} from '@angular/router';
import {integerValidator, stepValidator} from './filterValidators';
import {NgOptimizedImage} from '@angular/common';

export type FilterSelection = {
  titleLike: string | null;
  minChapters: string | null;
  sortAscByRating: string | null;
  minRating: string | null;
  hasTags: BookTag[] | null;
};


@Component({
  selector: 'app-book-filter',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    NgOptimizedImage
  ],
  templateUrl: './book-filter.component.html',
  standalone: true,
  styleUrl: './book-filter.component.css'
})
export class BookFilterComponent implements OnInit {

  constructor(private nfb: FormBuilder,
              private router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  private readonly defaultFormValues = {
    titleLike: null,
    minChapters: null,
    sortAscByRating: null,
    minRating: null,
    filterByTags: Array.from(allTags, () => false)
  };

  private readonly defaultFilters: FilterSelection = {
    titleLike: null,
    minChapters: null,
    sortAscByRating: null,
    minRating: null,
    hasTags: null,
  };

  private newFilters: FilterSelection = {
    ...this.defaultFilters
  };

  private activeFilters: FilterSelection = {
    ...this.defaultFilters
  }

  filterForm!: FormGroup;

  get isFormPristine(): boolean {
    return this.filterForm.pristine;
  }

  handleFilterSubmit(): void {
    if (this.filterForm.pristine) return;
    if(this.shouldFilterAgain()){
      const url = this.router.url;
      if (url.substring(url.lastIndexOf('/') + 1, 7) !== 'filter'){
        //form not submitted on filter page, redirect
        this.redirectToFilterAndPushChanges();
      }else this.pushFilterChanges();
    } else this.filterForm.markAsPristine();
  }

  resetFilterForm(): void {
    this.filterForm.reset(this.defaultFormValues);
    this.clearFilters();
  }

  private shouldFilterAgain(): boolean {
    this.newFilters = {
      ...this.defaultFilters
    };
    const currentValues = this.filterForm.value;
    let anyValue: boolean = false;
    const titleLike: string | null = currentValues.titleLike?.trim();
    const minChapters: string | null = currentValues.minChapters;
    const minRating: string | null = currentValues.minRating;
    const selectedTags: BookTag[] = this.getSelectedTags(currentValues.filterByTags);
    const sortAscByRating: string | null = currentValues.sortAscByRating;

    const lastValueTitle = this.activeFilters.titleLike;
    if(lastValueTitle || titleLike){
      this.newFilters.titleLike = titleLike;
      if(lastValueTitle !== titleLike) anyValue = true;
    }

    const lastValueMinRating = this.activeFilters.minRating;
    if (lastValueMinRating || minRating) {
      this.newFilters.minRating = minRating;
      if(lastValueMinRating !== minRating) anyValue = true;
    }

    const lastValueMinChapters = this.activeFilters.minChapters;
    if (lastValueMinChapters || minChapters) {
      this.newFilters.minChapters = minChapters;
      if(lastValueMinChapters !== minChapters) anyValue = true;
    }

    const lastValueSortAscByRating = this.activeFilters.sortAscByRating;
    if (lastValueSortAscByRating || sortAscByRating) {
      this.newFilters.sortAscByRating = sortAscByRating;
      if(lastValueSortAscByRating !== sortAscByRating) anyValue = true;
    }

    const lastValueHasTags = this.activeFilters.hasTags;
    const anyTagsCurrently = selectedTags.length > 0;
    if (lastValueHasTags || anyTagsCurrently) {
      if(anyTagsCurrently){
        this.newFilters.hasTags = selectedTags;
        if(!this.areTheSameTagsSelected(lastValueHasTags, selectedTags)) anyValue = true;
      } else anyValue = true;
    }
    return anyValue;
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
      const tagIndex = allTagsAsString.findIndex(t => t === tag);
      if (tagIndex !== -1) tagsGroup.at(tagIndex).patchValue(true);
    });
  }

  private areTheSameTagsSelected(selectedTags1: BookTag[] | null, selectedTags2: BookTag[] | null): boolean {
    if (selectedTags1 === null || selectedTags2 === null) return selectedTags1 === selectedTags2;
    return selectedTags1.length === selectedTags2.length && selectedTags1.every((value, index) => value === selectedTags2[index]);
  }

  get filterByTags(): FormArray {
    return this.filterForm.get('filterByTags') as FormArray;
  }

  private redirectToFilterAndPushChanges(){
    this.activeFilters = this.newFilters;
    void this.router.navigate(['/filter'], {
      queryParams: this.activeFilters,
      queryParamsHandling: 'replace',
    });
  }

  private pushFilterChanges() {
    this.activeFilters = this.newFilters;
    void this.router.navigate([], {
      queryParams: this.activeFilters,
      queryParamsHandling: 'replace',
      replaceUrl: true,
      relativeTo: this.activatedRoute,
    });
  }

  private clearFilters(){
    this.activeFilters = this.defaultFilters;
    this.newFilters = this.defaultFilters;
    void this.router.navigate([], {
      queryParams: null,
      queryParamsHandling: 'replace',
      replaceUrl: true,
      relativeTo: this.activatedRoute,
    });
  }

  ngOnInit(): void {
    this.createFilterForm();
    const paramMap = this.activatedRoute.snapshot.queryParamMap;
    if (paramMap.keys.length > 0) {
      //page was redirected or reloaded
      const redirectTags = paramMap.getAll('hasTags');
      if (redirectTags.length > 0){
        this.patchTagValues(redirectTags, this.filterByTags);
        this.activeFilters.hasTags = redirectTags as BookTag[];
      }
      const redirectTitleLike = paramMap.get('titleLike');
      if (redirectTitleLike){
        this.filterForm.patchValue({titleLike: redirectTitleLike});
        this.activeFilters.titleLike = redirectTitleLike;
      }
      const redirectChapterCount = paramMap.get('minChapters');
      if (redirectChapterCount){
        this.filterForm.patchValue({minChapters: redirectChapterCount});
        this.activeFilters.minChapters = redirectChapterCount;
      }
      const redirectMinRating = paramMap.get('minRating');
      if (redirectMinRating){
        this.filterForm.patchValue({minRating: redirectMinRating});
        this.activeFilters.minRating = redirectMinRating;
      }
      const redirectRatingOrder = paramMap.get('sortAscByRating');
      if (redirectRatingOrder === 'true' || redirectRatingOrder === 'false'){
        this.filterForm.patchValue({sortAscByRating: redirectRatingOrder});
        this.activeFilters.sortAscByRating = redirectRatingOrder;
      }
      this.filterForm.markAsDirty();
    }
  }

  private createFilterForm() {
    this.filterForm = this.nfb.group({
      titleLike: [this.defaultFormValues.titleLike, [Validators.maxLength(20), Validators.minLength(3), Validators.pattern(/^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\s'_!.-]*$/)]],
      minChapters: [this.defaultFormValues.minChapters, [Validators.min(0), Validators.max(5000), integerValidator()]],
      sortAscByRating: this.defaultFormValues.sortAscByRating,
      filterByTags: this.nfb.array(
        this.defaultFormValues.filterByTags.map(
          (value: boolean) => this.nfb.control(value, { nonNullable: true })
        )
      ),
      minRating: [this.defaultFormValues.minRating, [Validators.min(0), Validators.max(10), stepValidator(3)]],
    });
  }

  protected readonly allTags = allTags;
}
