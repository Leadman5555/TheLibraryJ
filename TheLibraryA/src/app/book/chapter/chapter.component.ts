import {Component, inject, OnInit} from '@angular/core';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../shared/book-service';
import {ChapterContent} from '../shared/models/chapter-content';
import {channel} from 'node:diagnostics_channel';
import {BookPreview} from '../shared/models/book-preview';

@Component({
  selector: 'app-chapter',
  imports: [],
  templateUrl: './chapter.component.html',
  styleUrl: './chapter.component.css'
})
export class ChapterComponent implements OnInit {
  bookId!: string;
  chapterContent!: ChapterContent;
  chapterNumber! : number;
  private bookService: BookService = inject(BookService);
  private failureNav: string = '';

  constructor(private router: Router, private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    console.log('chapter');
    const params = this.activatedRoute.snapshot.paramMap;
    console.log(params);
    const chapterNumber= params.get('chapterNumber');
    const bookId = params.get('bookId');
    if (!chapterNumber || !bookId) this.router.navigate([this.failureNav]);
    this.chapterNumber = +chapterNumber!;
    this.bookService.getChapterContentByNumber(bookId!, this.chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
      },
      error: (e) => {
        console.error(e);
        this.router.navigate([this.failureNav]);
      }
    })
  }

  fetchChapter(chapterNumber: number) {
    if(chapterNumber <= 0) this.router.navigate([this.failureNav]);
    this.bookService.getChapterContentByNumber(this.bookId, chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
        this.chapterNumber = chapterNumber;
      },
      error: (e) => {
        console.error(e);
      }
    })
  }
}
