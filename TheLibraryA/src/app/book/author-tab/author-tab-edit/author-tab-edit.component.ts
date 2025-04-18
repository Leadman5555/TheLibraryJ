import {Component, OnDestroy, OnInit} from '@angular/core';
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
import {atLeastOneValidator} from '@app/shared/functions/atLeastOneValidator';
import {allTags, BookTag} from '../../shared/models/BookTag';
import {ImageDropComponent} from '@app/shared/image-drop/image-drop.component';
import {stateArray} from '../../shared/models/BookState';
import {carriageReturnLengthValidator} from '@app/shared/functions/carriageReturnLengthValidator';
import {repeatValidator} from '@app/shared/functions/repeatValidator';
import {ChapterEditComponent} from './chapter-edit/chapter-edit.component';
import {imageFileTypeValidator} from '@app/shared/functions/fileTypeValidator';
import {Subscription} from 'rxjs';
import {NgOptimizedImage} from '@angular/common';

export const currentBookKey = 'currentlyEditingBook';
export const emailKey = 'authorEmail';

@Component({
  selector: 'app-author-tab-edit',
  imports: [
    ReactiveFormsModule,
    ImageDropComponent,
    FormsModule,
    ChapterEditComponent,
    NgOptimizedImage
  ],
  templateUrl: './author-tab-edit.component.html',
  styleUrl: './author-tab-edit.component.css',
  standalone: true,
})
export class AuthorTabEditComponent implements OnInit, OnDestroy {

  constructor(private bookService: BookService, private authorTabDataService: AuthorTabDataService, private fb: NonNullableFormBuilder) {
  }

  ngOnDestroy(): void {
    if(this.currentBookSubscription) this.currentBookSubscription.unsubscribe();
  }

  private currentBookSubscription!: Subscription;
  currentlyEditingBook!: BookResponse;
  updateBookFrom?: FormGroup;

  ngOnInit(): void {
    const book: BookResponse | null = this.authorTabDataService.getCurrentlyEditedBook();
    if (book !== null) {
      sessionStorage.setItem(emailKey, this.authorTabDataService.authorEmail);
      this.changeCurrentlyEditingBook(book);
    } else {
      const storedBook = sessionStorage.getItem(currentBookKey);
      const storedEmail = sessionStorage.getItem(emailKey);
      if (storedBook !== null && storedEmail !== null) {
        this.currentlyEditingBook = JSON.parse(storedBook);
        this.authorTabDataService.authorEmail = storedEmail;
        this.createForm();
      }
    }
    this.setCurrentlyEditingBookSubscription();
  }

  private defaultFormValues!: any;

  private setCurrentlyEditingBookSubscription(){
    this.currentBookSubscription = this.authorTabDataService.getCurrentlyEditingObservable().subscribe(
      newBook => {
        this.closeDeleteBookForm();
        this.hideChapterEditForm();
        if(newBook) this.changeCurrentlyEditingBook(newBook);
        else this.updateBookFrom = undefined;
      }
    )
  }

  private createForm() {
    this.createDefaultFormValues();
    this.updateBookFrom = this.fb.group({
      title: [this.defaultFormValues.title, [Validators.minLength(6), Validators.maxLength(40), Validators.pattern(/^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\s'_"!.-]*$/)]],
      description: [this.defaultFormValues.description, [carriageReturnLengthValidator(50, 800)]],
      bookTags: this.fb.array(this.defaultFormValues.bookTags, atLeastOneValidator()),
      editCoverImage: [false],
      coverImage: [this.defaultFormValues.coverImage, imageFileTypeValidator()],
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

  showChapterEditForm() {
    this.showChapterEdit = true;
  }

  hideChapterEditForm() {
    this.showChapterEdit = false;
  }

  bookUpdateErrorMessage: string | null = null;
  bookUpdatedSuccessfully: boolean = false;

  attemptBookUpdate() {
    if ((this.updateBookFrom!.pristine && !this.updateBookFrom!.get('editCoverImage')!.value) || this.updateBookFrom!.invalid) return;
    const formData = new FormData();
    const values = this.updateBookFrom!.value;
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
    //resetCoverImage = false -> was new image sent ? change image: do nothing
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
      formData.set(emailKey, this.authorTabDataService.authorEmail);
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
    const selectedTags = this.updateBookFrom!.get('bookTags') as FormArray;
    return allTags.filter(
      (_, index) => selectedTags.at(index).value
    );
  }

  private changeCurrentlyEditingBook(book: BookResponse) {
    this.currentlyEditingBook = book;
    sessionStorage.setItem(currentBookKey, JSON.stringify(this.currentlyEditingBook));
    this.createForm();
  }

  private clearCurrentEditingBook() {
    this.updateBookFrom = undefined;
    sessionStorage.removeItem(currentBookKey);
    sessionStorage.removeItem(emailKey);
  }

  deleteBookForm?: FormGroup;

  deleteBookSuccess: boolean = true;
  deleteBookConfirmation!: string;

  attemptBookDeletion() {
    if (this.deleteBookForm!.pristine || this.deleteBookForm!.invalid) return;
    const value: string = this.deleteBookForm!.value.confirmDelete;
    if (value != this.deleteBookConfirmation) return;
    this.deleteBookSuccess = true;
    this.bookService.deleteBook(this.currentlyEditingBook.id, this.authorTabDataService.authorEmail).subscribe({
        next: () => {
          this.clearCurrentEditingBook();
        },
        error: () => this.deleteBookSuccess = false
      }
    );
  }

  showDeleteBookForm() {
    this.deleteBookConfirmation = `I want to delete book titled \"${this.currentlyEditingBook.title}\". I acknowledge that this action is irreversible.`;
    this.deleteBookForm = this.fb.group({
      confirmDelete: ['', repeatValidator(this.deleteBookConfirmation)],
    });
  }

  closeDeleteBookForm() {
    this.deleteBookForm = undefined;
  }

  resetForm() {
    this.bookUpdateErrorMessage = null;
    this.bookUpdatedSuccessfully = false;
    this.updateBookFrom!.reset();
  }

  resetImageForm() {
    this.updateBookFrom!.get('coverImage')!.setValue(null);
  }

  protected readonly allTags = allTags;

  getCoverImageControl() {
    return this.updateBookFrom!.get('coverImage') as FormControl;
  }

  protected readonly stateArray = stateArray;
}
