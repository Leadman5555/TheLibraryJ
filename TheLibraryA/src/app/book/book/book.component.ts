import {Component, inject, OnInit} from '@angular/core';
import {BookService} from '../shared/book-service';
import {BookPreview} from '../shared/models/book-preview';
import {ActivatedRoute, ActivatedRouteSnapshot, Router, RouterLink} from '@angular/router';
import {BookDetail} from '../shared/models/book-detail';
import {BookResponse} from '../shared/models/book-response';
import {BookTag} from '../shared/models/BookTag';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {map} from 'rxjs';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-book',
  imports: [RouterLink, NgIf],
  templateUrl: './book.component.html',
  styleUrl: './book.component.css'
})
export class BookComponent implements OnInit {
  private bookService: BookService = inject(BookService);
  private activatedRoute: ActivatedRoute = inject(ActivatedRoute);
  private defaultRoute: string = '';
  bookPreview!: BookPreview;
  bookDetail!: BookDetail;

  constructor(private router: Router) {
    const navigation = this.router.getCurrentNavigation();
    if (navigation?.extras?.state) {
      const state = navigation.extras.state as { bp: BookPreview };
      this.bookPreview = state.bp;
    }
  }

  ngOnInit() {
    if (this.bookPreview) {
      this.bookService.getBookDetail(this.bookPreview.id).subscribe({
        next: (v) => this.bookDetail = v,
        error: (_) => this.router.navigate([`book`, this.bookPreview.title]),
      })
    } else {
      const title: string = this.activatedRoute.snapshot.params['title'];
      this.activatedRoute.paramMap.subscribe(params => {
        let v = params.get('title');
      })
      if (title) {
        this.bookService.getBook(title).subscribe({
          next: (v) => {
            this.bookPreview = {
              id: v.id,
              title: v.title,
              bookTags: v.bookTags,
              averageRating: v.averageRating,
              chapterCount: v.chapterCount,
              coverImage: v.coverImage,
              ratingCount: v.ratingCount,
              bookState: v.bookState,
            }
            this.bookDetail = {
              author: v.author,
              description: v.description,
              ratings: v.ratings,
              chapterPreviews: v.chapterPreviews
            }
          },
          error: (e) => {
            console.error(e);
            this.router.navigate([this.defaultRoute]);
          }
        })
      } else this.router.navigate([this.defaultRoute]);
    }
  }
}
