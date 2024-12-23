import {Component, inject, OnInit} from '@angular/core';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../shared/book-service';
import {ChapterContent} from '../shared/models/chapter-content';
import {channel} from 'node:diagnostics_channel';
import {BookPreview} from '../shared/models/book-preview';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-chapter',
  imports: [
    NgIf
  ],
  templateUrl: './chapter.component.html',
  styleUrl: './chapter.component.css'
})
export class ChapterComponent implements OnInit {
  bookId!: string;
  bookTitle!: string;
  chapterContent!: ChapterContent;
  chapterNumber! : number;
  private bookService: BookService = inject(BookService);
  private readonly bookNav: string = '/book';
  private readonly failureNav: string = '';

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    const params = this.activatedRoute.snapshot.paramMap;
    const chapterNumber= params.get('chapterNumber');
    const bookId = params.get('bookId');
    const bookTitle = params.get('title');
    if (!chapterNumber || !bookId)
      if(bookTitle) this.router.navigate([this.bookNav, bookTitle]);
      else this.router.navigate([this.failureNav]);
    this.chapterNumber = +chapterNumber!;
    this.bookId = bookId!;
    this.bookTitle = bookTitle!;
    this.bookService.getChapterContentByNumber(bookId!, this.chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
      },
      error: (e) => {
        console.error(e);
        this.router.navigate([this.bookNav, bookTitle]);
      }
    })
  }

  fetchChapter(chapterNumber: number) {
    if(chapterNumber <= 0) this.router.navigate([this.bookNav, this.bookTitle]);
    this.bookService.getChapterContentByNumber(this.bookId, chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
        this.chapterNumber = chapterNumber;
      },
      error: (_) => {
        this.router.navigate([this.bookNav, this.bookTitle]);
      }
    })
  }
}
