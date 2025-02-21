import {CanActivate} from '@angular/router';
import {Injectable} from '@angular/core';
import {UserAuthService} from './user-auth.service';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthorGuard implements CanActivate {
  constructor(private userAuthService: UserAuthService) {
  }

  canActivate(): Observable<boolean> {
    return this.userAuthService.canAuthor();
  }

}
