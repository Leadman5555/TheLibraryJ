import {HttpInterceptorFn} from '@angular/common/http';

export const csrfInterceptor: HttpInterceptorFn = (req, next) => {
  if(req.url.includes('/na/')) return next(req);
  const match = document.cookie.match(new RegExp('XSRF-TOKEN=([^;]+)'));
  if(match && match.length > 0){
    return next(req.clone({ headers: req.headers.set('X-XSRF-TOKEN', match[1]), withCredentials: true}))
  }
  return next(req);
};
