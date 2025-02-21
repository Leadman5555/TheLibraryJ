import {Injectable} from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import {catchError, Observable, of, Subscriber, switchMap, throwError} from 'rxjs';
import {UserAuthService} from '../user/account/userAuth/user-auth.service';
import {StorageService} from '../shared/storage/storage.service';


@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private readonly REFRESH_DELAY_MS: number = 1000;

  constructor(
    private userAuthService: UserAuthService,
    private storageService: StorageService
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (req.url.includes('/na/')) return next.handle(req);
    return next.handle(req.clone({headers: req.headers.set('Authorization', `Bearer ${this.storageService.getAccessToken()}`),}))
      .pipe(catchError((error) => {
          if (
            error instanceof HttpErrorResponse &&
            error.status === 403 &&
            error.error?.errorDetails?.message === 'Jwt token expired'
          ) {
            if (this.isRefreshing) {
              return new Observable((observer: Subscriber<HttpRequest<any>>) => {
                const sleep = setInterval(() => {
                  if (!this.isRefreshing) {
                    clearInterval(sleep);
                    observer.next(
                      req.clone({
                        headers: req.headers.set('Authorization', `Bearer ${this.storageService.getAccessToken()}`),
                      })
                    );
                    observer.complete();
                  }
                }, this.REFRESH_DELAY_MS);
              }).pipe(
                switchMap((updatedReq: HttpRequest<any>) => next.handle(updatedReq))
              );
            }
            return this.handleTokenExpired().pipe(
              switchMap((tokenRefreshed) => {
                if (tokenRefreshed) {
                  return next.handle(
                    req.clone({
                      headers: req.headers.set('Authorization', `Bearer ${this.storageService.getAccessToken()}`),
                    })
                  );
                }
                return throwError(() => error);
              })
            );
          }
          return throwError(() => error);
        })
      );
  }


// @ts-ignore: Suppress deprecation warnings
  private handleTokenExpired(): Observable<boolean> {
    this.isRefreshing = true;
    if (this.storageService.isLoggedIn()) {
      return this.userAuthService.refreshAccessToken().pipe(
        switchMap((event) => {
          const refreshedJwt: string | null = event.headers.get('access_token');
          if (refreshedJwt) {
            this.storageService.setAccessToken(refreshedJwt);
            this.isRefreshing = false;
            return of(true);
          } else {
            this.userAuthService.sendLogOutEvent()
            this.isRefreshing = false;
            alert('Session expired, please log in again.');
            return of(false);
          }
        }),
        catchError((_) => {
          this.userAuthService.sendLogOutEvent()
          this.isRefreshing = false;
          return of(false);
        })
      );
    }
    this.isRefreshing = false;
    return of(false);
  }
}

export const jwtInterceptorProvider = [
  {provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true},
];
