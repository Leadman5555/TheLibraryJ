import {Component, inject, OnInit} from '@angular/core';
import {BookPreview} from '../../book/book-preview';
import {BookService} from '../../book/book-service';

@Component({
  selector: 'app-home',
  imports: [],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private bookService: BookService = inject(BookService);
  bookPreviews!: BookPreview[];

  ngOnInit() {
      this.bookService.getBookPreviews().subscribe({
        next: (v) => this.bookPreviews = v,
        error: (e) => console.error(e)
      })
  }
}
