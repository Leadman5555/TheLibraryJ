import {Component, Input} from '@angular/core';
import {BookPreview} from '../shared/models/book-preview';
import {RouterLink} from '@angular/router';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-book-preview-card',
  imports: [
    NgIf,
    RouterLink
  ],
  templateUrl: './book-preview-card.component.html',
  standalone: true,
  styleUrl: './book-preview-card.component.css'
})
export class BookPreviewCardComponent {
  @Input() bookPreview! : BookPreview;
}
