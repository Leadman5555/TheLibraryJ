import {Injectable} from '@angular/core';
import {CanActivate, Router} from '@angular/router';
import {UserAuthService} from './user-auth.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(private userAuthService: UserAuthService, private router: Router) {
  }


  canActivate(): boolean {
    if (this.userAuthService.isLoggedIn()) return true;
    void this.router.navigate([''])
    return false;
  }
}
