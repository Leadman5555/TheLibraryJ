import {Injectable} from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import {catchError, Observable, switchMap, throwError} from 'rxjs';
import {UserAuthService} from '../user/userAuth/user-auth.service';
import {EventData} from '../shared/eventBus/event.class';
import {EventBusService} from '../shared/eventBus/event-bus.service';
import {StorageService} from '../shared/storage/storage.service';


@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;

  constructor(
    private userAuthService: UserAuthService,
    private eventBusService: EventBusService,
    private storageService: StorageService
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes('/na/')) return next.handle(req);
    if(!this.storageService.isLoggedIn()) return throwError(() => "Protected route, no logged in user.");
    console.log("Adding token to request");
    req = req.clone({headers: req.headers.set('Authorization', 'Bearer ' + this.storageService.getAccessToken())});
    return next.handle(req).pipe(
      catchError((error) => {
        if (error instanceof HttpErrorResponse && error.status === 403 && error.error.error.message === 'Jwt token expired') {
          return this.handleTokenExpired(req, next);
        }
        return throwError(() => error);
      })
    );
  }

  private handleTokenExpired(request: HttpRequest<any>, next: HttpHandler) {
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      if (this.storageService.isLoggedIn()) {
        return this.userAuthService.refreshAccessToken().pipe(
          switchMap((event) => {
            this.isRefreshing = false;
            const refreshedJwt: string | undefined = event.headers.get('access_token');
            if(refreshedJwt) this.storageService.setAccessToken(refreshedJwt);
            else{
              this.eventBusService.emit(new EventData('logout', null));
              return throwError(() => "Access token missing");
            }
            return next.handle(request);
          }),
          catchError((error) => {
            this.isRefreshing = false;
            this.eventBusService.emit(new EventData('logout', null));
            return throwError(() => error);
          })
        );
      }
    }
    return next.handle(request);
  }
}

export const jwtInterceptorProvider = [
  {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
];
