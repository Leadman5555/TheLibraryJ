import {Component, OnInit} from '@angular/core';
import {BookService} from '../shared/book-service';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import {AuthorTabDataService} from './shared/author-tab-data.service';
import {BookPreview} from '../shared/models/book-preview';
import {UserAuthService} from '../../user/account/userAuth/user-auth.service';
import {BookPreviewCardComponent} from '../book-preview-card/book-preview-card.component';

@Component({
  selector: 'app-author-tab',
  imports: [
    RouterOutlet,
    RouterLink,
    BookPreviewCardComponent
  ],
  templateUrl: './author-tab.component.html',
  styleUrl: './author-tab.component.css'
})
export class AuthorTabComponent implements OnInit {

  constructor(private bookService: BookService, private authorTabDataService: AuthorTabDataService, private userAuthService: UserAuthService, private router: Router) {
  }

  ngOnInit(): void {
    this.bookService.getBookPreviewsByAuthor(this.userAuthService.getLoggedInUsername()).subscribe({
      next: (v) => this.bookPreviews = v,
      error: (_) => this.router.navigate([''])
    })
  }

  protected bookPreviews!: BookPreview[];

  moveToEdit(index: number) {
    this.bookService.getBookDetail(this.bookPreviews[index].id)
      .subscribe({
        next: (detail) => {
          this.authorTabDataService.setCurrentBook(
            this.bookService.mergePreviewAndDetail(
              this.bookPreviews[index],
              detail
            )
          );
          this.router.navigate(['edit']);
        },
        error: (_) => this.router.navigate(['/author-tab'])
      });
  }

}
