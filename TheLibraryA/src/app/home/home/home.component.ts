import {Component, inject, OnInit} from '@angular/core';
import {BookPreview} from '../../book/book-preview';
import {BookService} from '../../book/book-service';
import {ActivatedRoute, Router} from '@angular/router';
import {BookTag} from '../../book/BookTag';
import {FilterByTagNamePipe} from '../../book/filter-by-tag-name.pipe'
import {
  AbstractControl,
  FormBuilder,
  FormGroup, NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {NgForOf, NgIf} from '@angular/common';

@Component({
  selector: 'app-home',
  imports: [
    FilterByTagNamePipe, ReactiveFormsModule, NgIf, NgForOf
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private bookService: BookService = inject(BookService);
  private router: Router = inject(Router);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  bookPreviews!: BookPreview[];
  allTags: BookTag[] = ['TAG1', 'TAG2', 'TAG3', 'TAG4', 'TAG5', 'TAG6', 'TAG7', 'TAG8','UNTAGGED']

  constructor(private fb : NonNullableFormBuilder) {}

  selectedTags: BookTag[] = [];
  keywordIncluded : string = '';
  orderByRating: string = '';
  minChapterCount : number = 0;

  public ratingOrderOptions : string[] = ['Descending','Ascending'];

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

  filterForm! : FormGroup;

  //FIX BUG WITH SORT ORDER + TAGS FILTER

  handleFilterSubmit(){
    this.selectedTags = this.getSelectedTags();
    this.keywordIncluded = this.filterForm.controls['titleContains'].value;
    this.orderByRating = this.filterForm.controls['ratingOrder'].value;
    this.minChapterCount = this.filterForm.controls['minChapterCount'].value;
  }

  resetFilters(){
    console.log('resetFilters');
    console.log(this.filterForm);
    this.filterForm.reset(this.defaultFormValues);
    console.log(this.filterForm);

    this.keywordIncluded = this.defaultFormValues.titleContains;
    this.orderByRating = this.defaultFormValues.ratingOrder;
    this.minChapterCount = this.defaultFormValues.minChapterCount;
    this.selectedTags = [];
  }

  getSelectedTags() : BookTag[] {
    const tagsGroup = this.filterForm.get('filterByTags');
    const result : BookTag[] = [];
    this.allTags.forEach(tag => {
      if(tagsGroup?.get(tag)?.value){
        result.push(tag);
      }
    });
    return result;
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const fromParams = params['tags'];
      if(fromParams){
        this.selectedTags = params['tags'];
      }
    });
    this.createFilterForm();
    this.bookService.getBookPreviews().subscribe({
      next: (v) => this.bookPreviews = v,
      error: (e) => console.error(e)
    });
  }

  createFilterForm(){
    this.filterForm= this.fb.group({
      titleContains: [this.defaultFormValues.titleContains, [Validators.maxLength(20), Validators.minLength(3)]],
      minChapterCount : [this.defaultFormValues.minChapterCount, Validators.min(0)],
      ratingOrder: [this.defaultFormValues.ratingOrder],
      filterByTags: this.createTagFilter()
    });
  }

  createTagFilter(){
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

  routeToBook(bookPreview: BookPreview) {
    this.router.navigate(['book'], { state: { bp:  bookPreview}});
  }
}
