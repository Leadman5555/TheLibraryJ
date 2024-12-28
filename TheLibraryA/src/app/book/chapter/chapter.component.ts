import {Component, inject, OnInit} from '@angular/core';
import {ChapterPreview} from '../shared/models/chapter-preview';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {BookService} from '../shared/book-service';
import {ChapterContent} from '../shared/models/chapter-content';
import {channel} from 'node:diagnostics_channel';
import {BookPreview} from '../shared/models/book-preview';
import {NgForOf, NgIf, NgStyle} from '@angular/common';
import {FormGroup, NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {TimesMaxPagingPipe} from '../../shared/pipes/times-max-paging.pipe';
import {TimesMaxPipe} from '../../shared/pipes/times-max.pipe';

@Component({
  selector: 'app-chapter',
  imports: [
    NgIf,
    NgStyle,
    ReactiveFormsModule,
    NgForOf,
    TimesMaxPipe
  ],
  templateUrl: './chapter.component.html',
  styleUrl: './chapter.component.css'
})
export class ChapterComponent implements OnInit {
  bookId!: string;
  bookTitle!: string;
  chapterContent!: ChapterContent;
  chapterNumber! : number;

  readonly minFontSize : number = 10;
  readonly maxFontSize : number = 48;

  readonly fontFamilies : string[] = ['Roboto', 'Arial Narrow', 'Times New Roman','Bell MT', 'Verdana'];

  readonly minLineHeight : number = 7;
  readonly maxLineHeight : number = 20;

  readonly backgroundColors: string[] = ['#9cbbbd', 'cadetblue', 'darkgoldenrod', 'white', 'bisque','darkgrey', 'gray', '#527755'];

  private readonly defaultFormValues = {
    fontSize: 24,
    fontFamily: 'Bell MT',
    lineHeight: 1200,
    backgroundColor: '#9cbbbd',
    alignText: 'left'
  };

  showSettings : boolean = false;

  toggleSettings(){
    this.showSettings = !this.showSettings;
  }

  styleForm!: FormGroup;

  private bookService: BookService = inject(BookService);
  private readonly bookNav: string = '/book';
  private readonly failureNav: string = '';

  constructor(private router: Router, private activatedRoute: ActivatedRoute, private fb: NonNullableFormBuilder) {}

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
    });
    this.createFilterForm();
  }

  private createFilterForm() {
    this.styleForm = this.fb.group({
      fontSize: [this.defaultFormValues.fontSize],
      fontFamily: [this.defaultFormValues.fontFamily],
      lineHeight: [this.defaultFormValues.lineHeight/10],
      backgroundColor: [this.defaultFormValues.backgroundColor],
      alignText: [this.defaultFormValues.alignText],
    });
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

  routeToBook(){
    this.router.navigate([this.bookNav, this.bookTitle]);
  }

  identifyByValue(_: number, item: any) : any {
    return item;
  }
}
