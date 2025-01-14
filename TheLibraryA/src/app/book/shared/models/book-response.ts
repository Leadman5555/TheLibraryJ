import {BookState} from './BookState';
import {BookTag} from './BookTag';

export interface BookResponse {
  id : string,
  title: string;
  author: string;
  description: string;
  chapterCount: number;
  averageRating: number;
  ratingCount: number;
  bookTags: BookTag[],
  bookState: BookState;
  coverImage: string;
}
