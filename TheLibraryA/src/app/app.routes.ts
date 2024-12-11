import { Routes } from '@angular/router';
import { HomeComponent} from './home/home/home.component';
import {BookComponent} from './book/book/book.component';
import {ChapterComponent} from './book/chapter/chapter.component';
import {BookFilterComponent} from './book/book-filter/filterBox/book-filter.component';
import {BookViewComponent} from './book/book-view/book-view.component';

export const routes: Routes = [
  { path: '', component: HomeComponent},
  {
    path: 'book',
    component: BookComponent
  },
  {path : 'book/:title', component : BookComponent},
  {path:  'book/chapter/:bookId/:chapterNumber', component : ChapterComponent },
  {path: 'filter', component:  BookViewComponent},
  {path: '*', component: HomeComponent}
];
