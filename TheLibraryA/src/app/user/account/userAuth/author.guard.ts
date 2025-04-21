import {CanActivate} from '@angular/router';
import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {UserAuthService} from './user-auth.service';
import {Observable, of, tap} from 'rxjs';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthorGuard implements CanActivate {
  constructor(@Inject(PLATFORM_ID) private platformId: object, private userAuthService: UserAuthService) {
  }

  canActivate(): Observable<boolean> {
    if(isPlatformBrowser(this.platformId)){
      return this.userAuthService.canAuthor().pipe(
        tap(value => {
          if (!value) {
            alert('User account must be at least 24 hours old to access the Author tab.');
          }
        })
      );
    }
    return of(true);
  }

}
