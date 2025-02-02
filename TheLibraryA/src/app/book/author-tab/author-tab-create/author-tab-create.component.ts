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
import {UserAuthService} from '../../../user/account/userAuth/user-auth.service';
import {BookResponse} from '../../shared/models/book-response';
import {NgForOf, NgIf} from '@angular/common';
import {ImageDropComponent} from '../../../shared/image-drop/image-drop.component';
import {allTags, identifyTag} from '../../shared/models/BookTag';
import {atLeastOneValidator} from '../../../shared/functions/atLeastOneValidator';
import {carriageReturnLengthValidator} from '../../../shared/functions/CarriageReturnLengthValidator';

@Component({
  selector: 'app-author-tab-create',
  imports: [
    ReactiveFormsModule,
    NgIf,
    ImageDropComponent,
    NgForOf
  ],
  templateUrl: './author-tab-create.component.html',
  styleUrl: './author-tab-create.component.css'
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
      title: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40), Validators.pattern('^(?=.*[a-zA-Z0-9]+)[a-zA-Z0-9\\s\'_\"!.-]*$')]],
      description: ['', [Validators.required, carriageReturnLengthValidator(50, 800), Validators.pattern(/^[^<>]*(?:[<>][^<>]*){0,9}$/)]],
      bookTags: this.fb.array(allTags.map(() => false), atLeastOneValidator()),
      coverImage: [null],
    });
  }

  authorEmail!: string;
  bookCreationForm!: FormGroup;

  getCoverImageControl(): FormControl {
    return this.bookCreationForm.get('coverImage') as FormControl;
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
  protected readonly identifyTag = identifyTag;
}
