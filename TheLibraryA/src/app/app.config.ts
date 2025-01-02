import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {provideClientHydration, withEventReplay} from '@angular/platform-browser';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withFetch,
  withInterceptors,
  withInterceptorsFromDi
} from '@angular/common/http';
import {csrfInterceptor} from './interceptors/csrf.interceptor';
import {jwtInterceptor} from './interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}), provideRouter(routes), provideClientHydration(withEventReplay())
    , provideHttpClient(
      withFetch(),
      withInterceptors([csrfInterceptor, jwtInterceptor]),
    //  withInterceptorsFromDi()
    ),
   // {provide: HTTP_INTERCEPTORS, useClass: CsrfInterceptor, multi: true},
  ]
};
