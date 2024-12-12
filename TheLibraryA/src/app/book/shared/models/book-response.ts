import {ChapterPreview} from './chapter-preview';
import {RatingResponse} from './rating-response';
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
  chapterPreviews: ChapterPreview[];
  ratings: RatingResponse[];
  bookTags: BookTag[],
  bookState: BookState;
  coverImage: string;
}
