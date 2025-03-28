import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../shared/book-service';
import {ChapterContent} from '../shared/models/chapter-content';
import {FormGroup, NonNullableFormBuilder, ReactiveFormsModule} from '@angular/forms';
import {TimesMaxPipe} from '@app/shared/pipes/times-max.pipe';
import {NgClass, NgStyle} from '@angular/common';

@Component({
  selector: 'app-chapter',
  imports: [
    ReactiveFormsModule,
    TimesMaxPipe,
    NgStyle,
    NgClass
  ],
  templateUrl: './chapter.component.html',
  standalone: true,
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
      if(bookTitle) void this.router.navigate([this.bookNav, bookTitle]);
      else void this.router.navigate([this.failureNav]);
    this.chapterNumber = +chapterNumber!;
    this.bookId = bookId!;
    this.bookTitle = bookTitle!;
    this.bookService.getChapterContentByNumber(bookId!, this.chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
      },
      error: (_) => {
        void this.router.navigate([this.bookNav, bookTitle]);
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
    if(chapterNumber <= 0) void this.router.navigate([this.bookNav, this.bookTitle]);
    this.bookService.getChapterContentByNumber(this.bookId, chapterNumber).subscribe({
      next: (v) => {
        this.chapterContent = v;
        this.chapterNumber = chapterNumber;
      },
      error: (_) => {
        void this.router.navigate([this.bookNav, this.bookTitle]);
      }
    })
  }

  routeToBook(){
    void this.router.navigate([this.bookNav, this.bookTitle]);
  }
}
