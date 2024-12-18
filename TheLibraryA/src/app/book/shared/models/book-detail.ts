import {ChapterPreview} from './chapter-preview';
import {RatingResponse} from './rating-response';

export interface BookDetail {
  author: string;
  description: string;
  chapterPreviews: ChapterPreview[];
}
