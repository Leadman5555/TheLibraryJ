import {Component, inject, OnDestroy, OnInit} from '@angular/core';
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {BookFilterComponent} from "../book-filter/filterBox/book-filter.component";
import {BookPreviewCardComponent} from "../book-preview-card/book-preview-card.component";
import {TimesMaxPipe} from "../../shared/pipes/times-max.pipe";
import {BookPreview} from '../book-preview';
import {ActivatedRoute, Router} from '@angular/router';
import {BookService} from '../book-service';
import {HttpParams} from '@angular/common/http';
import {BookTag} from '../BookTag';
import {BookPage} from '../../home/home/paging/book-page';
import {BookFilterService} from '../book-filter/filterService/book-filter.service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-book-view',
  imports: [
    BookFilterComponent,
    BookPreviewCardComponent,
    NgForOf,
    NgIf
  ],
  templateUrl: './book-view.component.html',
  styleUrl: './book-view.component.css'
})
export class BookViewComponent implements OnInit, OnDestroy {
  bookPreviews: BookPreview[] = [];
  private bookService: BookService = inject(BookService);
  private filterSubscription!: Subscription;

  constructor(private activatedRoute: ActivatedRoute, private filterService: BookFilterService) {}

  tagsFromRedirect : string[] = [];

  ngOnInit(): void {
    this.tagsFromRedirect = this.activatedRoute.snapshot.paramMap.getAll('hasTags');
    this.filterService.onTagsRedirect(this.tagsFromRedirect);
    //add first fetch sort based on tags from rout params
    this.filterSubscription =  this.filterService.currentForm$.subscribe(outcome => {
      if(outcome.isValid) {
        if(outcome.sortAsc !== undefined){
          let compareF;
          if(outcome.sortAsc) compareF = (a : BookPreview, b : BookPreview) => a.averageRating - b.averageRating;
          else compareF = (a : BookPreview, b : BookPreview) => b.averageRating - a.averageRating;
          this.bookPreviews = this.bookPreviews.filter(outcome.predicate).sort(compareF);
        }else{
          this.bookPreviews = this.bookPreviews.filter(outcome.predicate);
        }
      }else this.bookService.getBookPreviewsByParams(outcome.params).subscribe({
        next: (v) => this.bookPreviews = v,
        error: (_) => console.log("Error fetched data"),
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
