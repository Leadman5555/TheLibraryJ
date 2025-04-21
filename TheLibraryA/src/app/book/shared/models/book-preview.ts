import {BookTag} from './BookTag';
import {BookState} from './BookState';

export interface BookPreview {
  title : string;
  chapterCount: number;
  averageRating: number;
  ratingCount: number;
  id: string;
  bookState: BookState;
  bookTags: BookTag[];
  coverImageUrl: string;
}
