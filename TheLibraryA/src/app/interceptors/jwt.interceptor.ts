import {HttpEventType, HttpInterceptorFn} from '@angular/common/http';
import {tap} from 'rxjs';

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  if(req.url.includes('/na/')) return next(req);
  const token = localStorage.getItem('jwt-token');
  if(token) req = req.clone({headers: req.headers.set('Authorization', 'Bearer ' + token), withCredentials: true});
  return next(req).pipe(tap(event => {
    if(event.type === HttpEventType.Response) {
      const refreshedJwt = event.headers.get('Refreshed-jwt-token');
      if(refreshedJwt) localStorage.setItem('jwt-token', refreshedJwt);
    }
  }));
};
