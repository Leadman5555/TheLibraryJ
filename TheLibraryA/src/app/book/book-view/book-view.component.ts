import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {NgForOf} from "@angular/common";
import {BookFilterComponent} from "../book-filter/filterBox/book-filter.component";
import {BookPreviewCardComponent} from "../book-preview-card/book-preview-card.component";
import {BookPreview} from '../shared/models/book-preview';
import {ActivatedRoute} from '@angular/router';
import {BookService} from '../shared/book-service';
import {BookFilterService} from '../book-filter/filterService/book-filter.service';
import {catchError, skip, Subscription} from 'rxjs';
import {handleError} from '../../shared/errorHandling/handleError';

@Component({
  selector: 'app-book-view',
  imports: [
    BookFilterComponent,
    BookPreviewCardComponent,
    NgForOf
  ],
  templateUrl: './book-view.component.html',
  standalone: true,
  styleUrl: './book-view.component.css'
})
export class BookViewComponent implements OnInit, OnDestroy {
  bookPreviews: BookPreview[] = [];
  private bookService: BookService = inject(BookService);
  private filterSubscription!: Subscription;

  constructor(private filterService: BookFilterService) {}

  ngOnInit(): void {
    this.filterSubscription =  this.filterService.currentForm$.pipe(skip(1), catchError(handleError)).subscribe(outcome => {
      if(outcome.isValid && !outcome.isRedirected) {
        if(outcome.sortAsc !== undefined){
          let compareF;
          if(outcome.sortAsc) compareF = (a : BookPreview, b : BookPreview) => a.averageRating - b.averageRating;
          else compareF = (a : BookPreview, b : BookPreview) => b.averageRating - a.averageRating;
          this.bookPreviews = this.bookPreviews.filter(outcome.predicate).sort(compareF);
        }else{
          this.bookPreviews = this.bookPreviews.filter(outcome.predicate);
        }
      }else this.bookService.getBookPreviewsByParams(outcome.params).subscribe({
        next: (v) => {
          this.bookPreviews = v
          for(let i = 0; i < 100; i++){
            this.bookPreviews.push(this.bookPreviews[i]);
          }
        }
      });
    });
  }


  ngOnDestroy(): void {
    if(this.filterSubscription) this.filterSubscription.unsubscribe();
  }

  identifyBp(index: number, item: BookPreview) {
    return item.title;
  }
}
