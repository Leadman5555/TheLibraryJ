import { Routes } from '@angular/router';
import { HomeComponent} from './home/home/home.component';
import {BookComponent} from './book/book/book.component';
import {ChapterComponent} from './book/chapter/chapter.component';
import {BookFilterComponent} from './book/book-filter/book-filter.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'book',
    component: BookComponent,
    pathMatch: 'prefix',
    children : [
      {path:  'filter', component : BookFilterComponent },
    ]
  },
  {path : 'book/:title', component : BookComponent},
  {path:  'book/chapter/:bookId/:chapterNumber', component : ChapterComponent },
  {path: '*', component: HomeComponent}
];
