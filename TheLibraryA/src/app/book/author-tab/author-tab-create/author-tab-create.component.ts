import {Component} from '@angular/core';
import {BookService} from '../../shared/book-service';
import {
  FormArray,
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import {BookResponse} from '../../shared/models/book-response';
import {ImageDropComponent} from '@app/shared/image-drop/image-drop.component';
import {allTags} from '../../shared/models/BookTag';
import {atLeastOneValidator} from '@app/shared/functions/atLeastOneValidator';
import {carriageReturnLengthValidator} from '@app/shared/functions/carriageReturnLengthValidator';
import {imageFileTypeValidator} from '@app/shared/functions/fileTypeValidator';

@Component({
  selector: 'app-author-tab-create',
  imports: [
    ReactiveFormsModule,
    ImageDropComponent
  ],
  templateUrl: './author-tab-create.component.html',
  styleUrl: './author-tab-create.component.css',
  standalone: true,
})
export class AuthorTabCreateComponent {
  constructor(private bookService: BookService, private fb: NonNullableFormBuilder, private userAuthService: UserAuthService) {
    const email = this.userAuthService.getLoggedInEmail();
    if (!email) {
      window.location.replace('');
      return;
    }
    this.authorEmail = email;
    this.bookCreationForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40), Validators.pattern(/^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\s'_!.-]*$/)]],
      description: ['', [Validators.required, carriageReturnLengthValidator(50, 800)]],
      bookTags: this.fb.array(allTags.map(() => false), atLeastOneValidator()),
      coverImage: [null, imageFileTypeValidator()],
    });
  }

  authorEmail!: string;
  bookCreationForm!: FormGroup;

  getCoverImageControl(): FormControl {
    return this.bookCreationForm.get('coverImage') as FormControl;
  }

  get isFormPristine(): boolean {
    return this.bookCreationForm!.pristine;
  }

  bookCreationErrorMessage: string | null = null;
  createdBook: BookResponse | null = null;

  attemptBookCreation() {
    if (this.bookCreationForm.pristine || this.bookCreationForm.invalid) return;
    const formData = new FormData();
    const values = this.bookCreationForm.value;
    formData.set('title', values.title);
    formData.set('description', values.description);
    formData.set('tags', JSON.stringify(this.getSelectedTags()));
    formData.set('coverImage', values.coverImage);
    formData.set('authorEmail', this.authorEmail);
    this.bookCreationErrorMessage = null;
    this.bookService.createBook(formData)
      .subscribe({
        next: (bookResponse) => this.createdBook = bookResponse,
        error: (error) => this.bookCreationErrorMessage = error
      });
  }

  private getSelectedTags(): string[] {
    const selectedTags = this.bookCreationForm.get('bookTags') as FormArray;
    return allTags.filter(
      (_, index) => selectedTags.at(index).value
    );
  }

  resetForm() {
    this.bookCreationErrorMessage = null;
    this.bookCreationForm.reset();
  }

  protected readonly allTags = allTags;
}
