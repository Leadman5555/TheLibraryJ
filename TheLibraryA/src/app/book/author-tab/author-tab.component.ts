import {Component, OnInit} from '@angular/core';
import {BookService} from '../shared/book-service';
import {NavigationEnd, NavigationStart, Router, RouterLink, RouterOutlet} from '@angular/router';
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
    this.router.events.subscribe((event) => {
      if(event instanceof NavigationStart) this.showImage = false;
      else if(event instanceof NavigationEnd) this.showImage = this.router.url === '/author-tab';
    })
  }

  ngOnInit(): void {
    const username = this.userAuthService.getLoggedInUsername();
    const email = this.userAuthService.getLoggedInEmail();
    if(!username || !email){
      this.router.navigate(['']);
      return;
    }
    this.authorEmail = email;
    this.bookService.getBookPreviewsByAuthor(username).subscribe({
      next: (v) => this.bookPreviews = v,
      error: (_) => this.router.navigate([''])
    });
  }

  protected bookPreviews: BookPreview[] = [];
  private authorEmail!: string;

  moveToEdit(index: number) {
    this.bookService.getBookDetail(this.bookPreviews[index].id)
      .subscribe({
        next: (detail) => {
          this.authorTabDataService.setAuthorEmail(this.authorEmail)
          this.authorTabDataService.setCurrentBook(
            this.bookService.mergePreviewAndDetail(
              this.bookPreviews[index],
              detail
            )
          );
        },
        error: (_) => this.router.navigate(['/author-tab'])
      });
  }

  showImage: boolean = true;
}
