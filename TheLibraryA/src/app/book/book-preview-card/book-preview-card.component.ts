import {Component, inject, Input} from '@angular/core';
import {BookPreview} from '../book-preview';
import {Router} from '@angular/router';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-book-preview-card',
  imports: [
    NgIf
  ],
  templateUrl: './book-preview-card.component.html',
  styleUrl: './book-preview-card.component.css'
})
export class BookPreviewCardComponent {
  @Input() bookPreview! : BookPreview;

  routeToBook() {
    inject(Router).navigate(['book'], {state: {bp: this.bookPreview}});
  }
}
