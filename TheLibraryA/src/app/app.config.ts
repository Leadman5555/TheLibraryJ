import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {provideClientHydration, withEventReplay} from '@angular/platform-browser';
import {
  provideHttpClient,
  withFetch,
  withInterceptorsFromDi
} from '@angular/common/http';
import {jwtInterceptorProvider} from './interceptors/jwt.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}), provideRouter(routes), provideClientHydration(withEventReplay())
    , provideHttpClient(
      withFetch(),
      withInterceptorsFromDi()
    ),
   jwtInterceptorProvider
  ]
};
