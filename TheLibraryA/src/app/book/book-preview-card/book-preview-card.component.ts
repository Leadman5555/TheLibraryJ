import {Component, Input} from '@angular/core';
import {BookPreview} from '../shared/models/book-preview';
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

  constructor(private router: Router) {}

  navigateToBook(){
    this.router.navigate(['book'], {state: {bp: this.bookPreview}})
  }
}
