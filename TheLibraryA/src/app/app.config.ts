import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core';
import {provideRouter} from '@angular/router';
import {routes} from './app.routes';
import {provideClientHydration, withEventReplay} from '@angular/platform-browser';
import {provideHttpClient, withFetch, withInterceptorsFromDi} from '@angular/common/http';
import {jwtInterceptorProvider} from './interceptors/jwt.interceptor';
import {provideAnimations} from '@angular/platform-browser/animations';
import {provideServerRendering} from '@angular/platform-server';
import {provideServerRouting, withAppShell} from '@angular/ssr';
import {serverRoutes} from '@app/app.routes.server';
import {HeaderComponent} from '@app/header/header.component';

export const appConfig: ApplicationConfig = {
  providers: [provideZoneChangeDetection({eventCoalescing: true}), provideRouter(routes), provideClientHydration(withEventReplay())
    , provideHttpClient(
      withFetch(),
      withInterceptorsFromDi()
    ),
    provideServerRendering(),
    provideServerRouting(serverRoutes, withAppShell(HeaderComponent)),
    provideAnimations(),
    jwtInterceptorProvider
  ]
};
