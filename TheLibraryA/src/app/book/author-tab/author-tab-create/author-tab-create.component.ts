import { Component } from '@angular/core';
import { BookService } from '../../shared/book-service';
import {FormGroup, NonNullableFormBuilder, Validators} from '@angular/forms';

@Component({
  selector: 'app-author-tab-create',
  imports: [],
  templateUrl: './author-tab-create.component.html',
  styleUrl: './author-tab-create.component.css'
})
export class AuthorTabCreateComponent {
  constructor(private bookService: BookService, private fb: NonNullableFormBuilder) {
    this.bookCreationForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(40), Validators.pattern('^[a-zA-Z\\s\']*$')]],
      description: ['', [Validators.maxLength(700)]],
    })
  }

  bookCreationForm: FormGroup;

}
