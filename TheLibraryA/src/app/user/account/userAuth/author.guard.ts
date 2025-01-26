import {CanActivate} from '@angular/router';
import {Injectable} from '@angular/core';
import {UserAuthService} from './user-auth.service';
import {catchError, map, Observable, of} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthorGuard implements CanActivate {
  constructor(private userAuthService: UserAuthService) {}

  canActivate(): Observable<boolean> {
    return this.userAuthService.canAuthor().pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

}
