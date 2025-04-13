import {CanActivate} from '@angular/router';
import {Injectable} from '@angular/core';
import {UserAuthService} from './user-auth.service';
import {Observable, tap} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthorGuard implements CanActivate {
  constructor(private userAuthService: UserAuthService) {
  }

  canActivate(): Observable<boolean> {
    return this.userAuthService.canAuthor().pipe(
      tap(value => {
        if (!value) {
          alert('User account must be at least 24 hours old to access the Author tab.');
        }
      })
    );
  }

}
