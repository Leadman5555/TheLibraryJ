import {Inject, Injectable, PLATFORM_ID} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserAuthService} from './user-auth.service';
import {isPlatformBrowser} from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(@Inject(PLATFORM_ID) private platformId: object, private userAuthService: UserAuthService, private router: Router) {
  }


  canActivate(): boolean {
    if(isPlatformBrowser(this.platformId)){
      if (this.userAuthService.isLoggedIn()) return true;
      void this.router.navigate([''])
      return false;
    }
    return true;
  }
}
