import {RenderMode, ServerRoute} from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  {
    path: '',
    renderMode: RenderMode.Client
  },
  {
    path: 'register',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'oauth2Callback',
    renderMode: RenderMode.Prerender
  },
  {
    path: 'pavilion-of-glory',
    renderMode: RenderMode.Prerender
  },
  {
    path: '**',
    renderMode: RenderMode.Server,
  },
]
