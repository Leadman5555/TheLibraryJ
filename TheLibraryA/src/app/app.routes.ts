import { Routes } from '@angular/router';
import { HomeComponent} from './home/home/home.component';
import {BookComponent} from './book/book/book.component';
import {ChapterComponent} from './book/chapter/chapter.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  {
    path: 'book',
    component: BookComponent,
    // pathMatch: 'prefix',
    // children : [
    //   {path:  'chapter/fetch', component : ChapterComponent },
    //   {path : ':title', component : BookComponent},
    // ]
  },
  {path : 'book/:title', component : BookComponent},
  {path:  'book/chapter/:bookId/:chapterNumber', component : ChapterComponent },
  {path: '*', component: HomeComponent}
];
