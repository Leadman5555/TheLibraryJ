import {Component, OnInit} from '@angular/core';
import {UserAuthService} from '@app/user/account/userAuth/user-auth.service';
import {UserProfileService} from '@app/user/profile/user-profile.service';
import {BookPreview} from '@app/book/shared/models/book-preview';

@Component({
  selector: 'app-favourite-books',
  imports: [],
  templateUrl: './favourite-books.component.html',
  styleUrl: './favourite-books.component.css'
})
export class FavouriteBooksComponent implements OnInit {

  constructor(private userAuthService: UserAuthService, private userProfileService: UserProfileService) {
  }

  userLoggedIn: boolean = false;
  favouriteBooks?: BookPreview[];
  errorFetchingMessage!: string;

  ngOnInit(): void {
    this.userLoggedIn = this.userAuthService.isLoggedIn();
    if(this.userLoggedIn){
      const email = this.userAuthService.getLoggedInEmail();
      if(!email) return;
      this.userProfileService.getFavouriteBooksForUser(email).subscribe({
        next: (v) => this.favouriteBooks = v,
        error: (error: string) => this.errorFetchingMessage = error
      });
    }else {
      this.userProfileService.getFavouriteBooksForDevice().subscribe({
        next: (v) => this.favouriteBooks = v,
        error: (error: string) => this.errorFetchingMessage = error
      });
    }
  }

}
