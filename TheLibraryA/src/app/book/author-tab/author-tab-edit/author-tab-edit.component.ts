import {Component, OnInit} from '@angular/core';
import {AuthorTabDataService} from '../shared/author-tab-data.service';
import {BookService} from '../../shared/book-service';
import {BookResponse} from '../../shared/models/book-response';
import {
  FormArray,
  FormControl,
  FormGroup,
  FormsModule,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {atLeastOneValidator} from '../../../shared/functions/atLeastOneValidator';
import {allTags, BookTag, identifyTag} from '../../shared/models/BookTag';
import {NgForOf, NgIf} from '@angular/common';
import {ImageDropComponent} from '../../../shared/image-drop/image-drop.component';
import {identifyByIndex} from '../../../shared/functions/indentify';
import {stateArray} from '../../shared/models/BookState';
import {carriageReturnLengthValidator} from '../../../shared/functions/CarriageReturnLengthValidator';

@Component({
  selector: 'app-author-tab-edit',
  imports: [
    NgIf,
    ReactiveFormsModule,
    NgForOf,
    ImageDropComponent,
    FormsModule
  ],
  templateUrl: './author-tab-edit.component.html',
  styleUrl: './author-tab-edit.component.css'
})
export class AuthorTabEditComponent implements OnInit {

  constructor(private bookService: BookService, private authorTabDataService: AuthorTabDataService, private fb: NonNullableFormBuilder) {
  }

  currentlyEditingBook!: BookResponse;
  updateBookFrom!: FormGroup;

  ngOnInit(): void {
    this.authorTabDataService.currentlyEditing$.subscribe(book => {
      this.changeCurrentlyEditingBook(book);
      sessionStorage.setItem('authorEmail', this.authorTabDataService.authorEmail);
      this.createForm();
    });
    if (this.currentlyEditingBook === undefined) {
      const storedBook = sessionStorage.getItem('currentlyEditingBook');
      const storedEmail = sessionStorage.getItem('authorEmail');
      if (storedBook !== null && storedEmail !== null) {
        this.currentlyEditingBook = JSON.parse(storedBook);
        this.authorTabDataService.authorEmail = storedEmail;
        this.createForm();
      } else window.location.replace('/author-tab');
    }
  }

  private defaultFormValues!: any;

  private createForm() {
    this.createDefaultFormValues();
    this.updateBookFrom = this.fb.group({
      title: [this.defaultFormValues.title, [Validators.minLength(6), Validators.maxLength(40), Validators.pattern('^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s\'_\"!.-]*$')]],
      description: [this.defaultFormValues.description, [carriageReturnLengthValidator(50, 800), Validators.pattern(/^[^<>]*(?:[<>][^<>]*){0,9}$/)]],
      bookTags: this.fb.array(this.defaultFormValues.bookTags, atLeastOneValidator()),
      editCoverImage: [false],
      coverImage: [this.defaultFormValues.coverImage],
      state: [this.defaultFormValues.state, Validators.required]
    });
  }

  private createDefaultFormValues() {
    this.defaultFormValues = {
      title: '',
      description: this.currentlyEditingBook.description,
      bookTags: allTags.map((tag: BookTag) => this.currentlyEditingBook.bookTags.includes(tag)),
      coverImage: null,
      state: this.currentlyEditingBook.bookState
    };
  }

  showChapterEdit: boolean = false;

  toggleChapterEdit() {
    this.showChapterEdit = !this.showChapterEdit;
  }

  bookUpdateErrorMessage: string | null = null;
  bookUpdatedSuccessfully: boolean = false;

  attemptBookUpdate() {
    if ((this.updateBookFrom.pristine && !this.updateBookFrom.get('editCoverImage')!.value) || this.updateBookFrom.invalid) return;
    const formData = new FormData();
    const values = this.updateBookFrom.value;
    let anyChange: boolean = false;

    const newTitle: string = values.title;
    if (newTitle.length > 0 && newTitle !== this.currentlyEditingBook.title) {
      formData.set('title', newTitle);
      anyChange = true;
    }

    const description: string = values.description;
    if (description.length > 0 && description !== this.currentlyEditingBook.description) {
      formData.set('description', description);
      anyChange = true;
    }

    const selectedTags: string[] = this.getSelectedTags();
    if (selectedTags.length !== this.currentlyEditingBook.bookTags.length ||
      !selectedTags.every((tag, index) => tag === this.currentlyEditingBook.bookTags[index])) {
      formData.set('bookTags', JSON.stringify(selectedTags));
      anyChange = true;
    }

    //resetCoverImage = true -> resets cover
    //resetCoverImage = false -> was new image sent ? change image : do nothing
    if (!values.editCoverImage) formData.set('resetCoverImage', "false");
    else {
      if (values.coverImage) {
        formData.set('resetCoverImage', "false");
        formData.set('coverImage', values.coverImage);
      } else formData.set('resetCoverImage', "true");
      anyChange = true;
    }

    if (values.state !== this.currentlyEditingBook.bookState) {
      formData.set('state', values.state);
      console.log(values.state);
      anyChange = true;
    }
    this.bookUpdateErrorMessage = null;
    this.bookUpdatedSuccessfully = false;
    if (anyChange) {
      console.log('sending')
      formData.set('authorEmail', this.authorTabDataService.authorEmail);
      formData.set('bookId', this.currentlyEditingBook.id);
      this.bookService.updateBook(formData)
        .subscribe({
          next: (bookResponse) => {
            this.changeCurrentlyEditingBook(bookResponse);
            this.bookUpdatedSuccessfully = true;
          },
          error: (error) => this.bookUpdateErrorMessage = error
        });
    }
  }

  private getSelectedTags(): string[] {
    const selectedTags = this.updateBookFrom.get('bookTags') as FormArray;
    return allTags.filter(
      (_, index) => selectedTags.at(index).value
    );
  }

  private changeCurrentlyEditingBook(book: BookResponse) {
    this.currentlyEditingBook = book;
    sessionStorage.setItem('currentlyEditingBook', JSON.stringify(this.currentlyEditingBook));
    this.createForm();
  }


  resetForm() {
    this.bookUpdateErrorMessage = null;
    this.bookUpdatedSuccessfully = false;
    this.updateBookFrom.reset();
  }

  protected readonly allTags = allTags;
  protected readonly identifyTag = identifyTag;

  getCoverImageControl() {
    return this.updateBookFrom.get('coverImage') as FormControl;
  }

  protected readonly identifyByIndex = identifyByIndex;
  protected readonly stateArray = stateArray;
}
