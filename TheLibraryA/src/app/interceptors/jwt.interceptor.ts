import {Injectable} from '@angular/core';
import {
  HTTP_INTERCEPTORS,
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest
} from '@angular/common/http';
import {
  catchError,
  Observable,
  of,
  Subscriber,
  switchMap,
  throwError
} from 'rxjs';
import {UserAuthService} from '../user/userAuth/user-auth.service';
import {EventData} from '../shared/eventBus/event.class';
import {EventBusService} from '../shared/eventBus/event-bus.service';
import {StorageService} from '../shared/storage/storage.service';


@Injectable()
export class JwtInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private readonly REFRESH_DELAY_MS: number = 1000;

  constructor(
    private userAuthService: UserAuthService,
    private eventBusService: EventBusService,
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
            this.eventBusService.emit(new EventData('logout', null));
            this.isRefreshing = false;
            return of(false);
          }
        }),
        catchError((_) => {
          this.eventBusService.emit(new EventData('logout', null));
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
