import {Pipe, PipeTransform} from '@angular/core';
import {BookPreview} from '../models/book-preview';
import {BookTag} from '../models/BookTag';

@Pipe({
  name: 'filterByTagAndName'
})
export class FilterByTagNamePipe implements PipeTransform {

  transform(bookPreviews: BookPreview[], activeTags: BookTag[], name: string, minChapterCount: number, order: string): BookPreview[] {
    if (!bookPreviews) return [];
    if(name.length === 0 && minChapterCount === 0 && activeTags.length === 0) return FilterByTagNamePipe.sortPreviews(bookPreviews, order);
    if(activeTags.length === 0){
      const filtered = bookPreviews.filter(preview => {
        return (preview.title.includes(name) && preview.chapterCount >= minChapterCount)
      });
      return FilterByTagNamePipe.sortPreviews(filtered, order);
    }
    const filtered = bookPreviews.filter(preview => {
      if (preview.title.includes(name) && preview.chapterCount >= minChapterCount) {
        return FilterByTagNamePipe.containsTags(preview.bookTags, activeTags)
      } else return false;
    });
    return FilterByTagNamePipe.sortPreviews(filtered, order);
  }

  private static sortPreviews(bookPreviews: BookPreview[], order: string): BookPreview[] {
    if (order === 'A') return bookPreviews.sort((a, b) => {
      if (a.averageRating < b.averageRating) {
        return -1;
      } else if (a.averageRating > b.averageRating) {
        return 1;
      } else {
        return 0;
      }
    });
    else if (order === 'D') return bookPreviews.sort((a, b) => {
      if (a.averageRating < b.averageRating) {
        return 1;
      } else if (a.averageRating > b.averageRating) {
        return -1;
      } else {
        return 0;
      }
    });
    return bookPreviews;
  }

  private static containsTags(pretenderTags: BookTag[], activeTags: BookTag[]): boolean {
    for (let i = 0; i < activeTags.length; i++) if (!pretenderTags.includes(activeTags[i])) return false;
    return true;
  }
}
